package cn.com.shadowless.baseview.event;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.com.shadowless.baseview.annotation.UpdateReflect;
import cn.com.shadowless.baseview.manager.VmObjManager;
import cn.com.shadowless.baseview.reflectImpl.DelegateReflect;

public interface UpdateObjEvent {

    default List<UpdateReflectEvent> setReflectRules(List<UpdateReflectEvent> list) {
        list.add(DelegateReflect.getInstance());
        return list;
    }

    default void update(@NonNull VmObjManager<? extends ViewBinding> manager) {
        update(manager, true);
    }

    default void update(@NonNull VmObjManager<? extends ViewBinding> manager, boolean isAutoUpdate) {
        update(manager, isAutoUpdate, UpdateObjEvent.this.getClass(), UpdateObjEvent.this, setReflectRules(new ArrayList<>()));
    }

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
