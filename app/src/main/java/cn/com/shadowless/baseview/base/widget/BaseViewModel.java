package cn.com.shadowless.baseview.base.widget;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.viewbinding.ViewBinding;

import cn.com.shadowless.baseview.event.ViewModelEvent;
import cn.com.shadowless.baseview.lifecycle.BaseQuickLifecycle;
import cn.com.shadowless.baseview.manager.MultiDataViewDataManager;

/**
 * The type Base view model.
 *
 * @param <VB> the type parameter
 * @param <LD> the type parameter
 * @author sHadowLess
 */
public abstract class BaseViewModel<VB extends ViewBinding, LD extends BaseMutableLiveData> extends ViewModel
        implements ViewModelEvent, BaseQuickLifecycle {

    /**
     * The View binding.
     */
    private VB viewBinding;

    /**
     * The Activity.
     */
    @SuppressLint("StaticFieldLeak")
    private Activity activity;

    /**
     * The Fragment.
     */
    private Fragment fragment;

    /**
     * The Observe lifecycle.
     */
    private LifecycleOwner observeLifecycle;

    /**
     * Gets binding view.
     *
     * @return the binding view
     */
    protected VB getBindView() {
        return viewBinding;
    }

    /**
     * Sets view binding.
     *
     * @param viewBinding the view binding
     */
    public void setBindView(VB viewBinding) {
        this.viewBinding = viewBinding;
    }

    /**
     * Sets owner.
     *
     * @param owner the owner
     */
    public void setOwner(LifecycleOwner owner) {
        this.observeLifecycle = owner;
        this.observeLifecycle.getLifecycle().addObserver(this);
    }

    /**
     * Sets activity.
     *
     * @param activity the activity
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * Gets activity.
     *
     * @return the activity
     */
    protected Activity getActivity() {
        return activity;
    }

    /**
     * Sets fragment.
     *
     * @param fragment the fragment
     */
    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     * Gets fragment.
     *
     * @return the fragment
     */
    protected Fragment getFragment() {
        return fragment;
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

    public MultiDataViewDataManager getViewDataManager() {
        return null;
    }
}
