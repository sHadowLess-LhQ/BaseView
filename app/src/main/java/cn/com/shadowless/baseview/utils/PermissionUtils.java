package cn.com.shadowless.baseview.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import com.rxjava.rxlife.ObservableLife;
import com.rxjava.rxlife.RxLife;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.shadowless.baseview.permission.Permission;
import cn.com.shadowless.baseview.permission.RxPermissions;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * The type Permission utils.
 *
 * @author sHadowLess
 */
public class PermissionUtils {

    /**
     * Instantiates a new Permission utils.
     */
    private PermissionUtils() {
    }

    /**
     * The interface Permission call back.
     */
    public interface PermissionCallBack {
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

    /**
     * Gets permission observable.
     *
     * @param activity    the activity
     * @param owner       the owner
     * @param permissions the permissions
     * @return the permission observable
     */
    public static ObservableLife<Permission> getPermissionObservable(FragmentActivity activity, LifecycleOwner owner, String[] permissions) {
        return new RxPermissions(activity).requestEach(permissions).to(RxLife.to(owner));
    }

    /**
     * Gets permission observable.
     *
     * @param fragment    the fragment
     * @param owner       the owner
     * @param permissions the permissions
     * @return the permission observable
     */
    public static ObservableLife<Permission> getPermissionObservable(Fragment fragment, LifecycleOwner owner, String[] permissions) {
        return new RxPermissions(fragment).requestEach(permissions).to(RxLife.to(owner));
    }

    /**
     * Gets permission observable.
     *
     * @param activity    the activity
     * @param view        the view
     * @param permissions the permissions
     * @return the permission observable
     */
    public static ObservableLife<Permission> getPermissionObservable(FragmentActivity activity, View view, String[] permissions) {
        return new RxPermissions(activity).requestEach(permissions).to(RxLife.to(view));
    }

    /**
     * Gets permission observable.
     *
     * @param fragment    the fragment
     * @param view        the view
     * @param permissions the permissions
     * @return the permission observable
     */
    public static ObservableLife<Permission> getPermissionObservable(Fragment fragment, View view, String[] permissions) {
        return new RxPermissions(fragment).requestEach(permissions).to(RxLife.to(view));
    }

    /**
     * Deal permission.
     *
     * @param activity    the activity
     * @param view        the view
     * @param permissions the permissions
     * @param callBack    the call back
     */
    public static void dealPermission(FragmentActivity activity, View view, String[] permissions, PermissionCallBack callBack) {
        final List<String> disagree = new ArrayList<>();
        final List<String> ban = new ArrayList<>();
        getPermissionObservable(activity, view, permissions).subscribe(getObserver(disagree, ban, callBack));
    }

    /**
     * Deal permission.
     *
     * @param fragment    the fragment
     * @param view        the view
     * @param permissions the permissions
     * @param callBack    the call back
     */
    public static void dealPermission(Fragment fragment, View view, String[] permissions, PermissionCallBack callBack) {
        final List<String> disagree = new ArrayList<>();
        final List<String> ban = new ArrayList<>();
        getPermissionObservable(fragment, view, permissions).subscribe(getObserver(disagree, ban, callBack));
    }

    /**
     * Deal permission.
     *
     * @param fragment    the fragment
     * @param owner       the owner
     * @param permissions the permissions
     * @param callBack    the call back
     */
    public static void dealPermission(Fragment fragment, LifecycleOwner owner, String[] permissions, PermissionCallBack callBack) {
        final List<String> disagree = new ArrayList<>();
        final List<String> ban = new ArrayList<>();
        getPermissionObservable(fragment, owner, permissions).subscribe(getObserver(disagree, ban, callBack));
    }

    /**
     * Deal permission.
     *
     * @param activity    the activity
     * @param owner       the owner
     * @param permissions the permissions
     * @param callBack    the call back
     */
    public static void dealPermission(FragmentActivity activity, LifecycleOwner owner, String[] permissions, PermissionCallBack callBack) {
        final List<String> disagree = new ArrayList<>();
        final List<String> ban = new ArrayList<>();
        getPermissionObservable(activity, owner, permissions).subscribe(getObserver(disagree, ban, callBack));
    }

    /**
     * Dynamic add permission string [ ].
     *
     * @param oldPermissionGroup the old permission group
     * @param permission         the permission
     * @return the string [ ]
     */
    public static String[] dynamicAddPermission(String[] oldPermissionGroup, String... permission) {
        List<String> tempPermission = new ArrayList<>();
        List<String> oldGroup = Arrays.asList(oldPermissionGroup);
        List<String> newGroup = Arrays.asList(permission);
        tempPermission.addAll(oldGroup);
        tempPermission.addAll(newGroup);
        return tempPermission.toArray(new String[]{});
    }

    /**
     * Gets observer.
     *
     * @param disagree the disagree
     * @param ban      the ban
     * @param callBack the call back
     * @return the observer
     */
    private static Observer<Permission> getObserver(List<String> disagree, List<String> ban, PermissionCallBack callBack) {
        return new Observer<Permission>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

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
                    return;
                } else if (!ban.isEmpty()) {
                    if (callBack != null) {
                        callBack.ban(ban);
                    }
                } else {
                    if (callBack != null) {
                        callBack.disagree(disagree);
                    }
                }
                if (callBack != null) {
                    callBack.fail("暂无权限", null);
                }
            }
        };
    }

    /**
     * Check permission boolean.
     *
     * @param context the context
     * @param name    the name
     * @return the boolean
     */
    public static boolean checkHasPermission(Context context, String name) {
        return ContextCompat.checkSelfPermission(context, name) == PackageManager.PERMISSION_GRANTED;
    }
}
