package cn.com.shadowless.baseview.event;

/**
 * The interface View model event.
 *
 * @author sHadowLess
 */
public interface ViewModelEvent {

    /**
     * Init data observe.
     */
    void onModelCreated();

    /**
     * On model init data.
     */
    void onModelInitDataListener();

    /**
     * On model init data.
     */
    void onModelInitData();
}
