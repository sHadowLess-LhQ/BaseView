package cn.com.shadowless.baseview.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.util.Pools;
import androidx.core.view.LayoutInflaterCompat;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步布局填充器
 * <p>
 * 提供异步加载ViewBinding布局的功能，避免在主线程进行耗时的布局解析操作。
 * 使用线程池和Handler机制实现异步加载和主线程回调。
 * </p>
 *
 * @param <VB> ViewBinding类型参数
 * @author sHadowLess
 */
public class AsyncViewBindingInflate<VB extends ViewBinding> {

    /**
     * 请求对象池
     * <p>
     * 使用同步对象池管理InflateRequest对象，避免频繁创建和销毁对象。
     * </p>
     */
    private final Pools.SynchronizedPool<InflateRequest<VB>> mRequestPool = new Pools.SynchronizedPool<>(10);

    /**
     * 布局填充器
     */
    LayoutInflater mInflater;
    /**
     * 处理消息的Handler
     */
    Handler mHandler;
    /**
     * 调度器
     */
    Dispatcher<VB> mDispatcher;

    /**
     * 构造函数
     * <p>
     * 初始化异步布局填充器。
     * </p>
     *
     * @param context 上下文
     */
    public AsyncViewBindingInflate(@NonNull Context context) {
        mInflater = new BasicInflater(context);
        mHandler = new Handler(Looper.getMainLooper(), mHandlerCallback);
        mDispatcher = new Dispatcher<>();
    }

    /**
     * 异步加载布局
     * <p>
     * 将布局加载请求放入调度器中，由调度器处理。
     * </p>
     *
     * @param vbClass  ViewBinding类
     * @param parent   父容器
     * @param callback 加载完成后的回调
     */
    @UiThread
    public void inflate(Class<VB> vbClass, @Nullable ViewGroup parent, @NonNull OnInflateFinishedListener<VB> callback) {
        InflateRequest<VB> request = obtainRequest();
        request.inflater = this;
        request.vbClass = vbClass;
        request.parent = parent;
        request.callback = callback;
        mDispatcher.enqueue(request);
    }

    /**
     * 处理消息的回调
     */
    private Handler.Callback mHandlerCallback = msg -> {
        InflateRequest<VB> request = (InflateRequest<VB>) msg.obj;
        if (request.binding == null) {
            try {
                request.binding = inflate(request.vbClass, mInflater, request.parent, false);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                request.callback.onInflateError(e);
            }
        }
        request.callback.onInflateFinished(request.binding, request.parent);
        releaseRequest(request);
        return true;
    };

    /**
     * 加载完成后的回调接口
     *
     * @param <VB> ViewBinding类型参数
     */
    public interface OnInflateFinishedListener<VB> {
        /**
         * 加载完成后的回调方法
         *
         * @param binding 加载完成的ViewBinding对象
         * @param parent  父容器
         */
        void onInflateFinished(@NonNull VB binding, @Nullable ViewGroup parent);

        /**
         * 加载出错后的回调方法
         *
         * @param e 异常对象
         */
        void onInflateError(Exception e);
    }

    /**
     * 加载请求对象
     *
     * @param <VB> ViewBinding类型参数
     */
    private static class InflateRequest<VB extends ViewBinding> {
        /**
         * 异步布局填充器
         */
        AsyncViewBindingInflate<VB> inflater;
        /**
         * 父容器
         */
        ViewGroup parent;

        /**
         * ViewBinding类
         */
        Class<VB> vbClass;
        /**
         * 加载完成的ViewBinding对象
         */
        VB binding;
        /**
         * 加载完成后的回调
         */
        OnInflateFinishedListener<VB> callback;

        /**
         * 构造函数
         */
        InflateRequest() {
        }
    }


    /**
     * 调度器
     */
    private static class Dispatcher<VB extends ViewBinding> {

        /**
         * 获得当前CPU的核心数
         */
        private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        /**
         * 设置线程池的核心线程数2-4之间,但是取决于CPU核数
         */
        private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
        /**
         * 设置线程池的最大线程数为 CPU核数 * 2 + 1
         */
        private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
        /**
         * 设置线程池空闲线程存活时间30s
         */
        private static final int KEEP_ALIVE_SECONDS = 30;

        /**
         * The constant sThreadFactory.
         */
        private static final ThreadFactory S_THREAD_FACTORY = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, "AsyncLayoutInflatePlus #" + mCount.getAndIncrement());
            }
        };

        /**
         * LinkedBlockingQueue 默认构造器，队列容量是Integer.MAX_VALUE
         */
        private static final BlockingQueue<Runnable> S_POOL_WORK_QUEUE =
                new LinkedBlockingQueue<Runnable>();

        /**
         * The constant THREAD_POOL_EXECUTOR.
         */
        public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;

        static {
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                    CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                    S_POOL_WORK_QUEUE, S_THREAD_FACTORY);
            threadPoolExecutor.allowCoreThreadTimeOut(true);
            THREAD_POOL_EXECUTOR = threadPoolExecutor;
        }

        /**
         * 将加载请求放入线程池中
         *
         * @param request 加载请求对象
         */
        public void enqueue(InflateRequest<VB> request) {
            THREAD_POOL_EXECUTOR.execute((new InflateRunnable<>(request)));

        }

    }

    /**
     * 基础布局填充器
     */
    private static class BasicInflater extends LayoutInflater {
        /**
         * The constant sClassPrefixList.
         */
        private static final String[] sClassPrefixList = {
                "android.widget.",
                "android.webkit.",
                "android.app."
        };

        /**
         * Instantiates a new Basic inflater.
         *
         * @param context the context
         */
        BasicInflater(Context context) {
            super(context);
            if (context instanceof AppCompatActivity) {
                // 手动setFactory2，兼容AppCompatTextView等控件
                AppCompatDelegate appCompatDelegate = ((AppCompatActivity) context).getDelegate();
                if (appCompatDelegate instanceof Factory2) {
                    LayoutInflaterCompat.setFactory2(this, (Factory2) appCompatDelegate);
                }
            }
        }

        @Override
        public LayoutInflater cloneInContext(Context newContext) {
            return new BasicInflater(newContext);
        }

        @Override
        protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
            for (String prefix : sClassPrefixList) {
                View view = createView(name, prefix, attrs);
                if (view != null) {
                    return view;
                }
            }

            return super.onCreateView(name, attrs);
        }
    }


    /**
     * 加载任务
     */
    private static class InflateRunnable<VB extends ViewBinding> implements Runnable {
        /**
         * The Request.
         */
        private final InflateRequest<VB> request;
        /**
         * The Is running.
         */
        private boolean isRunning;

        /**
         * Instantiates a new Inflate runnable.
         *
         * @param request the request
         */
        public InflateRunnable(InflateRequest<VB> request) {
            this.request = request;
        }

        @Override
        public void run() {
            isRunning = true;
            try {
                request.binding = inflate(request.vbClass, request.inflater.mInflater, request.parent, false);
            } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            Message.obtain(request.inflater.mHandler, 0, request)
                    .sendToTarget();
        }

        /**
         * Is running boolean.
         *
         * @return the boolean
         */
        public boolean isRunning() {
            return isRunning;
        }
    }


    /**
     * Obtain request inflate request.
     *
     * @return the inflate request
     */
    public InflateRequest<VB> obtainRequest() {
        InflateRequest<VB> obj = mRequestPool.acquire();
        if (obj == null) {
            obj = new InflateRequest<>();
        }
        return obj;
    }

    /**
     * Release request.
     *
     * @param obj the obj
     */
    public void releaseRequest(InflateRequest<VB> obj) {
        obj.callback = null;
        obj.inflater = null;
        obj.parent = null;
        obj.vbClass = null;
        obj.binding = null;
        mRequestPool.release(obj);
    }

    /**
     * Inflate t.
     *
     * @param <T>            the type parameter
     * @param tClass         the t class
     * @param layoutInflater the layout inflater
     * @param parent         the parent
     * @param attachToParent the attach to parent
     * @return the t
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws NoSuchMethodException     the no such method exception
     */
    public static <T extends ViewBinding> T inflate(Class<T> tClass, LayoutInflater layoutInflater, ViewGroup parent, boolean attachToParent) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method inflateMethod = tClass.getMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
        return (T) inflateMethod.invoke(null, layoutInflater, parent, attachToParent);
    }

    /**
     * Cancel.
     */
    public void cancel() {
        mHandler.removeCallbacksAndMessages(null);
        mHandlerCallback = null;
    }
}
