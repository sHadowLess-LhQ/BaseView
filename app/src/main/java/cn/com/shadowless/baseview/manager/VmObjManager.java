package cn.com.shadowless.baseview.manager;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewbinding.ViewBinding;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ViewModel对象资源管理器
 * <p>
 * 管理ViewModel相关的各种资源，包括Activity、Fragment、LifecycleOwner和ViewBinding等。
 * 使用WeakReference避免内存泄漏，使用读写锁保证线程安全。
 * </p>
 *
 * @param <VB> ViewBinding类型参数
 * @author sHadowLess
 */
public class VmObjManager<VB extends ViewBinding> {

    /**
     * 当前Activity的弱引用
     * <p>
     * 使用WeakReference避免持有Activity的强引用导致内存泄漏。
     * </p>
     */
    private volatile WeakReference<Activity> currentActivityWeakRef;
    
    /**
     * 当前Fragment的弱引用
     * <p>
     * 使用WeakReference避免持有Fragment的强引用导致内存泄漏。
     * </p>
     */
    private volatile WeakReference<Fragment> currentFragmentWeakRef;
    
    /**
     * 当前LifecycleOwner的弱引用
     * <p>
     * 使用WeakReference避免持有LifecycleOwner的强引用导致内存泄漏。
     * </p>
     */
    private volatile WeakReference<LifecycleOwner> currentLifecycleOwnerWeakRef;
    
    /**
     * 当前ViewBinding的弱引用
     * <p>
     * 使用WeakReference避免持有ViewBinding的强引用导致内存泄漏。
     * </p>
     */
    private volatile WeakReference<VB> currentViewBindingWeakRef;
    
    /**
     * 读写锁
     * <p>
     * 用于保证多线程环境下的线程安全。
     * </p>
     */
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    /**
     * 读锁
     */
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    
    /**
     * 写锁
     */
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    /**
     * 获取当前Activity实例
     * <p>
     * 通过WeakReference获取当前Activity实例，如果Activity已被销毁则返回null。
     * </p>
     *
     * @return 当前Activity实例，如果不存在或已被销毁则返回null
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
     * 设置当前Activity实例
     * <p>
     * 将当前Activity实例包装为WeakReference保存，避免内存泄漏。
     * 如果已存在Activity引用则先清除。
     * </p>
     *
     * @param activity 需要设置为当前的Activity实例
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
     * 获取当前Fragment实例
     * <p>
     * 通过WeakReference获取当前Fragment实例，如果Fragment已被销毁则返回null。
     * </p>
     *
     * @return 当前Fragment实例，如果不存在或已被销毁则返回null
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
     * 设置当前Fragment实例
     * <p>
     * 将当前Fragment实例包装为WeakReference保存，避免内存泄漏。
     * 如果已存在Fragment引用则先清除。
     * </p>
     *
     * @param fragment 需要设置为当前的Fragment实例
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
     * 获取当前LifecycleOwner实例
     * <p>
     * 通过WeakReference获取当前LifecycleOwner实例，如果已被销毁则返回null。
     * </p>
     *
     * @return 当前LifecycleOwner实例，如果不存在或已被销毁则返回null
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
     * 设置当前LifecycleOwner实例
     * <p>
     * 将当前LifecycleOwner实例包装为WeakReference保存，避免内存泄漏。
     * 如果已存在LifecycleOwner引用则先清除。
     * </p>
     *
     * @param lifecycleOwner 需要设置为当前的LifecycleOwner实例
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
     * 获取当前ViewBinding实例
     * <p>
     * 通过WeakReference获取当前ViewBinding实例，如果已被销毁则返回null。
     * </p>
     *
     * @return 当前ViewBinding实例，如果不存在或已被销毁则返回null
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
     * 设置当前ViewBinding实例
     * <p>
     * 将当前ViewBinding实例包装为WeakReference保存，避免内存泄漏。
     * 如果已存在ViewBinding引用则先清除。
     * </p>
     *
     * @param vb 需要设置为当前的ViewBinding实例
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
