package cn.com.shadowless.baseview.manager;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * Activity管理
 *
 * @author sHadowLess
 */
public enum ActivityManager {

    /**
     * Instance my activity manager.
     */
    INSTANCE;

    /**
     * The S current activity weak ref.
     */
    private WeakReference<Activity> currentActivityWeakRef;


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

}
