package cn.com.shadowless.baseview;

/**
 * 基础常量类
 * <p>
 * 定义项目中使用的基础常量值，如时间间隔、默认值等。
 * </p>
 *
 * @author sHadowLess
 */
public class BaseCons {

    /**
     * 默认时间间隔（毫秒）
     * <p>
     * 用于防重复点击等需要时间间隔判断的场景，默认值为500毫秒。
     * </p>
     */
    public static final int TIME = 500;
    
    /**
     * 上次点击时间戳
     * <p>
     * 用于记录上次点击的时间，配合TIME常量实现防重复点击功能。
     * </p>
     */
    public static long lastClickTime = 0;
}