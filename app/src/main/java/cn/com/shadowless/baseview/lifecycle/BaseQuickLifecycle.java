package cn.com.shadowless.baseview.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * 快速实现生命周期接口
 * 支持监听生命周期
 *
 * @author sHadowLess
 */
public interface BaseQuickLifecycle extends LifecycleEventObserver, LifecycleOwner {

    @Override
    void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event);

    /**
     * On terminate.
     *
     * @param event the event
     */
    void onTerminate(@NonNull Lifecycle.Event event);

    /**
     * Observe lifecycle lifecycle owner.
     *
     * @return the lifecycle owner
     */
    @NonNull
    LifecycleOwner getObserveLifecycleOwner();

    @NonNull
    @Override
    default Lifecycle getLifecycle() {
        return getObserveLifecycleOwner().getLifecycle();
    }
}
