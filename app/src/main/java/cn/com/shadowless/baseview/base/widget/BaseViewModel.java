package cn.com.shadowless.baseview.base.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.viewbinding.ViewBinding;

import cn.com.shadowless.baseview.event.UpdateObjEvent;
import cn.com.shadowless.baseview.event.ViewModelEvent;
import cn.com.shadowless.baseview.lifecycle.BaseQuickLifecycle;
import cn.com.shadowless.baseview.manager.MultiDataViewDataManager;
import cn.com.shadowless.baseview.manager.VmObjManager;

/**
 * The type Base view model.
 *
 * @param <VB> the type parameter
 * @param <LD> the type parameter
 * @author sHadowLess
 */
public abstract class BaseViewModel<VB extends ViewBinding, LD extends BaseMutableLiveData> extends ViewModel
        implements ViewModelEvent, BaseQuickLifecycle, UpdateObjEvent {

    private VmObjManager<VB> manager;

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            onTerminate();
            this.getLifecycle().removeObserver(this);
        }
    }

    @Nullable
    @Override
    public LifecycleOwner getObserveLifecycleOwner() {
        return manager.getCurrentLifecycleOwner();
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
        this.manager = (VmObjManager<VB>) manager;
        this.getLifecycle().addObserver(this);
        UpdateObjEvent.super.update(manager);
    }

    public final VmObjManager<VB> getObjManager() {
        return manager;
    }

    public MultiDataViewDataManager getViewDataManager() {
        return null;
    }
}
