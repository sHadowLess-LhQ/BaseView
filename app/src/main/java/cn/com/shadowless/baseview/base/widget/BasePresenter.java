package cn.com.shadowless.baseview.base.widget;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import cn.com.shadowless.baseview.lifecycle.BaseQuickLifecycle;

/**
 * The type Base presenter.
 *
 * @param <LD> the type parameter
 * @author sHadowLess
 */
public abstract class BasePresenter<LD extends BaseMutableLiveData> implements
        BaseQuickLifecycle {

    /**
     * The Observe lifecycle.
     */
    private final LifecycleOwner observeLifecycle;

    /**
     * Instantiates a new Base presenter.
     *
     * @param observeLifecycle the observe lifecycle
     */
    public BasePresenter(LifecycleOwner observeLifecycle) {
        this.observeLifecycle = observeLifecycle;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == setStopEvent()) {
            onTerminate(event);
            this.getLifecycle().removeObserver(this);
        }
    }

    @NonNull
    @Override
    public Lifecycle.Event setStopEvent() {
        return Lifecycle.Event.ON_DESTROY;
    }

    @NonNull
    @Override
    public LifecycleOwner getObserveLifecycleOwner() {
        return observeLifecycle;
    }

    /**
     * Gets mutable.
     *
     * @return the mutable
     */
    public abstract LD getMutable();
}
