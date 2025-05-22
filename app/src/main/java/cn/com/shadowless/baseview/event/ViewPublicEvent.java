package cn.com.shadowless.baseview.event;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.OnPermissionInterceptor;
import com.hjq.permissions.XXPermissions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import cn.com.shadowless.baseview.BaseCons;
import cn.com.shadowless.baseview.base.widget.BaseViewModel;


/**
 * The interface Public event.
 *
 * @author sHadowLess
 */
public interface ViewPublicEvent {

    /**
     * The interface Init fragment event.
     */
    interface InitFragmentEvent {
        /**
         * Init first.
         */
        void initFirst();

        /**
         * Gets activity.
         *
         * @return the activity
         */
        default Activity getAttachActivity() {
            return null;
        }

        /**
         * 获取加载模式
         *
         * @return the load mode
         */
        default LoadMode getLoadMode() {
            return LoadMode.DEFAULT;
        }

        /**
         * The enum Load mode.
         */
        enum LoadMode {
            /**
             * Default lazy mode.
             */
            DEFAULT,
            /**
             * Only lazy data lazy mode.
             */
            ONLY_LAZY_DATA,
            /**
             * All lazy lazy mode.
             */
            LAZY_VIEW_AND_DATA
        }
    }

    /**
     * The interface Init view binding.
     *
     * @param <VB> the type parameter
     */
    interface InitViewBinding<VB extends ViewBinding> {

        /**
         * Gets bind view.
         *
         * @return the bind view
         */
        VB getBindView();

        /**
         * Get view binding generics class type [ ].
         *
         * @param o the o
         * @return the type [ ]
         */
        default Type[] getViewBindingGenericsClass(Object o) {
            Type superClass = o.getClass().getGenericSuperclass();
            ParameterizedType parameterized = (ParameterizedType) superClass;
            return parameterized.getActualTypeArguments();
        }

        /**
         * Init generics class class.
         *
         * @param o the o
         * @return the class
         */
        default Class<VB> initViewBindingGenericsClass(Object o) {
            Type[] types = getViewBindingGenericsClass(o);
            for (Type type : types) {
                Class<?> genericsCls = (Class<?>) type;
                if (!ViewBinding.class.isAssignableFrom(genericsCls)) {
                    continue;
                }
                if (genericsCls == ViewBinding.class) {
                    genericsCls = setBindViewClass();
                }
                if (genericsCls == null) {
                    throw new RuntimeException("实现动态ViewBinding，请重写setBindViewClass方法");
                }
                return (Class<VB>) genericsCls;
            }
            throw new RuntimeException("传入的泛型未找到与ViewBinding相关的泛型超类，请检查参数或手动初始化ViewBinding");
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
            Method inflateMethod = initViewBindingGenericsClass(o).getMethod("inflate", LayoutInflater.class);
            return (VB) inflateMethod.invoke(null, layoutInflater);
        }

        /**
         * Inflate view vb.
         *
         * @param o              the o
         * @param layoutInflater the layout inflater
         * @param parent         the parent
         * @param attachToParent the attach to parent
         * @return the vb
         * @throws InvocationTargetException the invocation target exception
         * @throws IllegalAccessException    the illegal access exception
         * @throws NoSuchMethodException     the no such method exception
         */
        default VB inflateView(Object o, LayoutInflater layoutInflater, ViewGroup parent, boolean attachToParent) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
            Method inflateMethod = initViewBindingGenericsClass(o).getMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            return (VB) inflateMethod.invoke(null, layoutInflater, parent, attachToParent);
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
    }

    /**
     * The interface Init view model.
     *
     * @param <VB> the type parameter
     */
    interface InitViewModel<VB extends ViewBinding> {

        /**
         * Sets view model.
         *
         * @return the base view model
         */
        @NonNull
        List<BaseViewModel<VB, ?>> setViewModels();

        /**
         * Create activity view model vm.
         *
         * @param <VM>     the type parameter
         * @param activity the activity
         * @param cls      the cls
         * @param vb       the vb
         * @return the vm
         */
        default <VM extends ViewModel> VM createActivityViewModel(FragmentActivity activity, Class<VM> cls, VB vb) {
            BaseViewModel<VB, ?> viewModel = (BaseViewModel<VB, ?>) new ViewModelProvider(activity, new ViewModelProvider.NewInstanceFactory()).get(cls);
            viewModel.setOwner(activity);
            viewModel.setActivity(activity);
            viewModel.setBindView(vb);
            viewModel.onModelCreated();
            viewModel.onModelInitDataListener();
            return (VM) viewModel;
        }

        /**
         * Create fragment view model vm.
         *
         * @param <VM>     the type parameter
         * @param fragment the fragment
         * @param cls      the cls
         * @param vb       the vb
         * @return the vm
         */
        default <VM extends ViewModel> VM createFragmentViewModel(Fragment fragment, Class<VM> cls, VB vb) {
            BaseViewModel<VB, ?> viewModel = (BaseViewModel<VB, ?>) new ViewModelProvider(fragment, new ViewModelProvider.NewInstanceFactory()).get(cls);
            viewModel.setOwner(fragment);
            viewModel.setFragment(fragment);
            viewModel.setBindView(vb);
            viewModel.onModelCreated();
            viewModel.onModelInitDataListener();
            return (VM) viewModel;
        }
    }

    /**
     * The interface Init view click.
     */
    interface InitViewClick extends View.OnClickListener {
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

        /**
         * Click.
         *
         * @param v the v
         */
        default void antiShakingClick(View v) {

        }

        @Override
        default void onClick(View v) {
            if (!isFastClick(BaseCons.TIME)) {
                antiShakingClick(v);
            }
        }
    }

    /**
     * The interface Init event.
     */
    interface InitBindingEvent {

        /**
         * 需要申请的权限
         *
         * @return the 权限组
         */
        @Nullable
        default String[] permissions() {
            return null;
        }

        /**
         * 初始化对象
         *
         * @param savedInstanceState the saved instance state
         */
        void initObject(Bundle savedInstanceState);

        /**
         * 给视图绑定数据
         */
        void initView();

        /**
         * 初始化视图监听
         */
        void initViewListener();

        /**
         * Bind data to view.
         */
        void initDataListener();

        /**
         * 初始化数据
         */
        void initData();

        /**
         * Init permission and init data.
         *
         * @param activity the activity
         */
        default void initPermissionAndInitData(FragmentActivity activity) {
            String[] permissions = permissions();
            boolean hasPermission = null != permissions && permissions.length != 0;
            if (!hasPermission) {
                initDataListener();
                initData();
                return;
            }
            dealPermission(activity, permissions);
        }

        /**
         * Init permission and init data.
         *
         * @param fragment the fragment
         */
        default void initPermissionAndInitData(Fragment fragment) {
            String[] permissions = permissions();
            boolean hasPermission = null != permissions && permissions.length != 0;
            if (!hasPermission) {
                initDataListener();
                initData();
                return;
            }
            dealPermission(fragment, permissions);
        }

        /**
         * Deal permission.
         *
         * @param activity    the activity
         * @param permissions the permissions
         */
        default void dealPermission(FragmentActivity activity, String[] permissions) {
            dealPermission(activity, permissions, null, null);
        }

        /**
         * Deal permission.
         *
         * @param activity    the activity
         * @param permissions the permissions
         * @param interceptor the interceptor
         */
        default void dealPermission(FragmentActivity activity, String[] permissions, OnPermissionInterceptor interceptor) {
            dealPermission(activity, permissions, interceptor, null);
        }

        /**
         * Deal permission.
         *
         * @param activity    the activity
         * @param permissions the permissions
         * @param interceptor the interceptor
         * @param callBack    the call back
         */
        default void dealPermission(FragmentActivity activity, String[] permissions, OnPermissionInterceptor interceptor, OnPermissionCallback callBack) {
            if (XXPermissions.isGranted(activity, permissions)) {
                initDataListener();
                initData();
                return;
            }
            XXPermissions.with(activity).permission(permissions).interceptor(interceptor).request(new OnPermissionCallback() {

                @Override
                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                    initDataListener();
                    initData();
                    if (callBack == null) {
                        return;
                    }
                    callBack.onGranted(permissions, allGranted);
                }

                @Override
                public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                    if (callBack == null) {
                        return;
                    }
                    callBack.onDenied(permissions, doNotAskAgain);
                }
            });
        }

        /**
         * Deal permission.
         *
         * @param fragment    the fragment
         * @param permissions the permissions
         */
        default void dealPermission(Fragment fragment, String[] permissions) {
            dealPermission(fragment, permissions, null, null);
        }

        /**
         * Deal permission.
         *
         * @param fragment    the fragment
         * @param permissions the permissions
         * @param interceptor the interceptor
         */
        default void dealPermission(Fragment fragment, String[] permissions, OnPermissionInterceptor interceptor) {
            dealPermission(fragment, permissions, interceptor, null);
        }

        /**
         * Deal permission.
         *
         * @param fragment    the fragment
         * @param permissions the permissions
         * @param interceptor the interceptor
         * @param callBack    the call back
         */
        default void dealPermission(Fragment fragment, String[] permissions, OnPermissionInterceptor interceptor, OnPermissionCallback callBack) {
            if (XXPermissions.isGranted(fragment.requireContext(), permissions)) {
                initDataListener();
                initData();
                return;
            }
            XXPermissions.with(fragment).permission(permissions).interceptor(interceptor).request(new OnPermissionCallback() {

                @Override
                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                    initDataListener();
                    initData();
                    if (callBack == null) {
                        return;
                    }
                    callBack.onGranted(permissions, allGranted);
                }

                @Override
                public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                    if (callBack == null) {
                        return;
                    }
                    callBack.onDenied(permissions, doNotAskAgain);
                }
            });
        }
    }

    /**
     * The interface Init event.
     */
    interface InitModelEvent {

        /**
         * 需要申请的权限
         *
         * @return the 权限组
         */
        default String[] permissions() {
            return null;
        }

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
         * Init model observe.
         */
        void initModelObserve();

        /**
         * Init model listener.
         */
        void initModelListener();

        /**
         * Init permission and init data.
         *
         * @param activity the activity
         */
        default void initPermissionAndInitData(FragmentActivity activity) {
            String[] permissions = permissions();
            boolean hasPermission = null != permissions && permissions.length != 0;
            if (!hasPermission) {
                initModelObserve();
                return;
            }
            dealPermission(activity, permissions);
        }

        /**
         * Init permission and init data.
         *
         * @param fragment the fragment
         */
        default void initPermissionAndInitData(Fragment fragment) {
            String[] permissions = permissions();
            boolean hasPermission = null != permissions && permissions.length != 0;
            if (!hasPermission) {
                initModelObserve();
                return;
            }
            dealPermission(fragment, permissions);
        }

        /**
         * Deal permission.
         *
         * @param activity    the activity
         * @param permissions the permissions
         */
        default void dealPermission(FragmentActivity activity, String[] permissions) {
            dealPermission(activity, permissions, null, null);
        }

        /**
         * Deal permission.
         *
         * @param activity    the activity
         * @param permissions the permissions
         * @param interceptor the interceptor
         */
        default void dealPermission(FragmentActivity activity, String[] permissions, OnPermissionInterceptor interceptor) {
            dealPermission(activity, permissions, interceptor, null);
        }

        /**
         * Deal permission.
         *
         * @param activity    the activity
         * @param permissions the permissions
         * @param interceptor the interceptor
         * @param callBack    the call back
         */
        default void dealPermission(FragmentActivity activity, String[] permissions, OnPermissionInterceptor interceptor, OnPermissionCallback callBack) {
            if (XXPermissions.isGranted(activity, permissions)) {
                initModelObserve();
                return;
            }
            XXPermissions.with(activity).permission(permissions).interceptor(interceptor).request(new OnPermissionCallback() {

                @Override
                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                    initModelObserve();
                    if (callBack == null) {
                        return;
                    }
                    callBack.onGranted(permissions, allGranted);
                }

                @Override
                public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                    if (callBack == null) {
                        return;
                    }
                    callBack.onDenied(permissions, doNotAskAgain);
                }
            });
        }

        /**
         * Deal permission.
         *
         * @param fragment    the fragment
         * @param permissions the permissions
         */
        default void dealPermission(Fragment fragment, String[] permissions) {
            dealPermission(fragment, permissions, null, null);
        }

        /**
         * Deal permission.
         *
         * @param fragment    the fragment
         * @param permissions the permissions
         * @param interceptor the interceptor
         */
        default void dealPermission(Fragment fragment, String[] permissions, OnPermissionInterceptor interceptor) {
            dealPermission(fragment, permissions, interceptor, null);
        }

        /**
         * Deal permission.
         *
         * @param fragment    the fragment
         * @param permissions the permissions
         * @param interceptor the interceptor
         * @param callBack    the call back
         */
        default void dealPermission(Fragment fragment, String[] permissions, OnPermissionInterceptor interceptor, OnPermissionCallback callBack) {
            if (XXPermissions.isGranted(fragment.requireContext(), permissions)) {
                initModelObserve();
                return;
            }
            XXPermissions.with(fragment).permission(permissions).interceptor(interceptor).request(new OnPermissionCallback() {

                @Override
                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                    initModelObserve();
                    if (callBack == null) {
                        return;
                    }
                    callBack.onGranted(permissions, allGranted);
                }

                @Override
                public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                    if (callBack == null) {
                        return;
                    }
                    callBack.onDenied(permissions, doNotAskAgain);
                }
            });
        }
    }


}
