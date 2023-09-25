package cn.com.shadowless.baseview.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.rxjava.rxlife.ObservableLife;
import com.rxjava.rxlife.RxLife;

import java.util.ArrayList;
import java.util.List;

import cn.com.shadowless.baseview.callback.PermissionCallBack;
import cn.com.shadowless.baseview.permission.Permission;
import cn.com.shadowless.baseview.permission.RxPermissions;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

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
     * Gets permission observable.
     *
     * @param activity    the activity
     * @param owner       the owner
     * @param permissions the permissions
     * @return the permission observable
     */
    public static ObservableLife<Permission> getPermissionObservable(FragmentActivity activity, LifecycleOwner owner, String[] permissions) {
        return new RxPermissions(activity)
                .requestEach(permissions)
                .as(RxLife.as(owner));
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
        return new RxPermissions(fragment)
                .requestEach(permissions)
                .as(RxLife.as(owner));
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
        return new RxPermissions(activity)
                .requestEach(permissions)
                .as(RxLife.as(view));
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
        return new RxPermissions(fragment)
                .requestEach(permissions)
                .as(RxLife.as(view));
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
        getPermissionObservable(activity, view, permissions)
                .subscribe(new Observer<Permission>() {
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
                });
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
        getPermissionObservable(fragment, view, permissions)
                .subscribe(new Observer<Permission>() {
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
                });
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
        getPermissionObservable(fragment, owner, permissions)
                .subscribe(new Observer<Permission>() {
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
                });
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
        getPermissionObservable(activity, owner, permissions)
                .subscribe(new Observer<Permission>() {
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
                });
    }
}
