package cn.com.shadowless.baseview.event;

import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Field;

import cn.com.shadowless.baseview.manager.VmObjManager;

public interface UpdateObjEvent {

    default void update(@NonNull VmObjManager<? extends ViewBinding> manager) {
        update(manager, false);
    }

    default void update(@NonNull VmObjManager<? extends ViewBinding> manager, boolean isAutoUpdate) {
        autoUpdate(this.getClass(), UpdateObjEvent.this, manager, isAutoUpdate);
    }

    default void autoUpdate(Class<?> cls, @NonNull Object obj, @NonNull VmObjManager<? extends ViewBinding> manager, boolean isAutoUpdate) {
        if (cls == null) {
            return;
        }
        if (!isAutoUpdate) {
            return;
        }
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!UpdateObjEvent.class.isAssignableFrom(field.getType())) {
                continue;
            }
            try {
                Object o = field.get(obj);
                if (o == null) {
                    continue;
                }
                ((UpdateObjEvent) o).update(manager, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        autoUpdate(cls.getSuperclass(), obj, manager, true);
    }
}
