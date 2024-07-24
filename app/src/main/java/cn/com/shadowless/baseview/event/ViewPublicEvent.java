package cn.com.shadowless.baseview.event;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.com.shadowless.baseview.base.BaseCons;
import cn.com.shadowless.baseview.permission.Permission;
import cn.com.shadowless.baseview.utils.PermissionUtils;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * The interface Public event.
 *
 * @param <VB> the type parameter
 * @author sHadowLess
 */
public interface ViewPublicEvent<VB extends ViewBinding> extends View.OnClickListener {

    /**
     * Init generics class class.
     *
     * @param o the o
     * @return the class
     */
    default Class<VB> initGenericsClass(Object o) {
        Type superClass = o.getClass().getGenericSuperclass();
        ParameterizedType parameterized = (ParameterizedType) superClass;
        Class<VB> genericsCls = (Class<VB>) parameterized.getActualTypeArguments()[0];
        if (genericsCls == ViewBinding.class) {
            genericsCls = setBindViewClass();
        }
        return genericsCls;
    }

    /**
     * Is fast click boolean.
     *
     * @param time the time
     * @return the boolean
     */
    default boolean isFastClick(int time) {
        long currentTime = System.currentTimeMillis();
        long timeInterval = currentTime - BaseCons.lastClickTime;
        if (0 < timeInterval && timeInterval < time) {
            return true;
        }
        BaseCons.lastClickTime = currentTime;
        return false;
    }

    @Override
    default void onClick(View v) {
        if (!isFastClick(BaseCons.TIME)) {
            antiShakingClick(v);
        }
    }

    /**
     * Inflate t.
     *
     * @param <T>            the type parameter
     * @param cls            the cls
     * @param layoutInflater the layout inflater
     * @return the t
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws NoSuchMethodException     the no such method exception
     */
    default <T> T inflate(Class<T> cls, LayoutInflater layoutInflater) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method inflateMethod = cls.getMethod("inflate", LayoutInflater.class);
        return (T) inflateMethod.invoke(null, layoutInflater);
    }

    /**
     * Click.
     *
     * @param v the v
     */
    default void antiShakingClick(View v) {

    }

    /**
     * Init first.
     */
    default void initFirst() {

    }

    /**
     * Inflate view vb.
     *
     * @param o              the o
     * @param layoutInflater the layout inflater
     * @return the vb
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws NoSuchMethodException     the no such method exception
     */
    default VB inflateView(Object o, LayoutInflater layoutInflater) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return this.inflate(initGenericsClass(o), layoutInflater);
    }

    /**
     * 设置绑定视图
     *
     * @return the 视图
     */
    default Class<VB> setBindViewClass() {
        return null;
    }

    /**
     * Init sync view async load view call back.
     *
     * @return the async load view call back
     */
    default AsyncLoadViewCallBack initSyncView() {
        return null;
    }

    /**
     * Is async load view boolean.
     *
     * @return the boolean
     */
    default boolean isAsyncLoadView() {
        return false;
    }

    /**
     * Init permission and init data.
     *
     * @param activity the activity
     * @param owner    the owner
     */
    default void initPermissionAndInitData(FragmentActivity activity, LifecycleOwner owner) {
        String[] permissions = permissions();
        if (null == permissions || permissions.length == 0) {
            initData();
            initDataListener();
            return;
        }
        initPermission(activity, owner, permissions);
    }

    /**
     * Init permission.
     *
     * @param activity    the activity
     * @param owner       the owner
     * @param permissions the permissions
     */
    default void initPermission(FragmentActivity activity, LifecycleOwner owner, String[] permissions) {
        dealPermission(activity, owner, permissions, null);
    }

    /**
     * Deal permission.
     *
     * @param activity    the activity
     * @param owner       the owner
     * @param permissions the permissions
     * @param callBack    the call back
     */
    default void dealPermission(FragmentActivity activity, LifecycleOwner owner, String[] permissions, PermissionCallBack callBack) {
        final List<String> disagree = new ArrayList<>();
        final List<String> ban = new ArrayList<>();
        PermissionUtils.getPermissionObservable(activity, owner, permissions).subscribe(new Observer<Permission>() {

            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Permission permission) {
                if (permission.shouldShowRequestPermissionRationale) {
                    ban.add(permission.name);
                } else if (!permission.granted) {
                    disagree.add(permission.name);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                if (callBack != null) {
                    callBack.fail("处理权限错误", e);
                }
            }

            @Override
            public void onComplete() {
                if (ban.isEmpty() && disagree.isEmpty()) {
                    if (callBack != null) {
                        callBack.agree();
                    }
                    initObject();
                    initData();
                    initView();
                } else if (!ban.isEmpty()) {
                    if (callBack != null) {
                        callBack.ban(ban);
                    }
                } else {
                    if (callBack != null) {
                        callBack.disagree(disagree);
                    }
                }
            }
        });
    }

    /**
     * 需要申请的权限
     *
     * @return the 权限组
     */
    @Nullable
    String[] permissions();

    /**
     * 初始化对象
     */
    void initObject();

    /**
     * 给视图绑定数据
     */
    void initView();

    /**
     * 初始化视图监听
     */
    void initViewListener();

    /**
     * 初始化数据
     */
    void initData();

    /**
     * Bind data to view.
     */
    void initDataListener();

    /**
     * The interface Async load view call back.
     */
    interface AsyncLoadViewCallBack {
        /**
         * Show.
         */
        void showLoadView();

        /**
         * Dismiss.
         */
        void dismissLoadView();
    }

    /**
     * The interface Permission call back.
     */
    interface PermissionCallBack {
        /**
         * Agree.
         */
        void agree();

        /**
         * Disagree.
         *
         * @param name the name
         */
        void disagree(List<String> name);

        /**
         * Ban.
         *
         * @param name the name
         */
        void ban(List<String> name);

        /**
         * Fail.
         *
         * @param msg the msg
         * @param e   the e
         */
        void fail(String msg, @Nullable Throwable e);
    }
}
