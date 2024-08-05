package cn.com.shadowless.baseview.event;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import cn.com.shadowless.baseview.BaseCons;
import cn.com.shadowless.baseview.base.widget.BaseViewModel;
import cn.com.shadowless.permissionlib.PermissionConfig;
import cn.com.shadowless.permissionlib.PermissionsFragment;


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
    }

    /**
     * The interface Init view binding.
     *
     * @param <VB> the type parameter
     */
    interface InitViewBinding<VB extends ViewBinding> {

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
        default String[] normalPermissions() {
            return new String[0];
        }

        /**
         * Special permissions string [ ].
         *
         * @return the string [ ]
         */
        @Nullable
        default String[] specialPermissions() {
            return new String[0];
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
         * 初始化数据
         */
        void initData();

        /**
         * Bind data to view.
         */
        void initDataListener();

        /**
         * Init permission and init data.
         *
         * @param activity the activity
         */
        default void initPermissionAndInitData(FragmentActivity activity) {
            String[] normalPermissions = normalPermissions();
            String[] specialPermissions = specialPermissions();
            boolean hasNormal = null != normalPermissions && normalPermissions.length != 0;
            boolean hasSpecial = null != specialPermissions && specialPermissions.length != 0;
            if (!hasNormal && !hasSpecial) {
                initData();
                initDataListener();
                return;
            }
            dealPermission(activity, normalPermissions, specialPermissions);
        }

        /**
         * Init permission and init data.
         *
         * @param fragment the fragment
         */
        default void initPermissionAndInitData(Fragment fragment) {
            String[] normalPermissions = normalPermissions();
            String[] specialPermissions = specialPermissions();
            boolean hasNormal = null != normalPermissions && normalPermissions.length != 0;
            boolean hasSpecial = null != specialPermissions && specialPermissions.length != 0;
            if (!hasNormal && !hasSpecial) {
                initData();
                initDataListener();
                return;
            }
            dealPermission(fragment, normalPermissions, specialPermissions);
        }

        /**
         * Init permission.
         *
         * @param activity          the activity
         * @param normalPermission  the normal permission
         * @param specialPermission the special permission
         */
        default void dealPermission(FragmentActivity activity, String[] normalPermission, String[] specialPermission) {
            dealPermission(activity, normalPermission, specialPermission, null);
        }

        /**
         * Init permission.
         *
         * @param fragment          the fragment
         * @param normalPermission  the normal permission
         * @param specialPermission the special permission
         */
        default void dealPermission(Fragment fragment, String[] normalPermission, String[] specialPermission) {
            dealPermission(fragment, normalPermission, specialPermission, null);
        }

        /**
         * Deal permission.
         *
         * @param activity          the activity
         * @param normalPermission  the normal permission
         * @param specialPermission the special permission
         * @param callBack          the call back
         */
        default void dealPermission(FragmentActivity activity, String[] normalPermission, String[] specialPermission, PermissionsFragment.PermissionCallBack callBack) {
            PermissionConfig config = new PermissionConfig(activity);
            config.setNormalPermission(normalPermission);
            config.setSpecialPermission(specialPermission);
            config.requestPermissions(new PermissionsFragment.PermissionCallBack() {
                @Override
                public void agree() {
                    initData();
                    initDataListener();
                    callBack.agree();
                }

                @Override
                public void disagree(List<String> name) {
                    callBack.disagree(name);
                }

                @Override
                public void ban(List<String> name) {
                    callBack.ban(name);
                }
            });
        }

        /**
         * Deal permission.
         *
         * @param fragment          the fragment
         * @param normalPermission  the normal permission
         * @param specialPermission the special permission
         * @param callBack          the call back
         */
        default void dealPermission(Fragment fragment, String[] normalPermission, String[] specialPermission, PermissionsFragment.PermissionCallBack callBack) {
            PermissionConfig config = new PermissionConfig(fragment);
            config.setNormalPermission(normalPermission);
            config.setSpecialPermission(specialPermission);
            config.requestPermissions(new PermissionsFragment.PermissionCallBack() {
                @Override
                public void agree() {
                    initData();
                    initDataListener();
                    callBack.agree();
                }

                @Override
                public void disagree(List<String> name) {
                    callBack.disagree(name);
                }

                @Override
                public void ban(List<String> name) {
                    callBack.ban(name);
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
        @Nullable
        default String[] normalPermissions() {
            return new String[0];
        }

        /**
         * Special permissions string [ ].
         *
         * @return the string [ ]
         */
        @Nullable
        default String[] specialPermissions() {
            return new String[0];
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
         * Init permission and init data.
         *
         * @param activity the activity
         */
        default void initPermissionAndInitData(FragmentActivity activity) {
            String[] normalPermissions = normalPermissions();
            String[] specialPermissions = specialPermissions();
            boolean hasNormal = null != normalPermissions && normalPermissions.length != 0;
            boolean hasSpecial = null != specialPermissions && specialPermissions.length != 0;
            if (!hasNormal && !hasSpecial) {
                initModelObserve();
                return;
            }
            dealPermission(activity, normalPermissions, specialPermissions);
        }

        /**
         * Init permission and init data.
         *
         * @param fragment the fragment
         */
        default void initPermissionAndInitData(Fragment fragment) {
            String[] normalPermissions = normalPermissions();
            String[] specialPermissions = specialPermissions();
            boolean hasNormal = null != normalPermissions && normalPermissions.length != 0;
            boolean hasSpecial = null != specialPermissions && specialPermissions.length != 0;
            if (!hasNormal && !hasSpecial) {
                initModelObserve();
                return;
            }
            dealPermission(fragment, normalPermissions, specialPermissions);
        }

        /**
         * Init permission.
         *
         * @param activity          the activity
         * @param normalPermission  the normal permission
         * @param specialPermission the special permission
         */
        default void dealPermission(FragmentActivity activity, String[] normalPermission, String[] specialPermission) {
            dealPermission(activity, normalPermission, specialPermission, null);
        }

        /**
         * Init permission.
         *
         * @param fragment          the fragment
         * @param normalPermission  the normal permission
         * @param specialPermission the special permission
         */
        default void dealPermission(Fragment fragment, String[] normalPermission, String[] specialPermission) {
            dealPermission(fragment, normalPermission, specialPermission, null);
        }

        /**
         * Deal permission.
         *
         * @param activity          the activity
         * @param normalPermission  the normal permission
         * @param specialPermission the special permission
         * @param callBack          the call back
         */
        default void dealPermission(FragmentActivity activity, String[] normalPermission, String[] specialPermission, PermissionsFragment.PermissionCallBack callBack) {
            PermissionConfig config = new PermissionConfig(activity);
            config.setNormalPermission(normalPermission);
            config.setSpecialPermission(specialPermission);
            config.requestPermissions(new PermissionsFragment.PermissionCallBack() {
                @Override
                public void agree() {
                    initModelObserve();
                    callBack.agree();
                }

                @Override
                public void disagree(List<String> name) {
                    callBack.disagree(name);
                }

                @Override
                public void ban(List<String> name) {
                    callBack.ban(name);
                }
            });
        }

        /**
         * Deal permission.
         *
         * @param fragment          the fragment
         * @param normalPermission  the normal permission
         * @param specialPermission the special permission
         * @param callBack          the call back
         */
        default void dealPermission(Fragment fragment, String[] normalPermission, String[] specialPermission, PermissionsFragment.PermissionCallBack callBack) {
            PermissionConfig config = new PermissionConfig(fragment);
            config.setNormalPermission(normalPermission);
            config.setSpecialPermission(specialPermission);
            config.requestPermissions(new PermissionsFragment.PermissionCallBack() {
                @Override
                public void agree() {
                    initModelObserve();
                    callBack.agree();
                }

                @Override
                public void disagree(List<String> name) {
                    callBack.disagree(name);
                }

                @Override
                public void ban(List<String> name) {
                    callBack.ban(name);
                }
            });
        }
    }


}
