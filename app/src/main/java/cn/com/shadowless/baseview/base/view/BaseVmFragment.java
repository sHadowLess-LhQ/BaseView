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
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import cn.com.shadowless.baseview.base.widget.BaseViewModel;
import cn.com.shadowless.baseview.event.ViewPublicEvent;
import cn.com.shadowless.baseview.manager.VmObjManager;
import cn.com.shadowless.baseview.utils.AsyncViewBindingInflate;

/**
 * 基类Fragment
 *
 * @param <VB> the type 视图
 * @author sHadowLess
 */
public abstract class BaseVmFragment<VB extends ViewBinding> extends Fragment
        implements ViewPublicEvent.InitViewBinding<VB>, ViewPublicEvent.InitViewModel<VB>,
        ViewPublicEvent.InitModelEvent, ViewPublicEvent.InitViewClick,
        ViewPublicEvent.InitFragmentEvent {

    /**
     * 视图绑定
     */
    protected VB bind = null;

    /**
     * 依附的activity
     */
    protected Activity mActivity = null;

    /**
     * The Call back.
     */
    protected AsyncLoadViewCallBack callBack;

    /**
     * The Main handler.
     */
    protected final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 是否懒加载标识符
     */
    protected boolean isLazyInit = false;

    /**
     * 是否懒加载成功标识符
     */
    protected boolean isLazyInitSuccess = false;

    /**
     * ViewModel所需对象管理
     */
    protected VmObjManager<VB> manager = null;

    /**
     * ViewModel对象集合
     */
    protected List<BaseViewModel<VB, ?>> tempList;

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
    public final View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initObject(savedInstanceState);
        manager = new VmObjManager<>();
        manager.setCurrentActivity(getAttachActivity());
        manager.setCurrentFragment(this);
        manager.setCurrentLifecycleOwner(this);
        tempList = collectionViewModels();
        for (BaseViewModel<VB, ?> model : tempList) {
            model.update(manager);
            model.onModelCreated();
            model.onModelInitListener();
        }
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
        if (mode != LoadMode.DEFAULT && isFragmentActive(this)) {
            requireView().post(() -> {
                if (isFragmentActive(this)) {
                    //防止多次加载标志位
                    if (!isLazyInit) {
                        isLazyInit = true;
                        switch (mode) {
                            case ONLY_LAZY_DATA:
                                initEvent();
                                break;
                            case LAZY_VIEW_AND_DATA:
                                //是否异步加载
                                if (isAsyncLoad()) {
                                    asyncInitView();
                                    return;
                                }
                                syncInitView();
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
     * 获取绑定的activity
     *
     * @return the bind activity
     */
    @Override
    public final Activity getAttachActivity() {
        return mActivity;
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
        manager.setCurrentViewBinding(bind);
        for (BaseViewModel<VB, ?> model : tempList) {
            model.onModelInitView();
        }
        return bind.getRoot();
    }

    /**
     * 同步加载布局
     */
    @Override
    public void syncInitView() {
        ViewGroup group = (ViewGroup) requireView();
        View contentView = getInflateView();
        group.addView(contentView);
        initEvent();
    }

    /**
     * 异步加载布局
     */
    @Override
    public void asyncInitView() {
        ViewGroup group = (ViewGroup) requireView();
        callBack = AsyncLoadView();
        if (callBack != null) {
            callBack.showLoadView();
        }
        AsyncViewBindingInflate<VB> asyncViewBindingInflate = new AsyncViewBindingInflate<>(getAttachActivity());
        asyncViewBindingInflate.inflate(initViewBindingGenericsClass(BaseVmFragment.this), group,
                new AsyncViewBindingInflate.OnInflateFinishedListener<VB>() {
                    @Override
                    public void onInflateFinished(@NonNull VB binding, @Nullable ViewGroup parent) {
                        bind = binding;
                        View view = bind.getRoot();
                        manager.setCurrentViewBinding(bind);
                        for (BaseViewModel<VB, ?> model : tempList) {
                            model.onModelInitView();
                        }
                        if (callBack != null) {
                            callBack.dismissLoadView();
                            callBack.startAsyncAnimSetView(view, new AsyncLoadViewAnimCallBack() {
                                @Override
                                public void animStart() {
                                    group.addView(view);
                                }

                                @Override
                                public void animEnd() {
                                    initEvent();
                                }
                            });
                            return;
                        }
                        group.addView(view);
                        initEvent();
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

    @Override
    public final void initEvent() {
        initView();
        initViewListener();
        initModelListener();
        initModelData();
        initPermissionAndInitData(this);
        isLazyInitSuccess = true;
    }

    /**
     * 获取绑定视图
     *
     * @return the bind
     */
    @NonNull
    @Override
    public final VB getBindView() {
        return bind;
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

    @Override
    public final void initModelData() {
        for (BaseViewModel<?, ?> model : tempList) {
            model.onModelInitData();
        }
    }

    @Override
    public final void initModelDataByPermission() {
        for (BaseViewModel<VB, ?> model : tempList) {
            model.onModelInitDataByPermission();
        }
    }
}
