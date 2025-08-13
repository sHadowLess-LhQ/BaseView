package cn.com.shadowless.baseview.base.mutual;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import cn.com.shadowless.baseview.base.view.BaseVmActivity;
import cn.com.shadowless.baseview.base.widget.BaseMutualViewModel;
import cn.com.shadowless.baseview.base.widget.BaseViewModel;
import cn.com.shadowless.baseview.utils.AsyncViewBindingInflate;

/**
 * 双向等待基类Activity
 *
 * @param <VB> the type 视图
 * @author sHadowLess
 */
public abstract class BaseMutualVmActivity<VB extends ViewBinding> extends BaseVmActivity<VB> {

    /**
     * 异步加载布局
     */
    @Override
    public final void asyncInitView(Bundle savedInstanceState) {
        callBack = AsyncLoadView();
        if (callBack != null) {
            callBack.showLoadView();
        }
        AsyncViewBindingInflate<VB> asyncViewBindingInflate = new AsyncViewBindingInflate<>(this);
        asyncViewBindingInflate.inflate(initViewBindingGenericsClass(BaseMutualVmActivity.this), null,
                new AsyncViewBindingInflate.OnInflateFinishedListener<VB>() {
                    @Override
                    public void onInflateFinished(@NonNull VB binding, @Nullable ViewGroup parent) {
                        bind = binding;
                        View view = bind.getRoot();
                        manager.setCurrentViewBinding(bind);
                        for (BaseViewModel<VB, ?> model : setViewModels()) {
                            model.onModelInitView();
                        }
                        if (callBack != null) {
                            callBack.dismissLoadView();
                            callBack.startAsyncAnimSetView(view, new AsyncLoadViewAnimCallBack() {
                                @Override
                                public void animStart() {
                                    setContentView(bind.getRoot());
                                    initView();
                                    initViewListener();
                                    isLazyInitSuccess = true;
                                    for (BaseViewModel<VB, ?> model : setViewModels()) {
                                        if (!(model instanceof BaseMutualViewModel)) {
                                            throw new RuntimeException("ViewModel请继承BaseMutualViewModel");
                                        }
                                        model.getViewDataManager().setViewBinding();
                                    }
                                }

                                @Override
                                public void animEnd() {

                                }
                            });
                            return;
                        }
                        setContentView(bind.getRoot());
                        initView();
                        initViewListener();
                        isLazyInitSuccess = true;
                        for (BaseViewModel<VB, ?> model : setViewModels()) {
                            if (!(model instanceof BaseMutualViewModel)) {
                                throw new RuntimeException("ViewModel请继承BaseMutualViewModel");
                            }
                            model.getViewDataManager().setViewBinding();
                        }
                    }

                    @Override
                    public void onInflateError(Exception e) {
                        if (callBack != null) {
                            callBack.dismissLoadView();
                        }
                        throw new RuntimeException("异步加载视图错误：\n" + Log.getStackTraceString(e));
                    }
                });
        initObject(savedInstanceState);
        initModelListener();
        initModelData();
        initPermissionAndInitData(this);
    }
}