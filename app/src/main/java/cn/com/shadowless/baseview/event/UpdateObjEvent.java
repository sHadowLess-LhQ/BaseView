package cn.com.shadowless.baseview.event;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.com.shadowless.baseview.annotation.UpdateReflect;
import cn.com.shadowless.baseview.manager.VmObjManager;
import cn.com.shadowless.baseview.reflectImpl.DelegateReflect;

/**
 * 对象更新事件接口
 * <p>
 * 定义了通过反射机制更新对象的方法，支持自动更新被@UpdateReflect注解标记的字段。
 * 实现此接口的类可以通过update方法触发字段的自动更新机制。
 * </p>
 *
 * @author sHadowLess
 */
public interface UpdateObjEvent {

    /**
     * 设置反射规则
     * <p>
     * 设置用于处理反射更新的规则列表，默认包含DelegateReflect规则。
     * 子类可以重写此方法以添加自定义的反射处理规则。
     * </p>
     *
     * @param list 反射规则列表
     * @return 更新后的反射规则列表
     */
    default List<UpdateReflectEvent> setReflectRules(List<UpdateReflectEvent> list) {
        list.add(DelegateReflect.getInstance());
        return list;
    }

    /**
     * 更新对象
     * <p>
     * 使用默认配置更新对象，启用自动更新功能。
     * </p>
     *
     * @param manager ViewModel对象管理器
     */
    default void update(@NonNull VmObjManager<? extends ViewBinding> manager) {
        update(manager, true);
    }

    /**
     * 更新对象
     * <p>
     * 根据指定的自动更新标志更新对象。
     * </p>
     *
     * @param manager      ViewModel对象管理器
     * @param isAutoUpdate 是否启用自动更新
     */
    default void update(@NonNull VmObjManager<? extends ViewBinding> manager, boolean isAutoUpdate) {
        update(manager, isAutoUpdate, UpdateObjEvent.this.getClass(), UpdateObjEvent.this, setReflectRules(new ArrayList<>()));
    }

    /**
     * 更新对象
     * <p>
     * 根据指定的类、对象和反射规则更新对象。
     * 通过反射遍历类的所有字段，对被@UpdateReflect注解标记的字段或实现UpdateObjEvent接口的字段执行更新操作。
     * </p>
     *
     * @param manager      ViewModel对象管理器
     * @param isAutoUpdate 是否启用自动更新
     * @param cls          要更新的类
     * @param obj          要更新的对象实例
     * @param events       反射处理规则列表
     */
    default void update(@NonNull VmObjManager<? extends ViewBinding> manager, boolean isAutoUpdate, Class<?> cls, @NonNull Object obj, @NonNull List<UpdateReflectEvent> events) {
        if (cls == null || !isAutoUpdate) {
            return;
        }
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value == null) {
                    continue;
                }
                if (field.isAnnotationPresent(UpdateReflect.class)) {
                    for (UpdateReflectEvent event : events) {
                        Object tempObj = event.getActualObject(value);
                        if (tempObj == null) {
                            continue;
                        }
                        if (!UpdateObjEvent.class.isAssignableFrom(tempObj.getClass())) {
                            continue;
                        }
                        ((UpdateObjEvent) tempObj).update(manager);
                    }
                } else {
                    if (!UpdateObjEvent.class.isAssignableFrom(field.getType())) {
                        continue;
                    }
                    ((UpdateObjEvent) value).update(manager);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        update(manager, true, cls.getSuperclass(), obj, events);
    }
}