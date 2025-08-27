package cn.com.shadowless.baseview.event;

/**
 * ViewModel事件接口
 * <p>
 * 定义了ViewModel相关的生命周期回调方法，用于在ViewModel的不同阶段执行相应的操作。
 * 实现此接口的类可以在特定时机执行初始化、数据加载等操作。
 * </p>
 *
 * @author sHadowLess
 */
public interface ViewModelEvent {

    /**
     * 当ViewModel创建完成时调用
     * <p>
     * 在ViewModel初始化完成后调用，用于执行一次性的初始化操作。
     * 此方法通常在ViewModel创建后立即调用。
     * </p>
     */
    void onModelCreated();

    /**
     * 当ViewModel需要初始化视图时调用
     * <p>
     * 在视图需要与ViewModel进行绑定时调用，用于设置视图相关的初始化操作。
     * 此方法通常在视图创建后调用。
     * </p>
     */
    void onModelInitView();

    /**
     * 当ViewModel需要初始化监听器时调用
     * <p>
     * 在需要设置各种事件监听器时调用，用于注册LiveData观察者等操作。
     * 此方法通常在视图和数据绑定完成后调用。
     * </p>
     */
    void onModelInitListener();

    /**
     * 当ViewModel需要初始化数据时调用
     * <p>
     * 在需要加载初始数据时调用，用于从网络或本地加载数据。
     * 此方法通常在监听器设置完成后调用。
     * </p>
     */
    void onModelInitData();

    /**
     * 当ViewModel需要基于权限初始化数据时调用
     * <p>
     * 用于根据用户权限加载特定数据。
     * 此方法默认为空实现，可根据需求在实现类中重写。
     * </p>
     */
    default void onModelInitDataByPermission() {

    }
}
