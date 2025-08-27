package cn.com.shadowless.baseview.event;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewbinding.ViewBinding;

import com.hjq.permissions.OnPermissionInterceptor;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.base.IPermission;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.com.shadowless.baseview.BaseCons;
import cn.com.shadowless.baseview.base.widget.BaseMutableLiveData;
import cn.com.shadowless.baseview.base.widget.BaseViewModel;


/**
 * 公共事件接口
 * <p>
 * 定义了视图相关的公共事件和接口，包括初始化、绑定、点击等操作。
 * 提供了统一的接口规范，便于实现一致的视图处理逻辑。
 * </p>
 *
 * @author sHadowLess
 */
public interface ViewPublicEvent {

    /**
     * Fragment事件初始化接口
     * <p>
     * 定义了Fragment相关的初始化事件方法。
     * </p>
     */
    interface InitFragmentEvent {
        /**
         * 首次初始化
         * <p>
         * 在Fragment创建时进行首次初始化操作。
         * </p>
         */
        void initFirst();

        /**
         * 获取依附的Activity
         * <p>
         * 获取Fragment当前依附的Activity实例。
         * </p>
         *
         * @return 依附的Activity实例
         */
        Activity getAttachActivity();

        /**
         * 获取加载模式
         * <p>
         * 获取Fragment的加载模式，默认为DEFAULT模式。
         * </p>
         *
         * @return 加载模式
         */
        default LoadMode getLoadMode() {
            return LoadMode.DEFAULT;
        }

        /**
         * 获取布局视图
         * <p>
         * 获取Fragment的布局视图。
         * </p>
         *
         * @return 布局视图
         */
        View getInflateView();

        /**
         * 判断Fragment是否处于激活状态
         * <p>
         * 检查Fragment是否已添加、未分离、未移除且Activity不为null。
         * </p>
         *
         * @param fragment 要检查的Fragment
         * @return 如果Fragment处于激活状态返回true，否则返回false
         */
        default boolean isFragmentActive(Fragment fragment) {
            return fragment.isAdded() && !fragment.isDetached() && !fragment.isRemoving();
        }

        /**
         * 加载模式枚举
         * <p>
         * 定义了Fragment的三种加载模式：
         * DEFAULT: 默认模式
         * ONLY_LAZY_DATA: 仅数据懒加载模式
         * LAZY_VIEW_AND_DATA: 视图和数据都懒加载模式
         * </p>
         */
        enum LoadMode {
            /**
             * 默认懒加载模式
             */
            DEFAULT,
            /**
             * 仅数据懒加载模式
             */
            ONLY_LAZY_DATA,
            /**
             * 视图和数据都懒加载模式
             */
            LAZY_VIEW_AND_DATA
        }
    }

    /**
     * 绑定公共事件接口
     * <p>
     * 定义了视图绑定相关的公共事件方法。
     * </p>
     */
    interface InitBindingPublicEvent {
        /**
         * 获取所需权限列表
         * <p>
         * 返回执行此功能所需的权限列表。
         * </p>
         *
         * @return 权限列表，如果不需要权限则返回null
         */
        @Nullable
        default List<IPermission> permissions() {
            return null;
        }

        /**
         * 初始化对象
         * <p>
         * 初始化相关对象和变量。
         * </p>
         *
         * @param savedInstanceState 保存的实例状态
         */
        void initObject(Bundle savedInstanceState);

        /**
         * 初始化视图
         * <p>
         * 初始化和设置视图相关组件。
         * </p>
         */
        void initView();

        /**
         * 初始化视图监听器
         * <p>
         * 初始化视图相关的事件监听器。
         * </p>
         */
        void initViewListener();

        /**
         * 强制数据获取执行所需权限
         * <p>
         * 返回强制执行数据初始化所需的权限列表。
         * </p>
         *
         * @return 强制权限列表
         */
        default List<IPermission> isForcePermissionToInitData() {
            return null;
        }

        /**
         * 获取被永久拒绝的权限
         * <p>
         * 从被拒绝的权限列表中筛选出被永久拒绝的权限。
         * </p>
         *
         * @param activity    当前Activity
         * @param deniedList  被拒绝的权限列表
         * @return 被永久拒绝的权限列表
         */
        default List<IPermission> getDoNotAskAgainPermission(Activity activity, List<IPermission> deniedList) {
            List<IPermission> doNotAskAgainList = new ArrayList<>();
            for (IPermission permission : deniedList) {
                if (permission.isDoNotAskAgainPermission(activity)) {
                    doNotAskAgainList.add(permission);
                }
            }
            return doNotAskAgainList;
        }

        /**
         * 判断强制执行initData所需权限是否涵盖
         * <p>
         * 检查已授予的权限是否涵盖了强制执行数据初始化所需的权限。
         * </p>
         *
         * @param grantedList 已授予的权限列表
         * @return 如果权限涵盖返回true，否则返回false
         */
        default boolean isAllCoverForcePermission(List<IPermission> grantedList) {
            boolean isAllCover = true;
            List<IPermission> temp = isForcePermissionToInitData();
            if (temp != null && !temp.isEmpty()) {
                for (IPermission permission : temp) {
                    if (!XXPermissions.containsPermission(grantedList, permission)) {
                        isAllCover = false;
                        break;
                    }
                }
            }
            return isAllCover;
        }

        /**
         * 权限请求结果回调接口
         */
        interface OnPermissionResult {

            /**
             * 权限请求结果回调
             * <p>
             * 当权限请求完成后调用此方法。
             * </p>
             *
             * @param grantedList       授予权限列表
             * @param deniedList        拒绝权限列表
             * @param doNotAskAgainList 永久拒绝权限列表
             */
            void onResult(@NonNull List<IPermission> grantedList, @NonNull List<IPermission> deniedList, @NonNull List<IPermission> doNotAskAgainList);
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
        default AsyncLoadViewCallBack AsyncLoadView() {
            return null;
        }

        /**
         * Is async load view boolean.
         *
         * @return the boolean
         */
        default boolean isAsyncLoad() {
            return false;
        }

        boolean isLazyInitSuccess();

        void syncInitView();

        void asyncInitView();

        @MainThread
        void initEvent();

        interface AsyncLoadViewAnimCallBack {
            void animStart();

            void animEnd();
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

            default void startAsyncAnimSetView(View view, AsyncLoadViewAnimCallBack callBack) {
                view.setAlpha(0);
                view.animate().alpha(0).alpha(1).setDuration(500).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        callBack.animStart();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        callBack.animEnd();
                    }
                }).setInterpolator(new LinearInterpolator()).start();
            }
        }
    }

    /**
     * The interface Init view model.
     *
     * @param <VB> the type parameter
     */
    interface InitViewModel<VB extends ViewBinding> {

        interface ExecuteModelEvent<VB extends ViewBinding> {
            /**
             * Exec.
             */
            void exec(BaseViewModel<VB, ? extends BaseMutableLiveData> model);
        }

        /**
         * Sets view model.
         *
         * @return the base view model
         */
        @NonNull
        List<BaseViewModel<VB, ? extends BaseMutableLiveData>> collectionViewModels();

        /**
         * exec Model Event.
         */
        default void execModelEvent(ExecuteModelEvent<VB> event) {
            execModelEvent(collectionViewModels(), event);
        }

        /**
         * exec Model Event.
         */
        default void execModelEvent(List<BaseViewModel<VB, ? extends BaseMutableLiveData>> list, ExecuteModelEvent<VB> event) {
            for (BaseViewModel<VB, ? extends BaseMutableLiveData> model : list) {
                event.exec(model);
            }
        }

        /**
         * Create view model vm.
         *
         * @param <VM> the type parameter
         * @param cls  the cls
         * @return the vm
         */
        default <VM extends ViewModel> VM createViewModel(@NonNull ViewModelStoreOwner owner, @NonNull Class<VM> cls) {
            return (VM) createViewModel(owner, new ViewModelProvider.NewInstanceFactory(), cls);
        }

        /**
         * Create view model vm.
         *
         * @param <VM> the type parameter
         * @param cls  the cls
         * @return the vm
         */
        default <VM extends ViewModel> VM createViewModel(@NonNull ViewModelStoreOwner owner, @NonNull ViewModelProvider.Factory factory, @NonNull Class<VM> cls) {
            return (VM) new ViewModelProvider(owner, factory).get(cls);
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
    interface InitBindingEvent extends InitBindingPublicEvent {

        /**
         * Bind data to view.
         */
        void initDataListener();

        /**
         * 初始化数据
         */
        void initData();

        /**
         * 获取权限后执行数据获取
         */
        default void initDataByPermission() {

        }

        /**
         * Init permission and init data.
         *
         * @param activity the activity
         */
        default void initPermissionAndInitData(FragmentActivity activity) {
            List<IPermission> permissions = permissions();
            boolean hasPermission = null != permissions && permissions.size() != 0;
            if (!hasPermission) {
                initDataByPermission();
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
            List<IPermission> permissions = permissions();
            boolean hasPermission = null != permissions && permissions.size() != 0;
            if (!hasPermission) {
                initDataByPermission();
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
        default void dealPermission(FragmentActivity activity, List<IPermission> permissions) {
            dealPermission(activity, permissions, null, null);
        }

        /**
         * Deal permission.
         *
         * @param activity    the activity
         * @param permissions the permissions
         * @param interceptor the interceptor
         */
        default void dealPermission(FragmentActivity activity, List<IPermission> permissions, OnPermissionInterceptor interceptor) {
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
        default void dealPermission(FragmentActivity activity, List<IPermission> permissions, OnPermissionInterceptor interceptor, OnPermissionResult callBack) {
            if (XXPermissions.isGrantedPermissions(activity, permissions)) {
                initDataByPermission();
                return;
            }
            XXPermissions.with(activity).permissions(permissions).interceptor(interceptor).request((grantedList, deniedList) -> {
                if (isAllCoverForcePermission(grantedList)) {
                    initDataByPermission();
                }
                if (callBack == null) {
                    return;
                }
                callBack.onResult(grantedList, deniedList, getDoNotAskAgainPermission(activity, deniedList));
            });
        }

        /**
         * Deal permission.
         *
         * @param fragment    the fragment
         * @param permissions the permissions
         */
        default void dealPermission(Fragment fragment, List<IPermission> permissions) {
            dealPermission(fragment, permissions, null, null);
        }

        /**
         * Deal permission.
         *
         * @param fragment    the fragment
         * @param permissions the permissions
         * @param interceptor the interceptor
         */
        default void dealPermission(Fragment fragment, List<IPermission> permissions, OnPermissionInterceptor interceptor) {
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
        default void dealPermission(Fragment fragment, List<IPermission> permissions, OnPermissionInterceptor interceptor, OnPermissionResult callBack) {
            if (XXPermissions.isGrantedPermissions(fragment.requireContext(), permissions)) {
                initDataByPermission();
                return;
            }
            XXPermissions.with(fragment).permissions(permissions).interceptor(interceptor).request((grantedList, deniedList) -> {
                if (isAllCoverForcePermission(grantedList)) {
                    initDataByPermission();
                }
                if (callBack == null) {
                    return;
                }
                callBack.onResult(grantedList, deniedList, getDoNotAskAgainPermission(fragment.requireActivity(), deniedList));
            });
        }
    }

    /**
     * The interface Init event.
     */
    interface InitModelEvent extends InitBindingPublicEvent {

        /**
         * Init model listener.
         */
        void initModelListener();

        /**
         * Init model data.
         */
        void initModelData();

        /**
         * Init model data.
         */
        default void initModelDataByPermission() {

        }

        /**
         * Init permission and init data.
         *
         * @param activity the activity
         */
        default void initPermissionAndInitData(FragmentActivity activity) {
            List<IPermission> permissions = permissions();
            boolean hasPermission = null != permissions && !permissions.isEmpty();
            if (!hasPermission) {
                initModelDataByPermission();
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
            List<IPermission> permissions = permissions();
            boolean hasPermission = null != permissions && !permissions.isEmpty();
            if (!hasPermission) {
                initModelDataByPermission();
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
        default void dealPermission(FragmentActivity activity, List<IPermission> permissions) {
            dealPermission(activity, permissions, null);
        }

        /**
         * Deal permission.
         *
         * @param activity    the activity
         * @param permissions the permissions
         * @param interceptor the interceptor
         */
        default void dealPermission(FragmentActivity activity, List<IPermission> permissions, OnPermissionInterceptor interceptor) {
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
        default void dealPermission(FragmentActivity activity, List<IPermission> permissions, OnPermissionInterceptor interceptor, OnPermissionResult callBack) {
            if (XXPermissions.isGrantedPermissions(activity, permissions)) {
                initModelDataByPermission();
                return;
            }
            XXPermissions.with(activity).permissions(permissions).interceptor(interceptor).request((grantedList, deniedList) -> {
                if (isAllCoverForcePermission(grantedList)) {
                    initModelDataByPermission();
                }
                if (callBack == null) {
                    return;
                }
                callBack.onResult(grantedList, deniedList, getDoNotAskAgainPermission(activity, deniedList));
            });
        }

        /**
         * Deal permission.
         *
         * @param fragment    the fragment
         * @param permissions the permissions
         */
        default void dealPermission(Fragment fragment, List<IPermission> permissions) {
            dealPermission(fragment, permissions, null);
        }

        /**
         * Deal permission.
         *
         * @param fragment    the fragment
         * @param permissions the permissions
         * @param interceptor the interceptor
         */
        default void dealPermission(Fragment fragment, List<IPermission> permissions, OnPermissionInterceptor interceptor) {
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
        default void dealPermission(Fragment fragment, List<IPermission> permissions, OnPermissionInterceptor interceptor, OnPermissionResult callBack) {
            if (XXPermissions.isGrantedPermissions(fragment.requireContext(), permissions)) {
                initModelDataByPermission();
                return;
            }
            XXPermissions.with(fragment).permissions(permissions).interceptor(interceptor).request((grantedList, deniedList) -> {
                if (isAllCoverForcePermission(grantedList)) {
                    initModelDataByPermission();
                }
                if (callBack == null) {
                    return;
                }
                callBack.onResult(grantedList, deniedList, getDoNotAskAgainPermission(fragment.requireActivity(), deniedList));
            });
        }
    }
}
