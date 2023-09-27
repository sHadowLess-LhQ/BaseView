package cn.com.shadowless.baseview.base;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewbinding.ViewBinding;

import com.rxjava.rxlife.RxLife;

import java.util.ArrayList;
import java.util.List;

import cn.com.shadowless.baseview.R;
import cn.com.shadowless.baseview.callback.InitDataCallBack;
import cn.com.shadowless.baseview.callback.PermissionCallBack;
import cn.com.shadowless.baseview.permission.Permission;
import cn.com.shadowless.baseview.permission.RxPermissions;
import cn.com.shadowless.baseview.utils.ClickUtils;
import cn.com.shadowless.baseview.utils.PermissionUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 基类Activity
 *
 * @param <VB> the type 视图
 * @param <T>  the type 传递数据类型
 * @author sHadowLess
 */
public abstract class BaseActivity<VB extends ViewBinding, T> extends AppCompatActivity implements ObservableOnSubscribe<T>, Observer<T>, View.OnClickListener {

    /**
     * 视图绑定
     */
    private VB bind = null;

    /**
     * The Is only complete.
     */
    private volatile boolean isOnlyComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        int customTheme = initTheme();
        if (-1 != customTheme) {
            setTheme(customTheme);
        }
        super.onCreate(savedInstanceState);
        initBindView();
        initListener();
        initPermissionData();
    }

    @Override
    protected void onDestroy() {
        if (null != bind) {
            bind = null;
        }
        super.onDestroy();
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<T> emitter) throws Exception {
        initData(new InitDataCallBack<T>() {
            @Override
            public void initSuccessViewWithData(@NonNull T t) {
                isOnlyComplete = false;
                emitter.onNext(t);
                emitter.onComplete();
            }

            @Override
            public void initSuccessViewWithOutData() {
                isOnlyComplete = true;
                emitter.onComplete();
            }

            @Override
            public void initFailView(Throwable e) {
                emitter.onError(e);
            }
        });
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull T mData) {
        initSuccessView(mData);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        initFailView(e);
    }

    @Override
    public void onComplete() {
        if (isOnlyComplete) {
            initSuccessView(null);
        }
    }

    @Override
    public void onClick(View v) {
        if (!ClickUtils.isFastClick()) {
            click(v);
        }
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
    protected abstract VB setBindView();

    /**
     * 初始化监听
     */
    protected abstract void initListener();

    /**
     * 初始化数据
     *
     * @param callBack the call back
     */
    protected abstract void initData(@NonNull InitDataCallBack<T> callBack);

    /**
     * 初始化视图
     *
     * @param data the 数据表
     */
    protected abstract void initSuccessView(@Nullable T data);

    /**
     * Init fail view.
     *
     * @param e the e
     */
    protected abstract void initFailView(@Nullable Throwable e);

    /**
     * 点击
     *
     * @param v the v
     */
    protected abstract void click(@NonNull View v);

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
     * 设置调度器
     *
     * @return the scheduler
     */
    protected Scheduler[] setScheduler() {
        return new Scheduler[]{
                AndroidSchedulers.mainThread(),
                AndroidSchedulers.mainThread()
        };
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
        PermissionUtils.getPermissionObservable(this, this, permissions)
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
                                   Toast.makeText(BaseActivity.this, "处理权限错误", Toast.LENGTH_SHORT).show();
                               }

                               @Override
                               public void onComplete() {
                                   if (ban.isEmpty() && disagree.isEmpty()) {
                                       if (callBack != null) {
                                           callBack.agree();
                                       }
                                       dealDataToView();
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
    private void initPermissionData() {
        String[] permissions = permissions();
        if (null == permissions || permissions.length == 0) {
            dealDataToView();
            return;
        }
        initPermission(permissions);
    }

    /**
     * Deal data.
     */
    private void dealDataToView() {
        Observable.create(this).compose(dealWithThreadMode(setScheduler())).as(RxLife.as(this)).subscribe(this);
    }

    /**
     * 初始化数据所在线程
     *
     * @param <TF>      the type parameter
     * @param scheduler the scheduler
     * @return the 线程模式
     */
    private <TF> ObservableTransformer<TF, TF> dealWithThreadMode(Scheduler[] scheduler) {
        return upstream -> upstream.subscribeOn(scheduler[0])
                .unsubscribeOn(scheduler[0])
                .observeOn(scheduler[1]);
    }

    /**
     * 初始化视图
     */
    private void initBindView() {
        bind = setBindView();
        setContentView(bind.getRoot());
    }
}