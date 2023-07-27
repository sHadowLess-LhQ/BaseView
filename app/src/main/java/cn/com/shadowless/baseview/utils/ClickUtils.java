package cn.com.shadowless.baseview.utils;

/**
 * The type Click utils.
 *
 * @author sHadowLess
 */
public class ClickUtils {

    /**
     * The constant TIME.
     */
    private static final int TIME = 500;
    /**
     * The constant lastClickTime.
     */
    private static long lastClickTime = 0;

    /**
     * 处理快速双击，多击事件，在TIME时间内只执行一次事件
     *
     * @return boolean
     */
    public static boolean isFastClick() {
        long currentTime = System.currentTimeMillis();
        long timeInterval = currentTime - lastClickTime;
        if (0 < timeInterval && timeInterval < TIME) {
            return true;
        }
        lastClickTime = currentTime;
        return false;
    }
}
