package cn.com.shadowless.baseview.lifecycle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * 快速实现生命周期接口
 * <p>
 * 提供了便捷的生命周期管理功能，实现此接口的类可以方便地监听和响应生命周期事件。
 * 继承了LifecycleEventObserver和LifecycleOwner接口，提供了完整的生命周期管理能力。
 * </p>
 *
 * @author sHadowLess
 */
public interface BaseQuickLifecycle extends LifecycleEventObserver, LifecycleOwner {

    /**
     * 当生命周期状态改变时调用
     * <p>
     * 当关联的LifecycleOwner的生命周期状态发生改变时会调用此方法。
     * 实现类可以在此方法中处理相应的生命周期事件。
     * </p>
     *
     * @param source 触发生命周期变化的LifecycleOwner
     * @param event  发生的生命周期事件
     */
    @Override
    void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event);

    /**
     * 当对象终止时调用
     * <p>
     * 在对象被销毁前调用，用于执行清理操作。
     * </p>
     */
    void onTerminate();

    /**
     * 获取观察生命周期的LifecycleOwner
     * <p>
     * 返回用于观察生命周期的LifecycleOwner实例。
     * </p>
     *
     * @return LifecycleOwner实例，如果不存在则返回null
     */
    @Nullable
    LifecycleOwner getObserveLifecycleOwner();

    /**
     * 获取Lifecycle实例
     * <p>
     * 返回关联的Lifecycle实例，用于注册生命周期观察者和查询当前状态。
     * </p>
     *
     * @return Lifecycle实例
     * @throws IllegalStateException 如果LifecycleOwner为null则抛出异常
     */
    @NonNull
    @Override
    default Lifecycle getLifecycle() {
        LifecycleOwner owner = getObserveLifecycleOwner();
        if (owner == null) {
            throw new IllegalStateException("LifecycleOwner is null, this instance may have been destroyed");
        }
        return owner.getLifecycle();
    }
}