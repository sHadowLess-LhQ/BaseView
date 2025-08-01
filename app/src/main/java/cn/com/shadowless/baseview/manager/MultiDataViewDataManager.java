package cn.com.shadowless.baseview.manager;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MultiDataViewDataManager implements LifecycleEventObserver {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final AtomicBoolean viewReady = new AtomicBoolean(false);
    private final AtomicBoolean isDestroyed = new AtomicBoolean(false);
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Map<DataKey<?>, DataState<?>> dataStates = new ConcurrentHashMap<>();
    private Lifecycle lifecycle;

    /**
     * 数据键接口，用于标识不同类型或同类型的不同数据
     *
     * @param <T> 数据类型
     */
    public interface DataKey<T> {
        /**
         * 获取键的名称，用于调试和日志
         *
         * @return 键名称
         */
        default String getName(T key) {
            return "";
        }
    }

    public class DataState<T> {
        private T data;
        private DataBindViewBinder<T> binder;
        private final AtomicBoolean dataReady = new AtomicBoolean(false);
        private final AtomicBoolean bound = new AtomicBoolean(false);
    }

    public interface DataBindViewBinder<T> {
        void bindWithViewBinding(@Nullable T data);
    }

    /**
     * 设置指定key的数据源
     */
    public <T> void setData(DataKey<T> key, T data) {
        if (isDestroyed.get()) return;
        lock.writeLock().lock();
        try {
            DataState<T> state = getDataState(key);
            state.data = data;
            state.dataReady.set(true);
            checkAndBind(state);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 设置指定key的绑定器
     */
    public <T> void setBinder(DataKey<T> key, DataBindViewBinder<T> binder) {
        lock.writeLock().lock();
        try {
            DataState<T> state = getDataState(key);
            state.binder = binder;
            checkAndBind(state);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 设置ViewBinding
     */
    @SuppressWarnings("unchecked")
    public void setViewBinding() {
        if (isDestroyed.get()) return;
        lock.writeLock().lock();
        try {
            viewReady.set(true);
            // 检查所有已注册的数据状态
            for (Map.Entry<DataKey<?>, DataState<?>> entry : dataStates.entrySet()) {
                checkAndBind((DataState<Object>) entry.getValue());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 绑定生命周期
     */
    public void bindLifecycle(LifecycleOwner lifecycle) {
        this.lifecycle = lifecycle.getLifecycle();
        this.lifecycle.addObserver(this);
    }

    private <T> DataState<T> getDataState(DataKey<T> key) {
        DataState<?> state = dataStates.get(key);
        if (state == null) {
            state = new DataState<>();
            dataStates.put(key, state);
        }
        return (DataState<T>) state;
    }

    private <T> void checkAndBind(DataState<T> state) {
        if (isDestroyed.get() || state.bound.get()) return;

        lock.readLock().lock();
        try {
            boolean canBind = state.dataReady.get() && viewReady.get() && state.binder != null;
            if (!canBind) return;
        } finally {
            lock.readLock().unlock();
        }

        // 使用CAS确保只执行一次绑定
        if (state.bound.compareAndSet(false, true)) {
            executeBinding(state);
        }
    }

    private <T> void executeBinding(DataState<T> state) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            performBinding(state);
        } else {
            handler.post(() -> performBinding(state));
        }
    }

    private <T> void performBinding(DataState<T> state) {
        lock.readLock().lock();
        try {
            if (viewReady.get() && state.binder != null) {
                state.binder.bindWithViewBinding(state.data);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 重置指定key的状态，用于重新绑定特定数据
     */
    public <T> void resetDataState(DataKey<T> key) {
        if (isDestroyed.get()) return;
        lock.writeLock().lock();
        try {
            DataState<?> state = dataStates.get(key);
            if (state != null) {
                state.dataReady.set(false);
                state.bound.set(false);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 重置所有状态
     */
    public void resetAllDataState() {
        if (isDestroyed.get()) return;
        lock.writeLock().lock();
        try {
            for (DataState<?> state : dataStates.values()) {
                state.dataReady.set(false);
                state.bound.set(false);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 重置状态，用于重新绑定
     */
    public void reset() {
        if (isDestroyed.get()) return;
        lock.writeLock().lock();
        try {
            if (lifecycle != null) {
                lifecycle.removeObserver(this);
            }
            lifecycle = null;
            viewReady.set(false);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 检查指定key的数据是否已绑定完成
     */
    public boolean isBindingCompleted(DataKey<?> key) {
        DataState<?> state = dataStates.get(key);
        return state != null && state.bound.get();
    }

    /**
     * 检查所有数据是否都已绑定完成
     */
    public boolean isAllBindingCompleted() {
        lock.readLock().lock();
        try {
            for (DataState<?> state : dataStates.values()) {
                if (!state.bound.get()) {
                    return false;
                }
            }
            return !dataStates.isEmpty();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            lock.writeLock().lock();
            try {
                if (lifecycle != null) {
                    lifecycle.removeObserver(this);
                }
                isDestroyed.set(true);
                dataStates.clear();
                lifecycle = null;
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
}
