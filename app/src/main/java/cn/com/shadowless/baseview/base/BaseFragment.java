package cn.com.shadowless.baseview.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;


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
 * 基类Fragment
 *
 * @param <VB> the type 视图
 * @author sHadowLess
 */
public abstract class BaseFragment<VB extends ViewBinding> extends Fragment implements
        AntiShakingOnClickListener {

    /**
     * 视图绑定
     */
    private VB bind = null;

    /**
     * 依附的activity
     */
    private Activity mActivity = null;

    /**
     * The Call back.
     */
    private AsyncLoadViewCallBack callBack;

    /**
     * The Main handler.
     */
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * The Is first.
     */
    private boolean isLazyInitSuccess = false;

    /**
     * The enum Lazy mode.
     */
    protected enum LoadMode {
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirst();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LoadMode mode = getLoadMode();
        switch (mode) {
            case ONLY_LAZY_DATA:
                return getInflateView();
            case LAZY_VIEW_AND_DATA:
                return new FrameLayout(getAttachActivity());
            default:
                View defaultView = getInflateView();
                initEvent();
                return defaultView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadMode mode = getLoadMode();
        if (mode != LoadMode.DEFAULT && this.isAdded()) {
            mainHandler.postDelayed(() -> {
                if (this.isAdded()) {
                    //防止多次加载标志位
                    if (!isLazyInitSuccess) {
                        isLazyInitSuccess = true;
                        switch (mode) {
                            case ONLY_LAZY_DATA:
                                initEvent();
                                break;
                            case LAZY_VIEW_AND_DATA:
                                //获取空布局
                                FrameLayout layout = (FrameLayout) getView();
                                //是否异步加载
                                if (isAsyncLoadView()) {
                                    initAsync(layout);
                                    return;
                                }
                                initSync(layout);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }, 10);
        }
    }

    @Override
    public void onDestroyView() {
        if (bind != null) {
            bind = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    /**
     * Is lazy init boolean.
     *
     * @return the boolean
     */
    protected boolean isLazyInitSuccess() {
        return isLazyInitSuccess;
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
     * Inflate view vb.
     *
     * @return the vb
     */
    protected VB inflateView() {
        try {
            return ViewBindingUtils.inflate(setBindViewClass().getName(), getLayoutInflater());
        } catch (Exception e) {
            throw new RuntimeException("视图无法反射初始化，请检查setBindViewClassName是否传入绝对路径或重写自实现inflateView方法捕捉堆栈" + Log.getStackTraceString(e));
        }
    }

    /**
     * Gets inflate view.
     *
     * @return the inflate view
     */
    private View getInflateView() {
        bind = inflateView();
        return bind.getRoot();
    }

    /**
     * 获取绑定视图
     *
     * @return the bind
     */
    @NonNull
    public VB getBindView() {
        return bind;
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
     * Gets load mode.
     *
     * @return the load mode
     */
    protected LoadMode getLoadMode() {
        return LoadMode.DEFAULT;
    }

    /**
     * 获取绑定的activity
     *
     * @return the bind activity
     */
    protected Activity getAttachActivity() {
        return mActivity;
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
     * Init sync.
     *
     * @param viewGroup the view group
     */
    private void initSync(ViewGroup viewGroup) {
        if (callBack != null) {
            callBack.showLoadView();
        }
        mainHandler.postDelayed(() -> {
            View contentView = getInflateView();
            contentView.setAlpha(0);
            contentView
                    .animate()
                    .alpha(0)
                    .alpha(1)
                    .setDuration(500)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            super.onAnimationStart(animation);
                            viewGroup.addView(contentView);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            initEvent();
                        }
                    })
                    .setInterpolator(new LinearInterpolator())
                    .start();
            if (callBack != null) {
                callBack.dismissLoadView();
            }
        }, 800);
    }

    /**
     * Init async.
     *
     * @param group the group
     */
    private void initAsync(ViewGroup group) {
        if (callBack != null) {
            callBack.showLoadView();
        }
        mainHandler.postDelayed(() ->
                new AsyncViewBindingInflate(getAttachActivity()).inflate(setBindViewClass().getName(), group, new AsyncViewBindingInflate.OnInflateFinishedListener() {
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
                                        group.addView(view);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        initEvent();
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
                        TextView textView = new TextView(getAttachActivity());
                        String error = "异步加载视图错误：" + Log.getStackTraceString(e);
                        textView.setText(error);
                        textView.setTextColor(Color.RED);
                        group.addView(textView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                }), 500);
    }

    /**
     * Init.
     */
    private void initEvent() {
        initObject();
        initView();
        initViewListener();
        initPermissionAndInitData();
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
     * 碎片第一次创建
     */
    protected abstract void initFirst();

    /**
     * 初始化对象
     */
    protected abstract void initObject();

    /**
     * 初始化视图绑定数据监听
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
