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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;

import cn.com.shadowless.baseview.event.ViewPublicEvent;
import cn.com.shadowless.baseview.utils.AsyncViewBindingInflate;

/**
 * The type Base dialog fragment.
 *
 * @param <VB> the type parameter
 * @author sHadowLess
 */
public abstract class BaseDialogFragment<VB extends ViewBinding> extends DialogFragment implements
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
    public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        LoadMode mode = getLoadMode();
        switch (mode) {
            case ONLY_LAZY_DATA:
                return getInflateView();
            case LAZY_VIEW_AND_DATA:
                return new FrameLayout(getAttachActivity());
            default:
                View defaultView = getInflateView();
                mainHandler.postDelayed(() -> initEvent(this.savedInstanceState), 100);
                return defaultView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadMode mode = getLoadMode();
        if (mode != LoadMode.DEFAULT && this.isAdded()) {
            requireView().post(() -> {
                if (this.isAdded()) {
                    //防止多次加载标志位
                    if (!isLazyInit) {
                        isLazyInit = true;
                        switch (mode) {
                            case ONLY_LAZY_DATA:
                                initEvent(savedInstanceState);
                                break;
                            case LAZY_VIEW_AND_DATA:
                                //是否异步加载
                                if (isAsyncLoad()) {
                                    asyncInitView(savedInstanceState);
                                    return;
                                }
                                syncInitView(savedInstanceState);
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
    public final VB getBindView() {
        return bind;
    }

    /**
     * 获取绑定的activity
     *
     * @return the bind activity
     */
    @Override
    public final Activity getAttachActivity() {
        return mActivity;
    }

    /**
     * 获取懒加载状态
     *
     * @return the boolean
     */
    @Override
    public final boolean isLazyInitSuccess() {
        return isLazyInitSuccess;
    }

    /**
     * 获取视图
     *
     * @return the inflate view
     */
    @Override
    public View getInflateView() {
        try {
            bind = inflateView(this, getLayoutInflater());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("视图无法反射初始化，若动态布局请检查setBindViewClass是否传入或重写inflateView手动实现ViewBinding创建\n" + Log.getStackTraceString(e));
        }
        return bind.getRoot();
    }

    /**
     * 同步加载布局
     */
    @Override
    public void syncInitView(Bundle savedInstanceState) {
        ViewGroup group = (ViewGroup) requireView();
        View contentView = getInflateView();
        group.addView(contentView);
        initEvent(savedInstanceState);
    }

    /**
     * 异步加载布局
     */
    @Override
    public void asyncInitView(Bundle savedInstanceState) {
        ViewGroup group = (ViewGroup) requireView();
        callBack = AsyncLoadView();
        if (callBack != null) {
            callBack.showLoadView();
        }
        AsyncViewBindingInflate<VB> asyncViewBindingInflate = new AsyncViewBindingInflate<>(getAttachActivity());
        asyncViewBindingInflate.inflate(initViewBindingGenericsClass(BaseDialogFragment.this), group,
                new AsyncViewBindingInflate.OnInflateFinishedListener<VB>() {
                    @Override
                    public void onInflateFinished(@NonNull VB binding, @Nullable ViewGroup parent) {
                        bind = binding;
                        View view = bind.getRoot();
                        if (callBack != null) {
                            callBack.dismissLoadView();
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

    /**
     * Init.
     */
    @Override
    public final void initEvent(Bundle savedInstanceState) {
        initObject(savedInstanceState);
        initView();
        initViewListener();
        initData();
        initPermissionAndInitData(this);
        isLazyInitSuccess = true;
    }
}
