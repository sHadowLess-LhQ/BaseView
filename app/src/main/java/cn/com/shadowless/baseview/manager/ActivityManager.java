package cn.com.shadowless.baseview.manager;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * Activity管理器
 * <p>
 * 通过单例模式管理当前的Activity实例，使用WeakReference避免内存泄漏。
 * 主要用于全局获取当前正在运行的Activity实例。
 * </p>
 *
 * @author sHadowLess
 */
public enum ActivityManager {

    /**
     * Activity管理器单例实例
     */
    INSTANCE;

    /**
     * 当前Activity的弱引用
     * <p>
     * 使用WeakReference避免持有Activity的强引用导致内存泄漏。
     * 当Activity被销毁时，WeakReference会自动释放引用。
     * </p>
     */
    private WeakReference<Activity> currentActivityWeakRef;


    /**
     * 获取当前Activity实例
     * <p>
     * 通过WeakReference获取当前Activity实例，如果Activity已被销毁则返回null。
     * </p>
     *
     * @return 当前Activity实例，如果不存在或已被销毁则返回null
     */
    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (currentActivityWeakRef != null) {
            currentActivity = currentActivityWeakRef.get();
        }
        return currentActivity;
    }

    /**
     * 设置当前Activity实例
     * <p>
     * 将当前Activity实例包装为WeakReference保存，避免内存泄漏。
     * </p>
     *
     * @param activity 需要设置为当前的Activity实例
     */
    public void setCurrentActivity(Activity activity) {
        currentActivityWeakRef = new WeakReference<>(activity);
    }

}