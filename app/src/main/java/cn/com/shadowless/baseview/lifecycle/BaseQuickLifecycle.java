package cn.com.shadowless.baseview.lifecycle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
     */
    void onTerminate();

    /**
     * Observe lifecycle lifecycle owner.
     *
     * @return the lifecycle owner
     */
    @Nullable
    LifecycleOwner getObserveLifecycleOwner();

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
