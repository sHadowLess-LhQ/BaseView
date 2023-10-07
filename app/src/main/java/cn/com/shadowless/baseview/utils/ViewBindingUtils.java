package cn.com.shadowless.baseview.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The type View binding utils.
 *
 * @author sHadowLess
 */
public class ViewBindingUtils {
    /**
     * Instantiates a new View binding utils.
     */
    private ViewBindingUtils() {

    }

    /**
     * Inflate t.
     *
     * @param <T>            the type parameter
     * @param className      the class name
     * @param layoutInflater the layout inflater
     * @return the t
     * @throws ClassNotFoundException    the class not found exception
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws NoSuchMethodException     the no such method exception
     */
    public static <T> T inflate(String className, LayoutInflater layoutInflater) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class<?> bindingClass = Class.forName(className);
        Method inflateMethod = bindingClass.getMethod("inflate", LayoutInflater.class);
        return (T) inflateMethod.invoke(null, layoutInflater);
    }

    /**
     * Inflate t.
     *
     * @param <T>            the type parameter
     * @param className      the class name
     * @param layoutInflater the layout inflater
     * @param parent         the parent
     * @param attachToParent the attach to parent
     * @return the t
     * @throws ClassNotFoundException    the class not found exception
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws NoSuchMethodException     the no such method exception
     */
    public static <T> T inflate(String className, LayoutInflater layoutInflater, ViewGroup parent, boolean attachToParent) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class<?> bindingClass = Class.forName(className);
        Method inflateMethod = bindingClass.getMethod("inflate", LayoutInflater.class);
        return (T) inflateMethod.invoke(null, layoutInflater, parent, attachToParent);
    }

    /**
     * Inflate t.
     *
     * @param <T>       the type parameter
     * @param className the class name
     * @param view      the view
     * @return the t
     * @throws ClassNotFoundException    the class not found exception
     * @throws InvocationTargetException the invocation target exception
     * @throws IllegalAccessException    the illegal access exception
     * @throws NoSuchMethodException     the no such method exception
     */
    public static <T> T inflate(String className, View view) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class<?> bindingClass = Class.forName(className);
        Method inflateMethod = bindingClass.getMethod("bind", View.class);
        return (T) inflateMethod.invoke(null, view);
    }
}
