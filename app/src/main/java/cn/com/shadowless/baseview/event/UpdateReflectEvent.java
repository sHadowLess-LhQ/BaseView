package cn.com.shadowless.baseview.event;

/**
 * 更新反射事件接口
 * <p>
 * 定义了通过反射获取实际对象的方法，用于处理被 {@link cn.com.shadowless.baseview.annotation.UpdateReflect} 注解标记的字段。
 * 实现类需要提供从包装对象中获取实际对象的逻辑。
 * </p>
 *
 * @author sHadowLess
 */
public interface UpdateReflectEvent {
    /**
     * 获取实际对象
     * <p>
     * 从给定的值中提取或获取实际的对象引用。
     * 例如，当值是一个委托对象时，此方法应该返回委托对象所包装的实际对象。
     * </p>
     *
     * @param value 可能是包装对象的原始值
     * @return 实际的对象引用，如果无法获取则返回原始值或null
     */
    Object getActualObject(Object value);
}