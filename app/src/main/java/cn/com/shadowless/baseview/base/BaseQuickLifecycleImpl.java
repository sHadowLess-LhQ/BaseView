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
    private LifecycleOwner observeLifecycle;

    /**
     * The Set stop event.
     */
    private Lifecycle.Event setStopEvent;

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
    public BaseQuickLifecycleImpl(LifecycleOwner observeLifecycle) {
        init();
        this.observeLifecycle = observeLifecycle;
        this.setStopEvent = setStopEvent();
        observeLifecycle.getLifecycle().addObserver(this);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == setStopEvent) {
            onTerminate(event);
            getObserveLifecycle().getLifecycle().removeObserver(this);
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
        getLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        getLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_START);
        getLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    /**
     * Gets observe lifecycle.
     *
     * @return the observe lifecycle
     */
    public LifecycleOwner getObserveLifecycle() {
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
     *
     * @param event the event
     */
    protected void onTerminate(Lifecycle.Event event) {
        getLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        getLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        getLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }

    /**
     * Sets stop event.
     *
     * @return the stop event
     */
    @NonNull
    protected abstract Lifecycle.Event setStopEvent();
}
