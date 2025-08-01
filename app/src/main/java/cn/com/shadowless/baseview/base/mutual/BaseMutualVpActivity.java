package cn.com.shadowless.baseview.base.mutual;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import cn.com.shadowless.baseview.base.view.BaseVpActivity;
import cn.com.shadowless.baseview.manager.MultiDataViewDataManager;
import cn.com.shadowless.baseview.utils.AsyncViewBindingInflate;

/**
 * 双向等待基类Activity
 *
 * @param <VB> the type 视图
 * @author sHadowLess
 */
public abstract class BaseMutualVpActivity<VB extends ViewBinding> extends BaseVpActivity<VB> {

    /**
     * 双向等待管理
     */
    private MultiDataViewDataManager<VB> manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isAsyncLoadView()) {
            manager = new MultiDataViewDataManager<>();
            manager.reset();
            manager.resetAllDataState();
            manager.bindLifecycle(this);
            return;
        }
        super.onCreate(savedInstanceState);
    }

    /**
     * 异步加载布局
     */
    @Override
    public void asyncInitView(Bundle savedInstanceState) {
        callBack = AsyncLoadView();
        if (callBack != null) {
            callBack.showLoadView();
        }
        AsyncViewBindingInflate<VB> asyncViewBindingInflate = new AsyncViewBindingInflate<>(this);
        asyncViewBindingInflate.inflate(initViewBindingGenericsClass(BaseMutualVpActivity.this), null,
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
                        manager.setViewBinding(bind);
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

    @Nullable
    protected MultiDataViewDataManager<VB> getBindManager() {
        return manager;
    }
}