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
 * The type Async layout inflate plus.
 *
 * @param <VB> the type parameter
 * @author sHadowLess
 */
public class AsyncViewBindingInflate<VB extends ViewBinding> {

    /**
     * The M request pool.
     */
    private final Pools.SynchronizedPool<InflateRequest> mRequestPool = new Pools.SynchronizedPool<>(10);

    /**
     * The M inflater.
     */
    LayoutInflater mInflater;
    /**
     * The M handler.
     */
    Handler mHandler;
    /**
     * The M dispatcher.
     */
    Dispather mDispatcher;

    /**
     * Instantiates a new Async layout inflate plus.
     *
     * @param context     the context
     */
    public AsyncViewBindingInflate(@NonNull Context context) {
        mInflater = new BasicInflater(context);
        mHandler = new Handler(Looper.getMainLooper(), mHandlerCallback);
        mDispatcher = new Dispather();
    }

    /**
     * Inflate.
     *
     * @param vbClass  the vb class
     * @param parent   the parent
     * @param callback the callback
     */
    @UiThread
    public void inflate(Class<VB> vbClass, @Nullable ViewGroup parent, @NonNull OnInflateFinishedListener callback) {
        InflateRequest<VB> request = obtainRequest();
        request.inflater = this;
        request.vbClass = vbClass;
        request.parent = parent;
        request.callback = callback;
        mDispatcher.enqueue(request);
    }

    /**
     * The M handler callback.
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
     * The interface On inflate finished listener.
     *
     * @param <VB> the type parameter
     */
    public interface OnInflateFinishedListener<VB> {
        /**
         * On inflate finished.
         *
         * @param binding the binding
         * @param parent  the parent
         */
        void onInflateFinished(@NonNull VB binding, @Nullable ViewGroup parent);

        /**
         * On inflate error.
         *
         * @param e the e
         */
        void onInflateError(Exception e);
    }

    /**
     * The type Inflate request.
     *
     * @param <VB> the type parameter
     */
    private static class InflateRequest<VB> {
        /**
         * The Inflater.
         */
        AsyncViewBindingInflate inflater;
        /**
         * The Parent.
         */
        ViewGroup parent;

        /**
         * The Class name.
         */
        Class<VB> vbClass;
        /**
         * The View.
         */
        VB binding;
        /**
         * The Callback.
         */
        OnInflateFinishedListener<VB> callback;

        /**
         * Instantiates a new Inflate request.
         */
        InflateRequest() {
        }
    }


    /**
     * The type Dispather.
     */
    private static class Dispather {

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
        private static final ThreadFactory sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, "AsyncLayoutInflatePlus #" + mCount.getAndIncrement());
            }
        };

        /**
         * LinkedBlockingQueue 默认构造器，队列容量是Integer.MAX_VALUE
         */
        private static final BlockingQueue<Runnable> sPoolWorkQueue =
                new LinkedBlockingQueue<Runnable>();

        /**
         * The constant THREAD_POOL_EXECUTOR.
         */
        public static final ThreadPoolExecutor THREAD_POOL_EXECUTOR;

        static {
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                    CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                    sPoolWorkQueue, sThreadFactory);
            threadPoolExecutor.allowCoreThreadTimeOut(true);
            THREAD_POOL_EXECUTOR = threadPoolExecutor;
        }

        /**
         * Enqueue.
         *
         * @param request the request
         */
        public void enqueue(InflateRequest request) {
            THREAD_POOL_EXECUTOR.execute((new InflateRunnable(request)));

        }

    }

    /**
     * The type Basic inflater.
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
     * The type Inflate runnable.
     */
    private static class InflateRunnable implements Runnable {
        /**
         * The Request.
         */
        private final InflateRequest request;
        /**
         * The Is running.
         */
        private boolean isRunning;

        /**
         * Instantiates a new Inflate runnable.
         *
         * @param request the request
         */
        public InflateRunnable(InflateRequest request) {
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
            obj = new InflateRequest();
        }
        return obj;
    }

    /**
     * Release request.
     *
     * @param obj the obj
     */
    public void releaseRequest(InflateRequest obj) {
        obj.callback = null;
        obj.inflater = null;
        obj.parent = null;
        obj.vbClass = null;
        obj.binding = null;
        mRequestPool.release(obj);
    }

    public static <T> T inflate(Class<T> tClass, LayoutInflater layoutInflater, ViewGroup parent, boolean attachToParent) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
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
