package cn.com.shadowless.baseview.manager;

import android.content.Context;

import java.lang.ref.WeakReference;

/**
 * 上下文管理器
 * <p>
 * 通过单例模式管理全局Context实例，使用WeakReference避免内存泄漏。
 * 主要用于全局获取Application上下文。
 * </p>
 *
 * @author sHadowLess
 */
public enum ContextManager {

    /**
     * Context管理器单例实例
     */
    INSTANCE;

    /**
     * 当前Context的弱引用
     * <p>
     * 使用WeakReference避免持有Context的强引用导致内存泄漏。
     * 当Context被销毁时，WeakReference会自动释放引用。
     * </p>
     */
    private WeakReference<Context> currentContextWeakRef;


    /**
     * 获取当前Context实例
     * <p>
     * 通过WeakReference获取当前Context实例，如果Context已被销毁则返回null。
     * </p>
     *
     * @return 当前Context实例，如果不存在或已被销毁则返回null
     */
    public Context getCurrentContext() {
        Context currentContext = null;
        if (currentContextWeakRef != null) {
            currentContext = currentContextWeakRef.get();
        }
        return currentContext;
    }

    /**
     * 设置当前Context实例
     * <p>
     * 将当前Context实例包装为WeakReference保存，避免内存泄漏。
     * 通常在Application的onCreate方法中调用此方法设置Application Context。
     * </p>
     *
     * @param context 需要设置为当前的Context实例
     */
    public void setCurrentContext(Context context) {
        currentContextWeakRef = new WeakReference<>(context);
    }

}