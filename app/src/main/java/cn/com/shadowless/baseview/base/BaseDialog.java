package cn.com.shadowless.baseview.base;

import android.annotation.SuppressLint;
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

import cn.com.shadowless.baseview.event.PublicEvent;
import cn.com.shadowless.baseview.utils.ViewBindingUtils;


/**
 * 父类Dialog
 *
 * @param <VB> the type parameter
 * @author sHadowLess
 */
public abstract class BaseDialog<VB extends ViewBinding> extends Dialog implements
        PublicEvent<VB>, BaseQuickLifecycle {

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
    private final LifecycleRegistry lifecycleRegistry;

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
        super(context);
        this.context = context;
        this.setting = setDialogParam();
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
        this.setting = setDialogParam();
        lifecycleRegistry = new LifecycleRegistry(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        initDialogAttr();
        initObject();
        initView();
        initViewListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
    }

    @Override
    protected void onStop() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY);
        super.onStop();
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

    @Override
    public void show() {
        super.show();
        initDialog();
        initData();
        initDataListener();
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
     * 获取绑定视图控件
     *
     * @return the bind view
     */
    protected VB getBindView() {
        return bind;
    }

    /**
     * Gets activity context.
     *
     * @return the activity context
     */
    protected Context getActivityContext() {
        return this.context;
    }

    /**
     * Inflate view vb.
     *
     * @return the vb
     */
    protected VB inflateView() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return ViewBindingUtils.inflate(initGenericsClass(), getLayoutInflater());
    }

    /**
     * 设置绑定视图
     *
     * @return the 视图
     */
    protected Class<VB> setBindViewClass() {
        return null;
    }

    /**
     * Init generics class class.
     *
     * @return the class
     */
    private Class<VB> initGenericsClass() {
        Class<VB> genericsCls = this.initGenericsClass(this);
        if (genericsCls == ViewBinding.class) {
            genericsCls = setBindViewClass();
        }
        return genericsCls;
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
            bind = inflateView();
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

    /**
     * 初始化对象
     */
    protected abstract void initObject();

    /**
     * 初始化绑定视图数据监听
     */
    protected abstract void initView();

    /**
     * 初始化监听
     */
    protected abstract void initViewListener();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * Init data listener.
     */
    protected abstract void initDataListener();
}
