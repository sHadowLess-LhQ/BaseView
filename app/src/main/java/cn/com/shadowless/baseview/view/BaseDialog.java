package cn.com.shadowless.baseview.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.viewbinding.ViewBinding;

import cn.com.shadowless.baseview.utils.ClickUtils;


/**
 * 父类Dialog
 *
 * @param <VB> the type parameter
 * @author sHadowLess
 */
public abstract class BaseDialog<VB extends ViewBinding> extends Dialog implements View.OnClickListener, LifecycleObserver, LifecycleOwner {

    /**
     * Dialog窗体参数
     */
    private WindowManager.LayoutParams layoutParams;

    /**
     * Dialog窗体对象
     */
    private Window window;

    /**
     * 上下文
     */
    private final Context context;

    /**
     * 绑定视图
     */
    private VB bind;

    /**
     * The Lifecycle registry.
     */
    protected LifecycleRegistry lifecycleRegistry;

    /**
     * 普通dialog构造
     *
     * @param context the context
     */
    public BaseDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        lifecycleRegistry = new LifecycleRegistry(this);
    }

    /**
     * 指定主题dialog构造
     *
     * @param context    the context
     * @param themeResId the theme res id
     */
    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        lifecycleRegistry = new LifecycleRegistry(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    protected void onStop() {
        super.onStop();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        initDialogAttr();
        initView();
    }

    @Override
    public void show() {
        super.show();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        initListener();
        initDialog();
    }

    @Override
    public void cancel() {
        super.cancel();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
    }

    @Override
    public void dismiss() {
        lifecycleRegistry.removeObserver(this);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        super.dismiss();
    }

    /**
     * On click.
     *
     * @param v the v
     */
    @Override
    public void onClick(View v) {
        if (!ClickUtils.isFastClick()) {
            click(v);
        }
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    /**
     * 获取绑定视图控件
     *
     * @return the bind view
     */
    protected VB getBindView() {
        return bind;
    }

    /**
     * 设置绑定视图
     *
     * @return the bind view
     */
    @NonNull
    protected abstract VB setBindView();

    /**
     * Dialog params int [ ].
     *
     * @return the int [ ]
     */
    protected abstract int[] dialogParams();

    /**
     * Dialog position int.
     *
     * @return the int
     */
    protected abstract int dialogPosition();

    /**
     * Clear padding boolean.
     *
     * @return the boolean
     */
    protected abstract boolean clearPadding();

    /**
     * Cancel outside boolean.
     *
     * @return the boolean
     */
    protected abstract boolean cancelOutside();

    /**
     * isDrag
     *
     * @return the boolean
     */
    protected abstract boolean isDrag();

    /**
     * Has shadow boolean.
     *
     * @return the boolean
     */
    protected abstract boolean hasShadow();

    /**
     * Sets flag.
     *
     * @return the flag
     */
    protected abstract int setFlag();

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 初始化监听
     */
    protected abstract void initListener();

    /**
     * 点击
     *
     * @param v the v
     */
    protected abstract void click(@NonNull View v);

    /**
     * Gets activity context.
     *
     * @return the activity context
     */
    protected Context getActivityContext() {
        return this.context;
    }

    /**
     * Init dialog attr relative layout.
     */
    private void initDialogAttr() {
        window = this.getWindow();
        //无标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //子类设置是否外部关闭
        this.setCanceledOnTouchOutside(cancelOutside());
        //动态创建/初始化顶层容器
        bind = setBindView();
        //是否清除边框
        if (clearPadding()) {
            window.getDecorView().setPadding(0, 0, 0, 0);
        }
        //填充顶级容器
        this.setContentView(bind.getRoot());
    }

    /**
     * Init dialog position.
     */
    private void initDialog() {
        if (!hasShadow()) {
            window.setDimAmount(0f);
        }
        window.setGravity(dialogPosition());
        layoutParams = window.getAttributes();
        int[] params = dialogParams();
        if (params != null && params.length >= 4) {
            layoutParams.x = params[0];
            layoutParams.y = params[1];
            layoutParams.width = params[2];
            layoutParams.height = params[3];
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        int flag = setFlag();
        if (flag != 0) {
            layoutParams.flags = flag;
        }
        if (isDrag()) {
            window.getDecorView().setOnTouchListener(new FloatingOnTouchListener());
        }
        window.setAttributes(layoutParams);
    }

    /**
     * The type Floating on touch listener.
     */
    private class FloatingOnTouchListener implements View.OnTouchListener {
        /**
         * The X.
         */
        private int x;
        /**
         * The Y.
         */
        private int y;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    window.setAttributes(layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
