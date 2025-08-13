package cn.com.shadowless.baseview.base.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import cn.com.shadowless.baseview.base.widget.BaseViewModel;
import cn.com.shadowless.baseview.event.ViewPublicEvent;
import cn.com.shadowless.baseview.manager.VmObjManager;
import cn.com.shadowless.baseview.utils.AsyncViewBindingInflate;

/**
 * 基类Activity
 *
 * @param <VB> the type 视图
 * @author sHadowLess
 */
public abstract class BaseVmActivity<VB extends ViewBinding> extends AppCompatActivity
        implements ViewPublicEvent.InitViewBinding<VB>, ViewPublicEvent.InitViewModel<VB>,
        ViewPublicEvent.InitModelEvent, ViewPublicEvent.InitViewClick {

    /**
     * 视图绑定
     */
    protected VB bind = null;

    /**
     * The Call back.
     */
    protected AsyncLoadViewCallBack callBack;

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
    protected void onCreate(Bundle savedInstanceState) {
        int customTheme = initTheme();
        if (-1 != customTheme) {
            setTheme(customTheme);
        }
        super.onCreate(savedInstanceState);
        initObject(savedInstanceState);
        manager = new VmObjManager<>();
        manager.setCurrentActivity(this);
        manager.setCurrentLifecycleOwner(this);
        tempList = collectionViewModels();
        for (BaseViewModel<VB, ?> model : tempList) {
            model.setObjManager(manager);
            model.onModelCreated();
            model.onModelInitListener();
        }
        if (isAsyncLoad()) {
            asyncInitView();
            return;
        }
        syncInitView();
    }

    @Override
    protected void onDestroy() {
        if (null != bind) {
            bind = null;
        }
        super.onDestroy();
    }

    /**
     * 同步加载布局
     */
    @Override
    public void syncInitView() {
        try {
            bind = inflateView(this, getLayoutInflater());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("视图无法反射初始化，若动态布局请检查setBindViewClass是否传入或重写inflateView手动实现ViewBinding创建\n" + Log.getStackTraceString(e));
        }
        manager.setCurrentViewBinding(bind);
        for (BaseViewModel<VB, ?> model : tempList) {
            model.onModelInitView();
        }
        setContentView(bind.getRoot());
        initEvent();
    }

    /**
     * 异步加载布局
     */
    @Override
    public void asyncInitView() {
        callBack = AsyncLoadView();
        if (callBack != null) {
            callBack.showLoadView();
        }
        AsyncViewBindingInflate<VB> asyncViewBindingInflate = new AsyncViewBindingInflate<>(this);
        asyncViewBindingInflate.inflate(initViewBindingGenericsClass(BaseVmActivity.this), null,
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
                                    setContentView(view);
                                }

                                @Override
                                public void animEnd() {
                                    initEvent();
                                }
                            });
                            return;
                        }
                        setContentView(view);
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
     * 获取绑定的视图
     *
     * @return the 视图
     */
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
        for (BaseViewModel<VB, ?> model : tempList) {
            model.onModelInitData();
        }
    }

    @Override
    public final void initModelDataByPermission() {
        for (BaseViewModel<VB, ?> model : tempList) {
            model.onModelInitDataByPermission();
        }
    }

    /**
     * 初始化主题
     *
     * @return the int
     */
    protected int initTheme() {
        return -1;
    }
}