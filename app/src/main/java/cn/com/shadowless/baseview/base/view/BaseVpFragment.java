package cn.com.shadowless.baseview.base.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.MainThread;
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
public abstract class BaseVpFragment<VB extends ViewBinding> extends Fragment implements
        ViewPublicEvent.InitViewBinding<VB>, ViewPublicEvent.InitBindingEvent,
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
    private boolean isLazyInit = false;

    /**
     * 是否懒加载成功标识符
     */
    private boolean isLazyInitSuccess = false;

    /**
     * The Saved instance state.
     */
    private Bundle savedInstanceState;

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
        this.savedInstanceState = savedInstanceState;
        LoadMode mode = getLoadMode();
        switch (mode) {
            case ONLY_LAZY_DATA:
                return getInflateView();
            case LAZY_VIEW_AND_DATA:
                return new FrameLayout(getAttachActivity());
            default:
                View defaultView = getInflateView();
                initEvent(savedInstanceState, 100);
                return defaultView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadMode mode = getLoadMode();
        if (mode != LoadMode.DEFAULT && isFragmentActive(this)) {
            requireView().post(() -> {
                if (isFragmentActive(this)) {
                    //防止多次加载标志位
                    if (!isLazyInit) {
                        isLazyInit = true;
                        switch (mode) {
                            case ONLY_LAZY_DATA:
                                initEvent(savedInstanceState);
                                break;
                            case LAZY_VIEW_AND_DATA:
                                //获取空布局
                                FrameLayout layout = (FrameLayout) requireView();
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
            });
        }
    }

    @Override
    public void onDestroyView() {
        if (bind != null) {
            bind = null;
        }
        mainHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    /**
     * 获取绑定视图
     *
     * @return the bind
     */
    @Override
    public VB getBindView() {
        return bind;
    }

    /**
     * 获取绑定的activity
     *
     * @return the bind activity
     */
    @Override
    public Activity getAttachActivity() {
        return mActivity;
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
     * 获取视图
     *
     * @return the inflate view
     */
    private View getInflateView() {
        try {
            bind = inflateView(this, getLayoutInflater());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("视图无法反射初始化，若动态布局请检查setBindViewClass是否传入或重写inflateView手动实现ViewBinding创建\n" + Log.getStackTraceString(e));
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
        viewGroup.addView(contentView);
        initEvent(savedInstanceState);
    }

    /**
     * 异步加载布局
     *
     * @param group the group
     */
    private void initAsync(ViewGroup group) {
        callBack = initSyncView();
        if (callBack != null) {
            callBack.showLoadView();
        }
        AsyncViewBindingInflate<VB> asyncViewBindingInflate = new AsyncViewBindingInflate<>(getAttachActivity());
        asyncViewBindingInflate.inflate(initViewBindingGenericsClass(BaseVpFragment.this), group,
                new AsyncViewBindingInflate.OnInflateFinishedListener<VB>() {
                    @Override
                    public void onInflateFinished(@NonNull VB binding, @Nullable ViewGroup parent) {
                        if (callBack != null) {
                            callBack.dismissLoadView();
                        }
                        bind = binding;
                        View view = bind.getRoot();
                        if (callBack != null) {
                            callBack.startAsyncAnimSetView(view, new AsyncLoadViewAnimCallBack() {
                                @Override
                                public void animStart() {
                                    group.addView(view);
                                }

                                @Override
                                public void animEnd() {
                                    initEvent(savedInstanceState);
                                }
                            });
                            return;
                        }
                        group.addView(view);
                        initEvent(savedInstanceState);
                    }

                    @Override
                    public void onInflateError(Exception e) {
                        if (callBack != null) {
                            callBack.dismissLoadView();
                        }
                        throw new RuntimeException("异步加载视图错误：\n" + Log.getStackTraceString(e));
                    }
                });
    }

    @MainThread
    private void initEvent(Bundle savedInstanceState) {
        initEvent(savedInstanceState, 0);
    }

    /**
     * Init.
     *
     * @param savedInstanceState the saved instance state
     */
    @MainThread
    private void initEvent(Bundle savedInstanceState, int delay) {
        if (delay <= 0) {
            mainHandler.post(() -> {
                initObject(savedInstanceState);
                initView();
                initViewListener();
                initPermissionAndInitData(BaseVpFragment.this);
                isLazyInitSuccess = true;
            });
            return;
        }
        mainHandler.postDelayed(() -> {
            initObject(savedInstanceState);
            initView();
            initViewListener();
            initPermissionAndInitData(BaseVpFragment.this);
            isLazyInitSuccess = true;
        }, delay);
    }
}
