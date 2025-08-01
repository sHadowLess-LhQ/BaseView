package cn.com.shadowless.baseview.base.mutual;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import cn.com.shadowless.baseview.base.view.BaseVmFragment;
import cn.com.shadowless.baseview.base.widget.BaseMutualViewModel;
import cn.com.shadowless.baseview.base.widget.BaseViewModel;
import cn.com.shadowless.baseview.utils.AsyncViewBindingInflate;

/**
 * 双向等待基类Fragment
 *
 * @param <VB> the type 视图
 * @author sHadowLess
 */
public abstract class BaseMutualVmFragment<VB extends ViewBinding> extends BaseVmFragment<VB> {

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
        asyncViewBindingInflate.inflate(initViewBindingGenericsClass(BaseMutualVmFragment.this), group,
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
                                    for (BaseViewModel<VB, ?> model : setViewModels()) {
                                        if (!(model instanceof BaseMutualViewModel)) {
                                            throw new RuntimeException("ViewModel请继承BaseMutualViewModel");
                                        }
                                        model.getViewDataManager().setViewBinding(bind);
                                    }
                                }

                                @Override
                                public void animEnd() {

                                }
                            });
                            return;
                        }
                        group.addView(view);
                        for (BaseViewModel<VB, ?> model : setViewModels()) {
                            if (!(model instanceof BaseMutualViewModel)) {
                                throw new RuntimeException("ViewModel请继承BaseMutualViewModel");
                            }
                            model.getViewDataManager().setViewBinding(bind);
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
        initEvent(savedInstanceState);
    }
}
