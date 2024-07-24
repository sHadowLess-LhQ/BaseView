package cn.com.shadowless.baseview.event;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import cn.com.shadowless.baseview.base.BaseCons;

/**
 * The interface Public event.
 *
 * @param <VB> the type parameter
 * @author sHadowLess
 */
public interface PublicEvent<VB extends ViewBinding> extends View.OnClickListener {

    /**
     * Init generics class class.
     *
     * @param o the o
     * @return the class
     */
    default Class<VB> initGenericsClass(Object o) {
        Type superClass = o.getClass().getGenericSuperclass();
        ParameterizedType parameterized = (ParameterizedType) superClass;
        return (Class<VB>) parameterized.getActualTypeArguments()[0];
    }

    /**
     * Is fast click boolean.
     *
     * @param time the time
     * @return the boolean
     */
    default boolean isFastClick(int time) {
        long currentTime = System.currentTimeMillis();
        long timeInterval = currentTime - BaseCons.lastClickTime;
        if (0 < timeInterval && timeInterval < time) {
            return true;
        }
        BaseCons.lastClickTime = currentTime;
        return false;
    }

    @Override
    default void onClick(View v) {
        if (!isFastClick(BaseCons.TIME)) {
            antiShakingClick(v);
        }
    }

    /**
     * Click.
     *
     * @param v the v
     */
    void antiShakingClick(View v);

    /**
     * The interface Async load view call back.
     */
    interface AsyncLoadViewCallBack {
        /**
         * Show.
         */
        void showLoadView();

        /**
         * Dismiss.
         */
        void dismissLoadView();
    }

    interface PermissionCallBack {
        /**
         * Agree.
         */
        void agree();

        /**
         * Disagree.
         *
         * @param name the name
         */
        void disagree(List<String> name);

        /**
         * Ban.
         *
         * @param name the name
         */
        void ban(List<String> name);

        /**
         * Fail.
         *
         * @param msg the msg
         * @param e   the e
         */
        void fail(String msg, @Nullable Throwable e);
    }
}
