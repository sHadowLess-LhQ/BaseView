package cn.com.shadowless.baseview.manager;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewbinding.ViewBinding;

import java.lang.ref.WeakReference;

/**
 * ViewModel对象资源管理
 *
 * @author sHadowLess
 */
public class VmObjManager<VB extends ViewBinding> {

    private WeakReference<Activity> currentActivityWeakRef;
    private WeakReference<Fragment> currentFragmentWeakRef;
    private WeakReference<LifecycleOwner> currentLifecycleOwnerWeakRef;
    private WeakReference<VB> currentViewBindingWeakRef;

    /**
     * Get current activity activity.
     *
     * @return the activity
     */
    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (currentActivityWeakRef != null) {
            currentActivity = currentActivityWeakRef.get();
        }
        return currentActivity;
    }

    /**
     * Set current activity.
     *
     * @param activity the activity
     */
    public void setCurrentActivity(Activity activity) {
        currentActivityWeakRef = new WeakReference<>(activity);
    }

    /**
     * Get current fragment fragment.
     *
     * @return the fragment
     */
    public Fragment getCurrentFragment() {
        Fragment currentFragment = null;
        if (currentFragmentWeakRef != null) {
            currentFragment = currentFragmentWeakRef.get();
        }
        return currentFragment;
    }

    /**
     * Set current fragment.
     *
     * @param fragment the fragment
     */
    public void setCurrentFragment(Fragment fragment) {
        currentFragmentWeakRef = new WeakReference<>(fragment);
    }

    /**
     * Get current fragment fragment.
     *
     * @return the fragment
     */
    public LifecycleOwner getCurrentLifecycleOwner() {
        LifecycleOwner currentLifecycleOwner = null;
        if (currentLifecycleOwnerWeakRef != null) {
            currentLifecycleOwner = currentLifecycleOwnerWeakRef.get();
        }
        return currentLifecycleOwner;
    }

    /**
     * Set current fragment.
     *
     * @param lifecycleOwner the lifecycleOwner
     */
    public void setCurrentLifecycleOwner(LifecycleOwner lifecycleOwner) {
        currentLifecycleOwnerWeakRef = new WeakReference<>(lifecycleOwner);
    }

    /**
     * Get current VB VB.
     *
     * @return the vb
     */
    public VB getCurrentViewBinding() {
        VB currentVB = null;
        if (currentViewBindingWeakRef != null) {
            currentVB = currentViewBindingWeakRef.get();
        }
        return currentVB;
    }

    /**
     * Set current VB.
     *
     * @param vb the vb
     */
    public void setCurrentViewBinding(VB vb) {
        currentViewBindingWeakRef = new WeakReference<>(vb);
    }

}
