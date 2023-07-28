package cn.com.shadowless.baseview.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

/**
 * 快速实现生命周期实现
 *
 * @author sHadowLess
 */
public abstract class BaseQuickLifecycleImpl implements BaseQuickLifecycle {

    /**
     * 注册类
     */
    private final LifecycleRegistry lifecycleRegistry;

    /**
     * 构造
     */
    public BaseQuickLifecycleImpl() {
        lifecycleRegistry = new LifecycleRegistry(this);
    }

    @Override
    public abstract void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event);

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    /**
     * 获取注册类
     *
     * @return the lifecycle registry
     */
    public LifecycleRegistry getLifecycleRegistry() {
        return lifecycleRegistry;
    }
}
