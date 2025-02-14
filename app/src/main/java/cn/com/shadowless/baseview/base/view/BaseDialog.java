package cn.com.shadowless.baseview.base.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;

import cn.com.shadowless.baseview.event.ViewPublicEvent;
import cn.com.shadowless.baseview.lifecycle.BaseQuickLifecycle;


/**
 * 基类Dialog
 *
 * @param <VB> the type parameter
 * @author sHadowLess
 */
public abstract class BaseDialog<VB extends ViewBinding> extends Dialog implements
        ViewPublicEvent.InitViewBinding<VB>, ViewPublicEvent.InitBindingEvent,
        ViewPublicEvent.InitViewClick, BaseQuickLifecycle, LifecycleOwner {

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
     * The Is resumed.
     */
    private boolean isResumed = false;

    /**
     * The Lifecycle registry.
     */
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);

    /**
     * The Setting.
     */
    private final DialogSetting setting;

    /**
     * The type Dialog setting.
     */
    public static final class DialogSetting {
        /**
         * The Dialog params.
         */
        private int[] dialogParams;
        /**
         * The Dialog position.
         */
        private int dialogPosition;
        /**
         * The Clear padding.
         */
        private boolean clearPadding;
        /**
         * The Cancel outside.
         */
        private boolean cancelOutside;
        /**
         * The Is drag.
         */
        private boolean isDrag;
        /**
         * The Has shadow.
         */
        private boolean hasShadow;
        /**
         * The Set flag.
         */
        private int setFlag;

        /**
         * Get dialog params int [ ].
         *
         * @return the int [ ]
         */
        public int[] getDialogParams() {
            return dialogParams;
        }

        /**
         * Sets dialog params.
         *
         * @param dialogParams the dialog params
         */
        public void setDialogParams(int[] dialogParams) {
            this.dialogParams = dialogParams;
        }

        /**
         * Gets dialog position.
         *
         * @return the dialog position
         */
        public int getDialogPosition() {
            return dialogPosition;
        }

        /**
         * Sets dialog position.
         *
         * @param dialogPosition the dialog position
         */
        public void setDialogPosition(int dialogPosition) {
            this.dialogPosition = dialogPosition;
        }

        /**
         * Is clear padding boolean.
         *
         * @return the boolean
         */
        public boolean isClearPadding() {
            return clearPadding;
        }

        /**
         * Sets clear padding.
         *
         * @param clearPadding the clear padding
         */
        public void setClearPadding(boolean clearPadding) {
            this.clearPadding = clearPadding;
        }

        /**
         * Is cancel outside boolean.
         *
         * @return the boolean
         */
        public boolean isCancelOutside() {
            return cancelOutside;
        }

        /**
         * Sets cancel outside.
         *
         * @param cancelOutside the cancel outside
         */
        public void setCancelOutside(boolean cancelOutside) {
            this.cancelOutside = cancelOutside;
        }

        /**
         * Is drag boolean.
         *
         * @return the boolean
         */
        public boolean isDrag() {
            return isDrag;
        }

        /**
         * Sets drag.
         *
         * @param drag the drag
         */
        public void setDrag(boolean drag) {
            isDrag = drag;
        }

        /**
         * Is has shadow boolean.
         *
         * @return the boolean
         */
        public boolean isHasShadow() {
            return hasShadow;
        }

        /**
         * Sets has shadow.
         *
         * @param hasShadow the has shadow
         */
        public void setHasShadow(boolean hasShadow) {
            this.hasShadow = hasShadow;
        }

        /**
         * Gets set flag.
         *
         * @return the set flag
         */
        public int getSetFlag() {
            return setFlag;
        }

        /**
         * Sets set flag.
         *
         * @param setFlag the set flag
         */
        public void setSetFlag(int setFlag) {
            this.setFlag = setFlag;
        }
    }

    /**
     * 普通dialog构造
     *
     * @param context the context
     */
    public BaseDialog(@NonNull Context context) {
        this(context, 0);
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
        this.setting = setDialogParam();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
                checkVisibility()
        );
        initDialogAttr();
        initObject();
        initView();
        initViewListener();
    }

    @Override
    public void show() {
        super.show();
        handleLifecycleEvent(Lifecycle.Event.ON_START);
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
        isResumed = true;
        initDialog();
        initData();
        initDataListener();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        isResumed = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isResumed) {
            handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
            isResumed = true;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
        handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        isResumed = false;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            source.getLifecycle().removeObserver(this);
            this.dismiss();
        }
    }

    /**
     * 获取绑定视图控件
     *
     * @return the bind view
     */
    @Override
    public VB getBindView() {
        return bind;
    }

    /**
     * Gets attach activity.
     *
     * @return the attach activity
     */
    public Activity getAttachActivity() {
        return (Activity) this.context;
    }

    /**
     * Check visibility.
     */
    private void checkVisibility() {
        boolean isVisible = getWindow().getDecorView().getVisibility() == View.VISIBLE;
        if (isVisible && !isResumed) {
            handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
            isResumed = true;
        } else if (!isVisible && isResumed) {
            handleLifecycleEvent(Lifecycle.Event.ON_PAUSE);
            handleLifecycleEvent(Lifecycle.Event.ON_STOP);
            isResumed = false;
        }
    }

    /**
     * Handle lifecycle event.
     *
     * @param event the event
     */
    private void handleLifecycleEvent(Lifecycle.Event event) {
        if (lifecycleRegistry.getCurrentState() != Lifecycle.State.DESTROYED) {
            lifecycleRegistry.handleLifecycleEvent(event);
        }
    }

    /**
     * Sets observer lifecycle.
     *
     * @param owner the owner
     */
    public void setNeedObserveLifecycle(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(this);
    }

    /**
     * Init dialog attr relative layout.
     */
    private void initDialogAttr() {
        window = this.getWindow();
        //无标题
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //子类设置是否外部关闭
        this.setCanceledOnTouchOutside(setting.isCancelOutside());
        //动态创建/初始化顶层容器
        try {
            bind = inflateView(this, getLayoutInflater());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("视图无法反射初始化，若动态布局请检查setBindViewClass是否传入或重写inflateView手动实现ViewBinding创建" + Log.getStackTraceString(e));
        }
        //是否清除边框
        if (setting.isClearPadding()) {
            window.getDecorView().setPadding(0, 0, 0, 0);
        }
        //填充顶级容器
        this.setContentView(bind.getRoot());
    }

    /**
     * Init dialog position.
     */
    @SuppressLint("WrongConstant")
    private void initDialog() {
        if (!setting.isHasShadow()) {
            window.setDimAmount(0f);
        }
        window.setGravity(setting.getDialogPosition());
        layoutParams = window.getAttributes();
        int[] params = setting.getDialogParams();
        if (params != null && params.length >= 4) {
            layoutParams.x = params[0];
            layoutParams.y = params[1];
            layoutParams.width = params[2];
            layoutParams.height = params[3];
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        }
        int flag = setting.getSetFlag();
        if (flag != 0) {
            layoutParams.flags = flag;
        }
        if (setting.isDrag()) {
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

    /**
     * Sets dialog param.
     *
     * @return the dialog param
     */
    protected abstract DialogSetting setDialogParam();
}
