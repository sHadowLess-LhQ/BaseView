package cn.com.shadowless.baseview.base.mutual;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;

import cn.com.shadowless.baseview.base.widget.BaseViewModel;
import cn.com.shadowless.baseview.event.ViewPublicEvent;
import cn.com.shadowless.baseview.manager.ViewDataManager;
import cn.com.shadowless.baseview.utils.AsyncViewBindingInflate;

/**
 * 基类Activity
 *
 * @param <VB> the type 视图
 * @author sHadowLess
 */
public abstract class BaseMutualVmActivity<T, VB extends ViewBinding> extends AppCompatActivity
        implements ViewPublicEvent.InitViewBinding<VB>, ViewPublicEvent.InitViewModel<VB>,
        ViewPublicEvent.InitModelEvent, ViewPublicEvent.InitViewClick {

    /**
     * 视图绑定
     */
    private VB bind = null;

    /**
     * The Call back.
     */
    private AsyncLoadViewCallBack callBack;

    /**
     * 是否懒加载成功标识符
     */
    private boolean isLazyInitSuccess = false;

    /**
     * 双向等待管理
     */
    private ViewDataManager<T, VB> manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int customTheme = initTheme();
        if (-1 != customTheme) {
            setTheme(customTheme);
        }
        super.onCreate(savedInstanceState);
        if (isAsyncLoadView()) {
            manager = new ViewDataManager<>();
            manager.reset();
            manager.bindLifecycle(this);
            asyncInitView(savedInstanceState);
            return;
        }
        syncInitView(savedInstanceState);
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
    public void syncInitView(Bundle savedInstanceState) {
        try {
            bind = inflateView(this, getLayoutInflater());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("视图无法反射初始化，若动态布局请检查setBindViewClass是否传入或重写inflateView手动实现ViewBinding创建\n" + Log.getStackTraceString(e));
        }
        setContentView(bind.getRoot());
        initEvent(savedInstanceState);
    }

    /**
     * 异步加载布局
     */
    @Override
    public void asyncInitView(Bundle savedInstanceState) {
        callBack = initSyncView();
        if (callBack != null) {
            callBack.showLoadView();
        }
        AsyncViewBindingInflate<VB> asyncViewBindingInflate = new AsyncViewBindingInflate<>(this);
        asyncViewBindingInflate.inflate(initViewBindingGenericsClass(this), null,
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
                                    setContentView(bind.getRoot());
                                    manager.setViewBinding(bind);
                                }

                                @Override
                                public void animEnd() {

                                }
                            });
                            return;
                        }
                        setContentView(bind.getRoot());
                        manager.setViewBinding(binding);
                    }

                    @Override
                    public void onInflateError(Exception e) {
                        if (callBack != null) {
                            callBack.dismissLoadView();
                        }
                        throw new RuntimeException("异步加载视图错误：\n" + Log.getStackTraceString(e));
                    }
                });
        initEvent(savedInstanceState);
    }

    @Override
    public void initEvent(Bundle savedInstanceState) {
        initObject(savedInstanceState);
        initView();
        initViewListener();
        initPermissionAndInitData(this);
        isLazyInitSuccess = true;
    }

    /**
     * 获取绑定的视图
     *
     * @return the 视图
     */
    @Override
    public VB getBindView() {
        return bind;
    }

    /**
     * 获取懒加载状态
     *
     * @return the boolean
     */
    @Override
    public boolean isLazyInitSuccess() {
        return isLazyInitSuccess;
    }

    @Override
    public void initModelObserve() {
        initModelListener();
        for (BaseViewModel<?, ?> model : setViewModels()) {
            model.onModelInitData();
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

    protected ViewDataManager<T, VB> getBindManager() {
        if (!isAsyncLoadView()) {
            throw new RuntimeException("请在异步加载视图模式下使用");
        }
        return manager;
    }
}