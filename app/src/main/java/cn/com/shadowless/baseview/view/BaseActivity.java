package cn.com.shadowless.baseview.view;

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

import cn.com.shadowless.baseview.R;
import cn.com.shadowless.baseview.permission.RxPermissions;
import cn.com.shadowless.baseview.utils.ClickUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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
     * 初始化数据回调接口
     *
     * @param <T> the type parameter
     */
    protected interface InitDataCallBack<T> {
        /**
         * 成功且带数据
         *
         * @param t the t
         */
        void initViewWithData(@NonNull T t);

        /**
         * 成功不带数据
         */
        void initViewWithOutData();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(initTheme());
        super.onCreate(savedInstanceState);
        initBindView();
        initListener();
        initPermissions();
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
            public void initViewWithData(@NonNull T t) {
                emitter.onNext(t);
            }

            @Override
            public void initViewWithOutData() {
                emitter.onComplete();
            }
        });
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull T mData) {
        initView(mData);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        initView(null);
    }

    @Override
    public void onComplete() {
        initView(null);
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
    protected abstract String[] permissionName();

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
    protected abstract void initView(@Nullable T data);

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
        return R.style.MyAppTheme;
    }

    /**
     * 初始化数据所在线程
     *
     * @param <T> the type parameter
     * @return the 线程模式
     */
    protected <T> ObservableTransformer<T, T> dealWithThreadMode() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 初始化权限
     */
    private void initPermissions() {
        String[] permissions = permissionName();
        if (null != permissions && permissions.length != 0) {
            new RxPermissions(this)
                    .requestEachCombined(permissions)
                    .as(RxLife.as(this))
                    .subscribe(permission -> {
                                if (permission.granted) {
                                    Observable.create(this).compose(dealWithThreadMode()).as(RxLife.as(this)).subscribe(this);
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    showToast(permission.name);
                                } else {
                                    showToast(permission.name);
                                }
                            }
                    );
        } else {
            Observable.create(this).compose(dealWithThreadMode()).as(RxLife.as(this)).subscribe(this);
        }
    }

    /**
     * 初始化视图
     */
    private void initBindView() {
        bind = setBindView();
        setContentView(bind.getRoot());
    }

    /**
     * 内部权限提示
     *
     * @param name the 权限名
     */
    private void showToast(String name) {
        String tip = "应用无法使用，请开启%s权限";
        Toast.makeText(this, String.format(tip, name), Toast.LENGTH_LONG).show();
    }
}