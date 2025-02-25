package cn.com.shadowless.baseview.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The type Single mutable live data.
 *
 * @param <T> the type parameter
 * @author sHadowLess
 */
public class SingleMutableLiveData<T> extends MutableLiveData<T> {

    /**
     * The Pending.
     */
    private final AtomicBoolean pending = new AtomicBoolean(false);

    /**
     * The Is single event.
     */
    private final AtomicBoolean isSingleEvent = new AtomicBoolean(false);

    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, value -> {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(value);
            }
        });
    }

    @Override
    public void setValue(T value) {
        if (isSingleEvent.get()) {
            pending.set(true);
        }
        super.setValue(value);
    }

    @Override
    public void postValue(T value) {
        if (isSingleEvent.get()) {
            pending.set(true);
        }
        super.postValue(value);
    }

    /**
     * Sets single event.
     *
     * @param singleEvent the single event
     */
    public void setSingleEvent(boolean singleEvent) {
        this.isSingleEvent.set(singleEvent);
    }
}
