package cn.com.shadowless.baseview.base.mutual;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import cn.com.shadowless.baseview.base.view.BaseVpFragment;
import cn.com.shadowless.baseview.manager.MultiDataViewDataManager;
import cn.com.shadowless.baseview.utils.AsyncViewBindingInflate;

/**
 * 双向等待基类Fragment
 *
 * @param <VB> the type 视图
 * @author sHadowLess
 */
public abstract class BaseMutualVpFragment<VB extends ViewBinding> extends BaseVpFragment<VB> {

    /**
     * 双向等待管理
     */
    private MultiDataViewDataManager manager;

    @Override
    public void onResume() {
        if (isAsyncLoad()) {
            manager = null;
            manager = new MultiDataViewDataManager();
            manager.reset();
            manager.bindLifecycleOwner(this);
        }
        super.onResume();
    }

    /**
     * 异步加载布局
     */
    @Override
    public final void asyncInitView() {
        ViewGroup group = (ViewGroup) requireView();
        callBack = AsyncLoadView();
        if (callBack != null) {
            callBack.showLoadView();
        }
        AsyncViewBindingInflate<VB> asyncViewBindingInflate = new AsyncViewBindingInflate<>(getAttachActivity());
        asyncViewBindingInflate.inflate(initViewBindingGenericsClass(BaseMutualVpFragment.this), group,
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
                                    initView();
                                    initViewListener();
                                    isLazyInitSuccess = true;
                                    manager.setViewBinding();
                                }

                                @Override
                                public void animEnd() {
                                }
                            });
                            return;
                        }
                        group.addView(view);
                        initView();
                        initViewListener();
                        isLazyInitSuccess = true;
                        manager.setViewBinding();
                    }

                    @Override
                    public void onInflateError(Exception e) {
                        if (callBack != null) {
                            callBack.dismissLoadView();
                        }
                        throw new RuntimeException("异步加载视图错误：\n" + Log.getStackTraceString(e));
                    }
                });
        initDataListener();
        initData();
        initPermissionAndInitData(this);
    }

    @Nullable
    protected final MultiDataViewDataManager getBindManager() {
        return manager;
    }
}
