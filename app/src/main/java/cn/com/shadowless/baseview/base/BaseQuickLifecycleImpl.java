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
    private LifecycleRegistry lifecycleRegistry;

    /**
     * The Lifecycle.
     */
    private Lifecycle observeLifecycle;

    /**
     * 构造
     */
    public BaseQuickLifecycleImpl() {
        init();
    }

    /**
     * 构造
     *
     * @param observeLifecycle the observe lifecycle
     */
    public BaseQuickLifecycleImpl(Lifecycle observeLifecycle) {
        this.observeLifecycle = observeLifecycle;
        observeLifecycle.addObserver(this);
        init();
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        Lifecycle.Event setStopEvent = setStopEvent();
        if (event == setStopEvent) {
            onTerminate(setStopEvent);
            getObserveLifecycle().removeObserver(this);
        }
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    /**
     * Init.
     */
    private void init() {
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    /**
     * Gets observe lifecycle.
     *
     * @return the observe lifecycle
     */
    public Lifecycle getObserveLifecycle() {
        return observeLifecycle;
    }

    /**
     * 获取注册类
     *
     * @return the lifecycle registry
     */
    public LifecycleRegistry getLifecycleRegistry() {
        return lifecycleRegistry;
    }

    /**
     * On state destroy.
     */
    protected void onTerminate(Lifecycle.Event event) {
        getLifecycleRegistry().handleLifecycleEvent(event);
    }

    /**
     * Sets stop event.
     *
     * @return the stop event
     */
    @NonNull
    protected abstract Lifecycle.Event setStopEvent();
}
