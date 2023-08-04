package cn.com.shadowless.baseview;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import cn.com.shadowless.baseview.manager.ActivityManager;
import cn.com.shadowless.baseview.manager.ContextManager;


/**
 * 基类Application
 *
 * @author sHadowLess
 */
public abstract class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {

    /**
     * The constant foregroundCount.
     */
    private static int foregroundCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        ContextManager.INSTANCE.setCurrentContext(this);
        registerActivityLifecycleCallbacks(this);
        init();
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        ActivityManager.INSTANCE.setCurrentActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        foregroundCount++;
        ActivityManager.INSTANCE.setCurrentActivity(activity);
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        ActivityManager.INSTANCE.setCurrentActivity(activity);
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        foregroundCount--;
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    /**
     * Is app foreground boolean.
     *
     * @return the boolean
     */
    public static boolean isAppForeground() {
        return foregroundCount > 0;
    }

    /**
     * Init.
     */
    protected abstract void init();
}
