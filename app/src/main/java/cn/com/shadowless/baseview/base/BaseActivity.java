package cn.com.shadowless.baseview.base;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;
import java.util.List;

import cn.com.shadowless.baseview.callback.PermissionCallBack;
import cn.com.shadowless.baseview.permission.Permission;
import cn.com.shadowless.baseview.utils.ClickUtils;
import cn.com.shadowless.baseview.utils.PermissionUtils;
import cn.com.shadowless.baseview.utils.ViewBindingUtils;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 基类Activity
 *
 * @param <VB> the type 视图
 * @author sHadowLess
 */
public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity implements View.OnClickListener, LifecycleEventObserver {

    /**
     * 视图绑定
     */
    private VB bind = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int customTheme = initTheme();
        if (-1 != customTheme) {
            setTheme(customTheme);
        }
        super.onCreate(savedInstanceState);
        getLifecycle().addObserver(this);
        initBindView();
        initViewListener();
    }

    @Override
    protected void onDestroy() {
        if (null != bind) {
            bind = null;
        }
        getLifecycle().removeObserver(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (!ClickUtils.isFastClick()) {
            click(v);
        }
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_RESUME) {
            initPermissionAndInitData();
        }
    }

    /**
     * 反射实例化ViewBinding
     *
     * @return the vb
     */
    protected VB inflateView() {
        try {
            return ViewBindingUtils.inflate(setBindViewClass().getName(), getLayoutInflater());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
     * 初始化权限
     */
    private void initPermissionAndInitData() {
        String[] permissions = permissions();
        if (null == permissions || permissions.length == 0) {
            initObject();
            initData();
            initView();
            return;
        }
        initPermission(permissions);
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
        PermissionUtils.getPermissionObservable(this, this, permissions).subscribe(new Observer<Permission>() {
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
                if (callBack != null) {
                    callBack.fail("处理权限错误", e);
                }
            }

            @Override
            public void onComplete() {
                if (ban.isEmpty() && disagree.isEmpty()) {
                    if (callBack != null) {
                        callBack.agree();
                    }
                    initObject();
                    initData();
                    initView();
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
        });
    }

    /**
     * 初始化视图
     */
    private void initBindView() {
        bind = inflateView();
        if (bind == null) {
            throw new RuntimeException("视图无法反射初始化，请检查setBindViewClassName是否传入正确或重写自实现inflateView方法");
        }
        setContentView(bind.getRoot());
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
    protected abstract Class<VB> setBindViewClass();

    /**
     * 初始化视图监听
     */
    protected abstract void initViewListener();

    /**
     * 初始化对象
     */
    protected abstract void initObject();

    /**
     * 给视图绑定数据
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 点击
     *
     * @param v the v
     */
    protected abstract void click(@NonNull View v);
}