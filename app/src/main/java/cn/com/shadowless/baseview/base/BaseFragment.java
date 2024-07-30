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

import java.lang.reflect.InvocationTargetException;

import cn.com.shadowless.baseview.event.ViewPublicEvent;
import cn.com.shadowless.baseview.utils.AsyncViewBindingInflate;

/**
 * 基类Fragment
 *
 * @param <VB> the type 视图
 * @author sHadowLess
 */
public abstract class BaseFragment<VB extends ViewBinding> extends Fragment implements
        ViewPublicEvent.InitViewBinding<VB>, ViewPublicEvent.InitEvent,
        ViewPublicEvent.InitViewClick, ViewPublicEvent.InitFragmentEvent {

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
    private ViewPublicEvent.InitViewBinding.AsyncLoadViewCallBack callBack;

    /**
     * The Main handler.
     */
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 是否懒加载标识符
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
                mainHandler.postDelayed(this::initEvent, 100);
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
                                callBack = initSyncView();
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
     * 获取懒加载状态
     *
     * @return the boolean
     */
    protected boolean isLazyInitSuccess() {
        return isLazyInitSuccess;
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
     * 获取加载模式
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
     * 获取视图
     *
     * @return the inflate view
     */
    private View getInflateView() {
        try {
            bind = inflateView(this, getLayoutInflater());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("视图无法反射初始化，若动态布局请检查setBindViewClass是否传入或重写inflateView手动实现ViewBinding创建" + Log.getStackTraceString(e));
        }
        return bind.getRoot();
    }

    /**
     * 同步加载布局
     *
     * @param viewGroup the view group
     */
    private void initSync(ViewGroup viewGroup) {
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
    }

    /**
     * 异步加载布局
     *
     * @param group the group
     */
    private void initAsync(ViewGroup group) {
        if (callBack != null) {
            callBack.showLoadView();
        }
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AsyncViewBindingInflate<VB> asyncViewBindingInflate = new AsyncViewBindingInflate<>(getAttachActivity());
                asyncViewBindingInflate.inflate(initGenericsClass(BaseFragment.this), group,
                        new AsyncViewBindingInflate.OnInflateFinishedListener<VB>() {
                            @Override
                            public void onInflateFinished(@NonNull VB binding, @Nullable ViewGroup parent) {
                                if (callBack != null) {
                                    callBack.dismissLoadView();
                                }
                                bind = binding;
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
                        });
            }
        }, 500);
    }

    /**
     * Init.
     */
    private void initEvent() {
        initObject();
        initView();
        initViewListener();
        initPermissionAndInitData(this);
    }
}
