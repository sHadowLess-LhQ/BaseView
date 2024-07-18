package cn.com.shadowless.baseview.click;

import android.view.View;

import cn.com.shadowless.baseview.base.BaseCons;


/**
 * The interface Anti shaking on click listener.
 *
 * @author sHadowLess
 */
public interface AntiShakingOnClickListener extends View.OnClickListener {


    @Override
    default void onClick(View v) {
        if (!isFastClick()) {
            antiShakingClick(v);
        }
    }

    /**
     * Is fast click boolean.
     *
     * @return the boolean
     */
    default boolean isFastClick() {
        long currentTime = System.currentTimeMillis();
        long timeInterval = currentTime - BaseCons.lastClickTime;
        if (0 < timeInterval && timeInterval < BaseCons.TIME) {
            return true;
        }
        BaseCons.lastClickTime = currentTime;
        return false;
    }

    /**
     * Click.
     *
     * @param v the v
     */
    void antiShakingClick(View v);
}
