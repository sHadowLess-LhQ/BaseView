package cn.com.shadowless.baseview.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * 快速实现生命周期接口
 * 支持监听与被监听生命周期
 *
 * @author sHadowLess
 */
public interface BaseQuickLifecycle extends LifecycleEventObserver, LifecycleOwner {

    @Override
    void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event);

    @NonNull
    @Override
    Lifecycle getLifecycle();

    /**
     * Sets stop event.
     *
     * @return the stop event
     */
    @NonNull
    Lifecycle.Event setStopEvent();

    /**
     * On terminate.
     *
     * @param event the event
     */
    void onTerminate(Lifecycle.Event event);

    /**
     * Observe lifecycle lifecycle owner.
     *
     * @return the lifecycle owner
     */
    @NonNull
    LifecycleOwner observeLifecycle();

}
