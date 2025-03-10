package cn.com.shadowless.baseview;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import cn.com.shadowless.baseview.manager.ActivityManager;
import cn.com.shadowless.baseview.manager.ContextManager;


/**
 * 基类Application
 *
 * @author sHadowLess
 */
public abstract class BaseApplication extends Application implements
        Application.ActivityLifecycleCallbacks, ViewModelStoreOwner {

    /**
     * The constant foregroundCount.
     */
    private static int foregroundCount = 0;

    /**
     * The M view model store.
     */
    private ViewModelStore mViewModelStore;

    @Override
    public void onCreate() {
        super.onCreate();
        mViewModelStore = new ViewModelStore();
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

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mViewModelStore;
    }

    /**
     * Gets global view model.
     *
     * @param <T>        the type parameter
     * @param modelClass the model class
     * @return the global view model
     */
    public static <T extends ViewModel> T getGlobalViewModel(@NonNull Class<T> modelClass) {
        BaseApplication context = (BaseApplication) ContextManager.INSTANCE.getCurrentContext();
        return new ViewModelProvider(context.getViewModelStore(), ViewModelProvider.AndroidViewModelFactory.getInstance(context)).get(modelClass);
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
