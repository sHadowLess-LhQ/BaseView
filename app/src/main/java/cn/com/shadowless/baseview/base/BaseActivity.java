package cn.com.shadowless.baseview.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import cn.com.shadowless.baseview.callback.PermissionCallBack;
import cn.com.shadowless.baseview.click.AntiShakingOnClickListener;
import cn.com.shadowless.baseview.permission.Permission;
import cn.com.shadowless.baseview.utils.AsyncViewBindingInflate;
import cn.com.shadowless.baseview.utils.PermissionUtils;
import cn.com.shadowless.baseview.utils.ViewBindingUtils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 基类Activity
 *
 * @param <VB> the type 视图
 * @author sHadowLess
 */
public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity implements
        AntiShakingOnClickListener {

    /**
     * 视图绑定
     */
    private VB bind = null;

    /**
     * The Call back.
     */
    private AsyncLoadViewCallBack callBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int customTheme = initTheme();
        if (-1 != customTheme) {
            setTheme(customTheme);
        }
        super.onCreate(savedInstanceState);
        initBindView();
    }

    @Override
    protected void onDestroy() {
        if (null != bind) {
            bind = null;
        }
        super.onDestroy();
    }

    /**
     * 获取绑定的视图
     *
     * @return the 视图
     */
    @NonNull
    protected VB getBindView() {
        return bind;
    }

    /**
     * 初始化主题
     *
     * @return the int
     */
    protected int initTheme() {
        return -1;
    }

    /**
     * Init sync view async load view call back.
     *
     * @return the async load view call back
     */
    protected AsyncLoadViewCallBack initSyncView() {
        return null;
    }

    /**
     * Is async load view boolean.
     *
     * @return the boolean
     */
    protected boolean isAsyncLoadView() {
        return false;
    }

    /**
     * 反射实例化ViewBinding
     *
     * @return the vb
     * @throws ClassNotFoundException    the class not found exception
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws NoSuchMethodException     the no such method exception
     */
    protected VB inflateView() throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return ViewBindingUtils.inflate(setBindViewClass().getName(), getLayoutInflater());
    }

    /**
     * 初始化权限
     */
    private void initPermissionAndInitData() {
        String[] permissions = permissions();
        if (null == permissions || permissions.length == 0) {
            initData();
            initDataListener();
            return;
        }
        initPermission(permissions);
    }

    /**
     * Init permission.
     *
     * @param permissions the permissions
     */
    protected void initPermission(String[] permissions) {
        dealPermission(permissions, null);
    }


    /**
     * Deal permission.
     *
     * @param permissions the permissions
     * @param callBack    the call back
     */
    protected void dealPermission(String[] permissions, PermissionCallBack callBack) {
        final List<String> disagree = new ArrayList<>();
        final List<String> ban = new ArrayList<>();
        PermissionUtils.getPermissionObservable(this, this, permissions).subscribe(new Observer<Permission>() {
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
                    initData();
                    initDataListener();
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
     * 初始化视图
     */
    private void initBindView() {
        boolean isAsync = isAsyncLoadView();
        if (!isAsync) {
            try {
                bind = inflateView();
            } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException("视图无法反射初始化，请检查setBindViewClassName是否传入绝对路径或重写自实现inflateView方法捕捉堆栈" + Log.getStackTraceString(e));
            }
            setContentView(bind.getRoot());
            initObject();
            initView();
            initViewListener();
            initPermissionAndInitData();
            return;
        }
        callBack = initSyncView();
        if (callBack != null) {
            callBack.showLoadView();
        }
        new AsyncViewBindingInflate(this).inflate(setBindViewClass().getName(), null,
                new AsyncViewBindingInflate.OnInflateFinishedListener() {
                    @Override
                    public void onInflateFinished(@NonNull ViewBinding binding, @Nullable ViewGroup parent) {
                        if (callBack != null) {
                            callBack.dismissLoadView();
                        }
                        bind = (VB) binding;
                        View view = bind.getRoot();
                        view.setAlpha(0);
                        view
                                .animate()
                                .alpha(0)
                                .alpha(1)
                                .setDuration(500)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        super.onAnimationStart(animation);
                                        setContentView(bind.getRoot());
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        initObject();
                                        initView();
                                        initViewListener();
                                        initPermissionAndInitData();
                                    }
                                })
                                .setInterpolator(new LinearInterpolator())
                                .start();
                    }

                    @Override
                    public void onInflateError(Exception e) {
                        if (callBack != null) {
                            callBack.dismissLoadView();
                        }
                        TextView textView = new TextView(BaseActivity.this);
                        String error = "异步加载视图错误：" + Log.getStackTraceString(e);
                        textView.setText(error);
                        textView.setTextColor(Color.RED);
                        setContentView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
    }

    /**
     * 需要申请的权限
     *
     * @return the 权限组
     */
    @Nullable
    protected abstract String[] permissions();

    /**
     * 设置绑定视图
     *
     * @return the 视图
     */
    @NonNull
    protected abstract Class<VB> setBindViewClass();

    /**
     * 初始化对象
     */
    protected abstract void initObject();

    /**
     * 给视图绑定数据
     */
    protected abstract void initView();

    /**
     * 初始化视图监听
     */
    protected abstract void initViewListener();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * Bind data to view.
     */
    protected abstract void initDataListener();
}