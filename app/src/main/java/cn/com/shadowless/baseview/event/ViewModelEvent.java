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
     * Init view.
     */
    void onModelInitView();

    /**
     * On model init data.
     */
    void onModelInitListener();

    /**
     * On model init data.
     */
    void onModelInitData();

    /**
     * On model init data.
     */
    default void onModelInitDataByPermission() {

    }
}
