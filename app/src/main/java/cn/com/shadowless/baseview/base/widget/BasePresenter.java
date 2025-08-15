package cn.com.shadowless.baseview.base.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewbinding.ViewBinding;

import cn.com.shadowless.baseview.event.UpdateObjEvent;
import cn.com.shadowless.baseview.lifecycle.BaseQuickLifecycle;
import cn.com.shadowless.baseview.manager.VmObjManager;

/**
 * The type Base presenter.
 *
 * @param <LD> the type parameter
 * @author sHadowLess
 */
public abstract class BasePresenter<LD extends BaseMutableLiveData> implements
        BaseQuickLifecycle, UpdateObjEvent {

    /**
     * The Observe lifecycle.
     */
    private LifecycleOwner observeLifecycle;

    /**
     * Instantiates a new Base presenter.
     *
     * @param observeLifecycle the observe lifecycle
     */
    public BasePresenter(@NonNull LifecycleOwner observeLifecycle) {
        this.observeLifecycle = observeLifecycle;
        this.observeLifecycle.getLifecycle().addObserver(this);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            onTerminate();
            this.getLifecycle().removeObserver(this);
            this.observeLifecycle = null;
        }
    }

    @Nullable
    @Override
    public LifecycleOwner getObserveLifecycleOwner() {
        return observeLifecycle;
    }

    /**
     * Gets mutable.
     *
     * @return the mutable
     */
    @Nullable
    public abstract LD getMutable();

    @Override
    public void update(@NonNull VmObjManager<? extends ViewBinding> manager) {
        this.getLifecycle().removeObserver(this);
        this.observeLifecycle = null;
        this.observeLifecycle = manager.getCurrentLifecycleOwner();
        this.getLifecycle().addObserver(this);
        UpdateObjEvent.super.update(manager);
    }
}
