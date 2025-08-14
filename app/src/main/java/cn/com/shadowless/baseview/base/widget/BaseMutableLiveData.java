package cn.com.shadowless.baseview.base.widget;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.shadowless.baseview.event.UpdateObjEvent;
import cn.com.shadowless.baseview.lifecycle.BaseQuickLifecycle;
import cn.com.shadowless.baseview.manager.VmObjManager;


/**
 * The type Base mutable.
 *
 * @author sHadowLess
 */
public abstract class BaseMutableLiveData implements BaseQuickLifecycle, UpdateObjEvent {

    /**
     * The Mutable live data list.
     */
    private final List<LiveData<?>> mutableLiveDataList;

    /**
     * The Lifecycle owner.
     */
    private LifecycleOwner observeLifecycle;

    /**
     * Instantiates a new Base mutable live data.
     *
     * @param lifecycleOwner the lifecycle owner
     */
    public BaseMutableLiveData(@NonNull LifecycleOwner lifecycleOwner) {
        this(lifecycleOwner, true);
    }

    /**
     * Instantiates a new Base mutable live data.
     *
     * @param lifecycleOwner the lifecycle owner
     * @param isReflectSet   the is reflect set
     */
    public BaseMutableLiveData(@NonNull LifecycleOwner lifecycleOwner, boolean isReflectSet) {
        this.observeLifecycle = lifecycleOwner;
        this.getLifecycle().addObserver(this);
        mutableLiveDataList = new ArrayList<>();
        if (isReflectSet) {
            getAllFields(this.getClass(), BaseMutableLiveData.this);
        }
    }

    /**
     * Gets all fields.
     *
     * @param cls the cls
     * @param obj the obj
     */
    private void getAllFields(Class<?> cls, Object obj) {
        if (cls == null) {
            return;
        }
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (!MutableLiveData.class.isAssignableFrom(field.getType())) {
                continue;
            }
            try {
                Object o = field.get(obj);
                if (o != null) {
                    continue;
                }
                field.set(obj, field.getType().getDeclaredConstructor().newInstance());
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        getAllFields(cls.getSuperclass(), obj);
    }

    /**
     * Sets mutable live data list.
     *
     * @param mutableLiveData the mutable live data
     */
    public void setForeverObserve(@NonNull LiveData<?>... mutableLiveData) {
        mutableLiveDataList.addAll(Arrays.asList(mutableLiveData));
    }

    /**
     * Sets mutable live data.
     *
     * @param mutableLiveData the mutable live data
     */
    public void setForeverObserve(@NonNull LiveData<?> mutableLiveData) {
        mutableLiveDataList.add(mutableLiveData);
    }

    /**
     * Clear all observer.
     *
     * @param owner the owner
     */
    public void clearAllForEverObserver(@NonNull LifecycleOwner owner) {
        for (LiveData<?> mutableLiveData : mutableLiveDataList) {
            mutableLiveData.removeObservers(owner);
        }
        mutableLiveDataList.clear();
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            onTerminate();
            clearAllForEverObserver(source);
            this.getLifecycle().removeObserver(this);
        }
    }

    @NonNull
    @Override
    public LifecycleOwner getObserveLifecycleOwner() {
        return observeLifecycle;
    }

    @Override
    public void update(@NonNull VmObjManager<? extends ViewBinding> manager) {
        this.getLifecycle().removeObserver(this);
        this.observeLifecycle = null;
        this.observeLifecycle = manager.getCurrentLifecycleOwner();
        this.getLifecycle().addObserver(this);
        UpdateObjEvent.super.update(manager);
    }
}
