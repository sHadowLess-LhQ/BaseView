package cn.com.shadowless.baseview.manager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewbinding.ViewBinding;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ViewDataManager<T, VB extends ViewBinding> implements
        LifecycleEventObserver {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final AtomicBoolean dataReady = new AtomicBoolean(false);
    private final AtomicBoolean viewReady = new AtomicBoolean(false);
    private final AtomicBoolean bindingExecuted = new AtomicBoolean(false);
    private final AtomicBoolean isDestroyed = new AtomicBoolean(false);
    private final Handler handler = new Handler(Looper.getMainLooper());

    private T data;
    private VB viewBinding;
    private DataBindViewBinder<T, VB> binder;
    private Lifecycle lifecycle;

    public interface DataBindViewBinder<T, VB> {
        void bindWithViewBinding(@Nullable T data, @NonNull VB viewBinding);
    }

    /**
     * 设置数据源
     */
    public void setData(T data) {
        if (isDestroyed.get()) return;
        lock.writeLock().lock();
        try {
            this.data = data;
            Log.e("TAG", "setData: 数据准备就绪");
            dataReady.set(true);
            checkAndBind();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 设置ViewBinding
     */
    public void setViewBinding(VB viewBinding) {
        if (isDestroyed.get()) return;
        lock.writeLock().lock();
        try {
            this.viewBinding = viewBinding;
            Log.e("TAG", "setViewBinding: 视图准备就绪");
            viewReady.set(true);
            checkAndBind();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 设置绑定器
     */
    public void setBinder(DataBindViewBinder<T, VB> binder) {
        lock.writeLock().lock();
        try {
            this.binder = binder;
            checkAndBind();
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

    /**
     * 检查并执行绑定
     */
    private void checkAndBind() {
        if (isDestroyed.get() || bindingExecuted.get()) return;
        // 使用读锁检查条件
        lock.readLock().lock();
        try {
            boolean canBind = dataReady.get() && viewReady.get() && binder != null;
            if (!canBind) return;
        } finally {
            lock.readLock().unlock();
        }
        // 使用CAS确保只执行一次
        if (bindingExecuted.compareAndSet(false, true)) {
            executeBinding();
        }
    }

    /**
     * 执行绑定操作
     */
    private void executeBinding() {
        if (Thread.currentThread().getName().equals("main")) {
            performBinding();
        } else {
            handler.post(this::performBinding);
        }
    }

    private void performBinding() {
        lock.readLock().lock();
        try {
            if (viewBinding != null && binder != null) {
                binder.bindWithViewBinding(data, viewBinding);
            }
        } finally {
            lock.readLock().unlock();
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
            dataReady.set(false);
            viewReady.set(false);
            bindingExecuted.set(false);
            data = null;
            viewBinding = null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 检查是否已绑定完成
     */
    public boolean isBindingCompleted() {
        return bindingExecuted.get();
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            this.lifecycle.removeObserver(this);
            isDestroyed.set(true);
            lock.writeLock().lock();
            try {
                data = null;
                viewBinding = null;
                binder = null;
                lifecycle = null;
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
}