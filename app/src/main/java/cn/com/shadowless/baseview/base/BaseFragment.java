package cn.com.shadowless.baseview.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.rxjava.rxlife.RxLife;

import java.util.ArrayList;
import java.util.List;

import cn.com.shadowless.baseview.callback.PermissionCallBack;
import cn.com.shadowless.baseview.permission.Permission;
import cn.com.shadowless.baseview.permission.RxPermissions;
import cn.com.shadowless.baseview.utils.ClickUtils;
import cn.com.shadowless.baseview.utils.ViewBindingUtils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 基类Fragment
 *
 * @param <VB> the type 视图
 * @author sHadowLess
 */
public abstract class BaseFragment<VB extends ViewBinding> extends Fragment implements View.OnClickListener {

    /**
     * 视图绑定
     */
    private VB bind = null;

    /**
     * 依附的activity
     */
    private Activity mActivity = null;

    /**
     * The Main handler.
     */
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * The Is first.
     */
    private boolean isFirst = true;

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
        bind = inflateView();
        if (bind == null) {
            throw new RuntimeException("视图无法反射初始化，请检查setBindViewClassName传是否入绝对路径或重写自实现inflateView方法");
        }
        initViewListener();
        initPermissionAndInitData();
        return bind.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.isAdded()) {
            if (isFirst) {
                isFirst = false;
                mainHandler.postDelayed(() -> {
                    if (this.isAdded()) {
                        initLazyData();
                    }
                }, 10);
            }
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

    @Override
    public void onClick(View v) {
        if (!ClickUtils.isFastClick()) {
            click(v);
        }
    }

    /**
     * Inflate view vb.
     *
     * @return the vb
     */
    protected VB inflateView() {
        try {
            return ViewBindingUtils.inflate(setBindViewClassName(), getLayoutInflater());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取绑定视图
     *
     * @return the bind
     */
    @NonNull
    public VB getBindView() {
        return bind;
    }

    /**
     * 获取绑定的activity
     *
     * @return the bind activity
     */
    protected Activity getAttachActivity() {
        return mActivity;
    }

    /**
     * Init permission.
     *
     * @param permissions the permissions
     */
    protected void initPermission(String[] permissions) {
        dealPermission(permissions, null);
    }

    /**
     * Deal permission.
     *
     * @param permissions the permissions
     * @param callBack    the call back
     */
    protected void dealPermission(String[] permissions, PermissionCallBack callBack) {
        final List<String> disagree = new ArrayList<>();
        final List<String> ban = new ArrayList<>();
        new RxPermissions(this)
                .requestEach(permissions)
                .as(RxLife.as(this))
                .subscribe(new Observer<Permission>() {
                               @Override
                               public void onSubscribe(@NonNull Disposable d) {

                               }

                               @Override
                               public void onNext(@NonNull Permission permission) {
                                   if (permission.shouldShowRequestPermissionRationale) {
                                       ban.add(permission.name);
                                   } else if (!permission.granted) {
                                       disagree.add(permission.name);
                                   }
                               }

                               @Override
                               public void onError(@NonNull Throwable e) {
                                   Toast.makeText(getAttachActivity(), "处理权限错误", Toast.LENGTH_SHORT).show();
                               }

                               @Override
                               public void onComplete() {
                                   if (ban.isEmpty() && disagree.isEmpty()) {
                                       if (callBack != null) {
                                           callBack.agree();
                                       }
                                       initData();
                                       bindDataToView();
                                   } else if (!ban.isEmpty()) {
                                       if (callBack != null) {
                                           callBack.ban(ban);
                                       }
                                   } else {
                                       if (callBack != null) {
                                           callBack.disagree(disagree);
                                       }
                                   }
                               }
                           }
                );
    }

    /**
     * 初始化权限
     */
    private void initPermissionAndInitData() {
        String[] permissions = permissions();
        if (null == permissions || permissions.length == 0) {
            initData();
            bindDataToView();
            return;
        }
        initPermission(permissions);
    }

    /**
     * 需要申请的权限
     *
     * @return the 权限组
     */
    @Nullable
    protected abstract String[] permissions();

    /**
     * 设置绑定视图
     *
     * @return the 视图
     */
    @NonNull
    protected abstract String setBindViewClassName();

    /**
     * 碎片第一次创建
     */
    protected abstract void initFirst();

    /**
     * 初始化视图监听
     */
    protected abstract void initViewListener();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化懒加载数据
     */
    protected abstract void initLazyData();

    /**
     * 给视图绑定数据
     */
    protected abstract void bindDataToView();

    /**
     * 点击
     *
     * @param v the v
     */
    protected abstract void click(@NonNull View v);
}
