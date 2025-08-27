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
 * <p>
 * 所有应用程序的基类，提供了全局的ViewModel存储、Activity生命周期监听等功能。
 * 实现了ViewModelStoreOwner接口，支持全局ViewModel管理。
 * </p>
 *
 * @author sHadowLess
 */
public abstract class BaseApplication extends Application implements
        Application.ActivityLifecycleCallbacks, ViewModelStoreOwner {

    /**
     * 前台Activity计数器
     * <p>
     * 用于跟踪当前处于前台的Activity数量，判断应用是否在前台运行。
     * </p>
     */
    private static int foregroundCount = 0;

    /**
     * ViewModel存储
     * <p>
     * 用于存储全局ViewModel实例，确保在整个应用程序生命周期内可用。
     * </p>
     */
    private ViewModelStore mViewModelStore;

    /**
     * 应用程序创建时调用
     * <p>
     * 初始化ViewModelStore，设置全局Context，注册Activity生命周期回调，并调用抽象初始化方法。
     * </p>
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mViewModelStore = new ViewModelStore();
        ContextManager.INSTANCE.setCurrentContext(this);
        registerActivityLifecycleCallbacks(this);
        init();
    }

    /**
     * Activity创建时调用
     * <p>
     * 当Activity创建时更新当前Activity引用。
     * </p>
     *
     * @param activity           创建的Activity
     * @param savedInstanceState 保存的实例状态
     */
    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        ActivityManager.INSTANCE.setCurrentActivity(activity);
    }

    /**
     * Activity启动时调用
     * <p>
     * 当Activity启动时增加前台计数器并更新当前Activity引用。
     * </p>
     *
     * @param activity 启动的Activity
     */
    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        foregroundCount++;
        ActivityManager.INSTANCE.setCurrentActivity(activity);
    }

    /**
     * Activity恢复时调用
     * <p>
     * 当Activity恢复时更新当前Activity引用。
     * </p>
     *
     * @param activity 恢复的Activity
     */
    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        ActivityManager.INSTANCE.setCurrentActivity(activity);
    }

    /**
     * Activity暂停时调用
     * <p>
     * 当Activity暂停时的回调方法，当前为空实现。
     * </p>
     *
     * @param activity 暂停的Activity
     */
    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    /**
     * Activity停止时调用
     * <p>
     * 当Activity停止时减少前台计数器。
     * </p>
     *
     * @param activity 停止的Activity
     */
    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        foregroundCount--;
    }

    /**
     * Activity保存状态时调用
     * <p>
     * 当Activity保存状态时的回调方法，当前为空实现。
     * </p>
     *
     * @param activity 保存状态的Activity
     * @param outState 用于保存状态的Bundle
     */
    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    /**
     * Activity销毁时调用
     * <p>
     * 当Activity销毁时的回调方法，当前为空实现。
     * </p>
     *
     * @param activity 销毁的Activity
     */
    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    /**
     * 获取ViewModel存储
     * <p>
     * 返回用于存储全局ViewModel的ViewModelStore实例。
     * </p>
     *
     * @return ViewModelStore实例
     */
    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mViewModelStore;
    }

    /**
     * 获取全局ViewModel
     * <p>
     * 获取指定类型的全局ViewModel实例，如果不存在则创建新的实例。
     * </p>
     *
     * @param <T>        ViewModel类型参数
     * @param modelClass ViewModel类
     * @return 指定类型的ViewModel实例
     */
    public static <T extends ViewModel> T getGlobalViewModel(@NonNull Class<T> modelClass) {
        BaseApplication context = (BaseApplication) ContextManager.INSTANCE.getCurrentContext();
        return new ViewModelProvider(context.getViewModelStore(), ViewModelProvider.AndroidViewModelFactory.getInstance(context)).get(modelClass);
    }

    /**
     * 判断应用是否在前台运行
     * <p>
     * 通过检查前台Activity计数器判断应用是否在前台运行。
     * </p>
     *
     * @return 如果应用在前台运行返回true，否则返回false
     */
    public static boolean isAppForeground() {
        return foregroundCount > 0;
    }

    /**
     * 初始化方法
     * <p>
     * 抽象初始化方法，子类需要实现此方法进行具体的初始化操作。
     * </p>
     */
    protected abstract void init();
}