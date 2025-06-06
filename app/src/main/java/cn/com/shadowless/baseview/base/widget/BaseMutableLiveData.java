package cn.com.shadowless.baseview.base.widget;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.com.shadowless.baseview.lifecycle.BaseQuickLifecycle;


/**
 * The type Base mutable.
 *
 * @author sHadowLess
 */
public abstract class BaseMutableLiveData implements BaseQuickLifecycle {

    /**
     * The Mutable live data list.
     */
    private final List<LiveData<?>> mutableLiveDataList;

    /**
     * The Lifecycle owner.
     */
    private final LifecycleOwner lifecycleOwner;

    /**
     * Instantiates a new Base mutable live data.
     *
     * @param lifecycleOwner the lifecycle owner
     */
    public BaseMutableLiveData(LifecycleOwner lifecycleOwner) {
        this(lifecycleOwner, true);
    }

    /**
     * Instantiates a new Base mutable live data.
     *
     * @param lifecycleOwner the lifecycle owner
     * @param isReflectSet   the is reflect set
     */
    public BaseMutableLiveData(LifecycleOwner lifecycleOwner, boolean isReflectSet) {
        this.lifecycleOwner = lifecycleOwner;
        this.lifecycleOwner.getLifecycle().addObserver(this);
        mutableLiveDataList = new ArrayList<>();
        if (isReflectSet) {
            getAllFields(this.getClass(), this);
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
            if (field.getType() != MutableLiveData.class) {
                continue;
            }
            try {
                Object o = field.get(obj);
                if (o != null) {
                    continue;
                }
                field.set(obj, new MutableLiveData<>());
            } catch (IllegalAccessException e) {
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
    public void setForeverObserve(LiveData<?>... mutableLiveData) {
        mutableLiveDataList.addAll(Arrays.asList(mutableLiveData));
    }

    /**
     * Sets mutable live data.
     *
     * @param mutableLiveData the mutable live data
     */
    public void setForeverObserve(LiveData<?> mutableLiveData) {
        mutableLiveDataList.add(mutableLiveData);
    }

    /**
     * Clear all observer.
     *
     * @param owner the owner
     */
    public void clearAllForEverObserver(LifecycleOwner owner) {
        for (LiveData<?> mutableLiveData : mutableLiveDataList) {
            mutableLiveData.removeObservers(owner);
        }
        mutableLiveDataList.clear();
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (event == setStopEvent()) {
            onTerminate(event);
            clearAllForEverObserver(source);
            this.getLifecycle().removeObserver(this);
        }
    }

    @NonNull
    @Override
    public Lifecycle.Event setStopEvent() {
        return Lifecycle.Event.ON_DESTROY;
    }

    @NonNull
    @Override
    public LifecycleOwner getObserveLifecycleOwner() {
        return lifecycleOwner;
    }
}
