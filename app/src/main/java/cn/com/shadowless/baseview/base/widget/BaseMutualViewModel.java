package cn.com.shadowless.baseview.base.widget;

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
        manager = new MultiDataViewDataManager();
        manager.reset();
        manager.resetAllDataState();
        manager.bindLifecycle(this);
    }

    @Override
    public MultiDataViewDataManager getViewDataManager() {
        return manager;
    }
}
