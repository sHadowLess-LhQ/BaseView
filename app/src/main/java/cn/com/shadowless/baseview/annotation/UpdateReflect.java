package cn.com.shadowless.baseview.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 更新反射注解
 * <p>
 * 用于标记BaseViewModel中需要通过反射机制进行更新操作的全局变量。
 * </p>
 *
 * @author sHadowLess
 */
@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UpdateReflect {
}