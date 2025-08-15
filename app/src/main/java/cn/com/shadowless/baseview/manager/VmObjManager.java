package cn.com.shadowless.baseview.manager;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewbinding.ViewBinding;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ViewModel对象资源管理
 *
 * @author sHadowLess
 */
public class VmObjManager<VB extends ViewBinding> {

    private volatile WeakReference<Activity> currentActivityWeakRef;
    private volatile WeakReference<Fragment> currentFragmentWeakRef;
    private volatile WeakReference<LifecycleOwner> currentLifecycleOwnerWeakRef;
    private volatile WeakReference<VB> currentViewBindingWeakRef;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    /**
     * Get current activity.
     *
     * @return the activity
     */
    public Activity getCurrentActivity() {
        readLock.lock();
        try {
            return currentActivityWeakRef != null ? currentActivityWeakRef.get() : null;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Set current activity.
     *
     * @param activity the activity
     */
    public void setCurrentActivity(Activity activity) {
        writeLock.lock();
        try {
            if (currentActivityWeakRef != null) {
                currentActivityWeakRef.clear();
            }
            currentActivityWeakRef = activity != null ? new WeakReference<>(activity) : null;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Get current fragment.
     *
     * @return the fragment
     */
    public Fragment getCurrentFragment() {
        readLock.lock();
        try {
            return currentFragmentWeakRef != null ? currentFragmentWeakRef.get() : null;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Set current fragment.
     *
     * @param fragment the fragment
     */
    public void setCurrentFragment(Fragment fragment) {
        writeLock.lock();
        try {
            if (currentFragmentWeakRef != null) {
                currentFragmentWeakRef.clear();
            }
            currentFragmentWeakRef = fragment != null ? new WeakReference<>(fragment) : null;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Get current lifecycle owner.
     *
     * @return the lifecycle owner
     */
    public LifecycleOwner getCurrentLifecycleOwner() {
        readLock.lock();
        try {
            return currentLifecycleOwnerWeakRef != null ? currentLifecycleOwnerWeakRef.get() : null;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Set current lifecycle owner.
     *
     * @param lifecycleOwner the lifecycle owner
     */
    public void setCurrentLifecycleOwner(LifecycleOwner lifecycleOwner) {
        writeLock.lock();
        try {
            if (currentLifecycleOwnerWeakRef != null) {
                currentLifecycleOwnerWeakRef.clear();
            }
            currentLifecycleOwnerWeakRef = lifecycleOwner != null ?
                    new WeakReference<>(lifecycleOwner) : null;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Get current view binding.
     *
     * @return the view binding
     */
    public VB getCurrentViewBinding() {
        readLock.lock();
        try {
            return currentViewBindingWeakRef != null ? currentViewBindingWeakRef.get() : null;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Set current view binding.
     *
     * @param vb the view binding
     */
    public void setCurrentViewBinding(VB vb) {
        writeLock.lock();
        try {
            if (currentViewBindingWeakRef != null) {
                currentViewBindingWeakRef.clear();
            }
            currentViewBindingWeakRef = vb != null ? new WeakReference<>(vb) : null;
        } finally {
            writeLock.unlock();
        }
    }

}
