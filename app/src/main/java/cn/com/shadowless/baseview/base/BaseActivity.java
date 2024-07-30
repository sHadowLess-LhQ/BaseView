package cn.com.shadowless.baseview.base;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;

import cn.com.shadowless.baseview.event.ViewPublicEvent;
import cn.com.shadowless.baseview.utils.AsyncViewBindingInflate;

/**
 * 基类Activity
 *
 * @param <VB> the type 视图
 * @author sHadowLess
 */
public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity implements
        ViewPublicEvent.InitViewBinding<VB>, ViewPublicEvent.InitEvent, ViewPublicEvent.InitViewClick {

    /**
     * 视图绑定
     */
    private VB bind = null;

    /**
     * The Call back.
     */
    private ViewPublicEvent.InitViewBinding.AsyncLoadViewCallBack callBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int customTheme = initTheme();
        if (-1 != customTheme) {
            setTheme(customTheme);
        }
        super.onCreate(savedInstanceState);
        boolean isAsync = isAsyncLoadView();
        if (isAsync) {
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
     * 同步加载布局
     */
    private void syncInitView() {
        try {
            bind = inflateView(this, getLayoutInflater());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("视图无法反射初始化，若动态布局请检查setBindViewClass是否传入或重写inflateView手动实现ViewBinding创建" + Log.getStackTraceString(e));
        }
        setContentView(bind.getRoot());
        initObject();
        initView();
        initViewListener();
        initPermissionAndInitData(this);
    }

    /**
     * 异步加载布局
     */
    private void asyncInitView() {
        callBack = initSyncView();
        if (callBack != null) {
            callBack.showLoadView();
        }
        AsyncViewBindingInflate<VB> asyncViewBindingInflate = new AsyncViewBindingInflate<>(this);
        asyncViewBindingInflate.inflate(initGenericsClass(this), null,
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
                                        setContentView(bind.getRoot());
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        initObject();
                                        initView();
                                        initViewListener();
                                        initPermissionAndInitData(BaseActivity.this);
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
                        throw new RuntimeException("异步加载视图错误：" + Log.getStackTraceString(e));
                    }
                });
    }
}