package cn.com.shadowless.baseview.base.widget;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import cn.com.shadowless.baseview.manager.MultiDataViewDataManager;

/**
 * The type Base view model.
 *
 * @param <VB> the type parameter
 * @param <LD> the type parameter
 * @author sHadowLess
 */
public abstract class BaseMutualViewModel<VB extends ViewBinding, LD extends BaseMutableLiveData> extends BaseViewModel<VB, LD> {

    private MultiDataViewDataManager manager;

    @Override
    public void onModelCreated() {
        if (manager == null) {
            manager = new MultiDataViewDataManager();
        }
        manager.reset();
        manager.resetAllDataState();
        manager.bindLifecycleOwner(this);
    }

    @NonNull
    @Override
    public final MultiDataViewDataManager getViewDataManager() {
        return manager;
    }
}
