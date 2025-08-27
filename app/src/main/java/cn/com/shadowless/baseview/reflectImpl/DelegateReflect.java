package cn.com.shadowless.baseview.reflectImpl;

import java.lang.reflect.Method;

import cn.com.shadowless.baseview.event.UpdateReflectEvent;

/**
 * 委托反射实现类
 * <p>
 * 用于处理通过委托方式包装的对象，通过反射调用getValue方法获取实际对象。
 * 实现了UpdateReflectEvent接口，提供从委托对象中提取实际对象的功能。
 * 使用单例模式确保全局只有一个实例。
 * </p>
 *
 * @author sHadowLess
 */
public class DelegateReflect implements UpdateReflectEvent {

    /**
     * 私有构造函数
     * <p>
     * 防止外部直接实例化，确保通过getInstance方法获取单例实例。
     * </p>
     */
    private DelegateReflect() {
    }

    /**
     * 静态内部类持有单例实例
     * <p>
     * 利用JVM类加载机制确保线程安全和懒加载。
     * </p>
     */
    private static class DelegateReflectImpl {
        private static final DelegateReflect instance = new DelegateReflect();
    }

    /**
     * 获取单例实例
     * <p>
     * 通过静态内部类的方式获取单例实例，确保线程安全和懒加载。
     * </p>
     *
     * @return DelegateReflect单例实例
     */
    public static DelegateReflect getInstance() {
        return DelegateReflectImpl.instance;
    }

    /**
     * 获取实际对象
     * <p>
     * 从给定的值中提取或获取实际的对象引用。
     * 如果值对象包含getValue方法，则通过反射调用该方法获取实际对象；
     * 否则直接返回原始值。
     * </p>
     *
     * @param value 可能是包装对象的原始值
     * @return 实际的对象引用，如果无法获取则返回原始值
     */
    @Override
    public Object getActualObject(Object value) {
        if (value == null) {
            return null;
        }
        try {
            Method getValueMethod = findMethod(value.getClass(), "getValue");
            if (getValueMethod != null) {
                getValueMethod.setAccessible(true);
                return getValueMethod.invoke(value);
            }
            return value;
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * 递归查找方法
     * <p>
     * 在指定类及其父类中查找指定名称和参数类型的方法。
     * </p>
     *
     * @param clazz      要查找的类
     * @param methodName 方法名称
     * @param paramTypes 方法参数类型
     * @return 找到的方法对象，如果未找到则返回null
     */
    private Method findMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        try {
            return clazz.getDeclaredMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null && !Object.class.equals(superClass)) {
                return findMethod(superClass, methodName, paramTypes);
            }
            return null;
        }
    }
}