package cn.com.shadowless.baseview.reflectImpl;

import java.lang.reflect.Method;

import cn.com.shadowless.baseview.event.UpdateReflectEvent;

public class DelegateReflect implements UpdateReflectEvent {

    private DelegateReflect() {
    }

    private static class DelegateReflectImpl {
        private static final DelegateReflect instance = new DelegateReflect();
    }

    public static DelegateReflect getInstance() {
        return DelegateReflectImpl.instance;
    }

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
