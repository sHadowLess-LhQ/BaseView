package cn.com.shadowless.baseview.manager;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * Activity管理
 *
 * @author sHadowLess
 */
public enum ContextManager {

    /**
     * Instance my application manager.
     */
    INSTANCE;

    /**
     * The S current activity weak ref.
     */
    private WeakReference<Context> currentContextWeakRef;


    /**
     * Get current activity activity.
     *
     * @return the activity
     */
    public Context getCurrentContext() {
        Context currentContext = null;
        if (currentContextWeakRef != null) {
            currentContext = currentContextWeakRef.get();
        }
        return currentContext;
    }

    /**
     * Set current activity.
     *
     * @param context the context
     */
    public void setCurrentContext(Context context) {
        currentContextWeakRef = new WeakReference<>(context);
    }

}
