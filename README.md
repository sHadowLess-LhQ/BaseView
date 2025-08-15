# BaseView

## 软件架构

个人Android项目快速搭建框架基类

> **注：** 1.x 和 2.x 版本的包结构不同

## 安装教程

### Step 1. 添加 Maven 仓库地址和配置

#### 旧版 Android Studio

```gradle
// build.gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

#### 新版 Android Studio

```gradle
// settings.gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### 配置 ViewBinding

#### 新版 Android Studio

```gradle
// 主项目的 build.gradle 中加入
android {
    ...
    buildFeatures {
        viewBinding true
    }
}
```

#### 旧版 Android Studio

```gradle
// 主项目的 build.gradle 中加入
android {
    ...
    viewBinding {
        enable = true
    }
}
```

### Step 2. 添加依赖

#### a. 克隆引入

直接下载源码引入 model

#### b. 远程仓库引入

[![](https://jitpack.io/v/sHadowLess-LhQ/BaseView.svg)](https://jitpack.io/#sHadowLess-LhQ/BaseView)

```gradle
// gradle.properties
// 没有声明转移至 androidX，需要声明，否则报错
android.enableJetifier=true

dependencies {
    implementation 'com.github.sHadowLess-LhQ:BaseView:Tag'
    implementation 'com.github.getActivity:XXPermissions:26'
}
```

#### c. 混淆规则

```proguard
-keepattributes Signature
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-keepclassmembers class rx.android.**{*;}
```

## 使用说明

### BaseVpActivity

```java
// 基类已实现异步布局加载，如需异步加载，请重写isAsyncLoadView方法
// 并返回true
// 若异步加载需要加载弹窗，需要重写initSyncView，并返回实现AsyncLoadViewCallBack
// 默认没有异步加载弹窗
// 需要实现异步加载View动画，请实现AsyncLoadViewCallBack，并重写startAsyncAnimSetView
// 一定要回调animStart，否则基类不会把视图添加至根视图
// 接口的对象，供基类调用，显示和关闭
// 创建xml后，点击编译，填入需要绑定的视图和传递数据类型
// click监听已做快速点击处理，请重写antiShakingClick接口
// 设置Activity主题，请重写initTheme()方法
// 设置权限申请前置步骤，请重写initPermission(String[] permissions)方法
// 如果单个Activity需要动态使用不同的布局文件，请给BaseActivity的泛型类型
// 传递ViewBinding，并重写setBindViewClass模板方法,传递不同ViewBinding类的class
// 如果不想用默认的反射去动态使用不同的布局，请重写inflateView模板方法
// 手动传递和实现ViewBinding类的实例
// 如果有反射加载视图慢的情况，请重写inflateView方法，手动实现ViewBinding类创建
// 需要更改点击防抖时间阈值，请重写isFastClick，在超类调用传递时间
// 需要在获取权限，请重写permissions方法
// 新增isForcePermissionToInitData方法，判断是否有需要获取权限后才允许获取数据
// 新增initDataByPermission方法，用于需要获取权限后才可以获取数据的逻辑
public class MainActivity extends BaseVpActivity<ActivityMainBinding> {

    @Nullable
    @Override
    public List<IPermission> permissions() {
        // 设置需要动态获取的权限
        return super.permissions();
    }
    
    @Override
    public List<IPermission> isForcePermissionToInitData() {
        // 强制获取哪些权限才可执行InitData获取数据
        return super.isForcePermissionToInitData();
    }

    @Override
    public void dealPermission(FragmentActivity activity, List<IPermission> permissions, OnPermissionInterceptor interceptor, OnPermissionResult callBack) {
        super.dealPermission(activity, permissions, interceptor, callBack);
        // 若使用基类获取权限，可重写此方法
        // 设置权限拦截器
        // 权限获取结果回调
    }
    
    @Override
    protected void initObject(Bundle savedInstanceState) {
        // 初始化对象
    }
    
    @Override
    protected void initView() {
        // 初始化视图
    }
    
    @Override
    protected void initViewListener() {
        // 初始化监听
        getBindView().test.setOnClickListener(this);
    }

    @Override
    protected void initDataListener() {
        // 初始化数据回调
    }
    
    @Override
    protected void initData() {
        // 初始化数据
    }
    
    @Override
    public void initDataByPermission() {
        super.initDataByPermission();
    }
    
    @Override
    public void antiShakingClick(View v) {
        super.antiShakingClick(v);
        // 点击事件
    }

    @Override
    public boolean isFastClick(int time) {
        // 传递需要的防抖时间阈值
        return super.isFastClick(time);
    }

    @Override
    public Class<ViewBinding> setBindViewClass() {
        // 反射动态布局
        Class<?> cls;
        if (i == 1) {
            cls = ActivityMainBinding.class;
        } else {
            cls = XxxBinding.class;
        }
        return (Class<ViewBinding>) cls;
    }
    
    @Override
    public ViewBinding inflateView(Object o, LayoutInflater layoutInflater) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 手动动态布局或手动初始化布局
        int i = 0;
        if (i == 0){
            return TestBinding.inflate(getLayoutInflater());
        }
        return Test1Binding.inflate(getLayoutInflater());
    }

    @Override
    public boolean isAsyncLoad() {
        // 是否异步加载视图
        return false;
    }
}
```

### BaseVpFragment

```java
// 基类已实现三种懒加载模式：
// DEFAULT：正常加载
// ONLY_LAZY_DATA：只懒加载数据
// LAZY_VIEW_AND_DATA：懒加载数据和页面
// 通过重写getLoadMode方法，返回不同的加载枚举
// 基类会执行不同的加载逻辑，不重写为DEFAULT
// 可通过isLazyInitSuccess方法，获取懒加载是否成功
// 基类已实现异步布局加载，如需异步加载，请重写isAsyncLoadView方法
// 并返回true
// 若异步加载需要加载弹窗，需要重写initSyncView，并返回实现AsyncLoadViewCallBack
// 默认没有异步加载弹窗
// 需要实现异步加载View动画，请实现AsyncLoadViewCallBack，并重写startAsyncAnimSetView
// 一定要回调animStart，否则基类不会把视图添加至根视图
// 接口的对象，供基类调用，显示和关闭
// 创建xml后，点击编译，填入需要绑定的视图和传递数据类型
// click监听已做快速点击处理，请重写antiShakingClick接口
// 设置Activity主题，请重写initTheme()方法
// 设置权限申请前置步骤，请重写initPermission(String[] permissions)方法
// 如果单个Fragment需要动态使用不同的布局文件，请给BaseFragment的泛型类型
// 传递ViewBinding，并重写setBindViewClass模板方法,传递不同ViewBinding类
// 如果不想用默认的反射去动态使用不同的布局，请重写inflateView模板方法
// 手动传递和实现ViewBinding类的实例
// 如果有反射加载视图慢的情况，请重写inflateView方法，手动实现ViewBinding类创建
// 需要更改点击防抖时间阈值，请重写isFastClick，在超类调用传递时间
// 需要获取权限，请重写permissions方法
// 新增isForcePermissionToInitData方法，判断是否有需要获取权限后才允许获取数据
// 新增initDataByPermission方法，用于需要获取权限后才可以获取数据的逻辑
public class MainFragment extends BaseVpFragment<FragmentMainBinding> {

    @Nullable
    @Override
    public List<IPermission> permissions() {
        // 设置需要动态获取的权限
        return super.permissions();
    }
    
    @Override
    public List<IPermission> isForcePermissionToInitData() {
        // 强制获取哪些权限才可执行InitData获取数据
        return super.isForcePermissionToInitData();
    }

    @Override
    public void dealPermission(Fragment fragment, List<IPermission> permissions, OnPermissionInterceptor interceptor, OnPermissionResult callBack) {
        super.dealPermission(fragment, permissions, interceptor, callBack);
        // 若使用基类获取权限，可重写此方法
        // 设置权限拦截器
        // 权限获取结果回调
    }

    @Override
    protected LoadMode getLoadMode() {
        // 设置加载模式
        return LoadMode.LAZY_VIEW_AND_DATA;
    }
    
    @Override
    protected void initFirst() {
        // 碎片第一次创建
    }
    
    @Override
    protected void initObject(Bundle savedInstanceState) {
        // 初始化对象
    }
    
    @Override
    protected void initView() {
        // 初始化视图
    }
    
    @Override
    protected void initViewListener() {
        // 初始化监听
        getBindView().test.setOnClickListener(this);
    }
    
    @Override
    protected void initDataListener() {
        // 初始化数据监听
    }
    
    @Override
    protected void initData() {
        // 初始化数据
    }
    
    @Override
    public void initDataByPermission() {
        super.initDataByPermission();
    }
    
    @Override
    public void antiShakingClick(View v) {
        super.antiShakingClick(v);
        // 点击事件
    }

    @Override
    public boolean isFastClick(int time) {
        // 传递需要的防抖时间阈值
        return super.isFastClick(time);
    }

    @Override
    public ViewBinding inflateView(Object o, LayoutInflater layoutInflater) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 手动动态布局或手动初始化布局
        int i = 0;
        if (i == 0){
            return TestBinding.inflate(getLayoutInflater());
        }
        return Test1Binding.inflate(getLayoutInflater());
    }

    @Override
    public Class<ViewBinding> setBindViewClass() {
        // 动态布局
        Class<?> cls;
        if (i == 1) {
            cls = ActivityMainBinding.class;
        } else {
            cls = XxxBinding.class;
        }
        return (Class<ViewBinding>) cls;
    }

    @Override
    public boolean isAsyncLoad() {
        // 是否异步加载视图
        return false;
    }
}
```

### BaseVmActivity

```java
// 基类已实现异步布局加载，如需异步加载，请重写isAsyncLoadView方法
// 并返回true
// 若异步加载需要加载弹窗，需要重写initSyncView，并返回实现AsyncLoadViewCallBack
// 默认没有异步加载弹窗
// 需要实现异步加载View动画，请实现AsyncLoadViewCallBack，并重写startAsyncAnimSetView
// 一定要回调animStart，否则基类不会把视图添加至根视图
// 接口的对象，供基类调用，显示和关闭
// 创建xml后，点击编译，填入需要绑定的视图和传递数据类型
// click监听已做快速点击处理，请重写antiShakingClick接口
// 设置Activity主题，请重写initTheme()方法
// 设置权限申请前置步骤，请重写initPermission(String[] permissions)方法
// 如果单个Activity需要动态使用不同的布局文件，请给BaseActivity的泛型类型
// 传递ViewBinding，并重写setBindViewClass模板方法,传递不同ViewBinding类的class
// 如果不想用默认的反射去动态使用不同的布局，请重写inflateView模板方法
// 手动传递和实现ViewBinding类的实例
// 如果有反射加载视图慢的情况，请重写inflateView方法，手动实现ViewBinding类创建
// 需要更改点击防抖时间阈值，请重写isFastClick，在超类调用传递时间
// 需要获取权限，请重写permissions方法
// 新增isForcePermissionToInitData方法，判断是否有需要获取权限后才允许获取数据
// 新增BaseViewModel#onModelInitDataByPermission方法，用于需要获取权限后才可以获取数据的逻辑
public class MainActivity extends BaseVmActivity<ActivityMainBinding> {

    private TestViewModel viewModel;

    @Nullable
    @Override
    public List<IPermission> permissions() {
        // 设置需要动态获取的权限
        return super.permissions();
    }
    
    @Override
    public List<IPermission> isForcePermissionToInitData() {
        // 强制获取哪些权限才可执行InitData获取数据
        return super.isForcePermissionToInitData();
    }

    @Override
    public void dealPermission(FragmentActivity activity, List<IPermission> permissions, OnPermissionInterceptor interceptor, OnPermissionResult callBack) {
        super.dealPermission(activity, permissions, interceptor, callBack);
        // 若使用基类获取权限，可重写此方法
        // 设置权限拦截器
        // 权限获取结果回调
    }
    
    @Override
    protected void initObject() {
        // 初始化对象
        // 如果使用的ViewModel必须继承自BaseViewModel
        // 可按照常规方法初始化viewModel，kotlin使用by viewModels()也行
        // 框架提供一个createActivityViewModel方法实例化
        viewModel = createViewModel(this, TestViewModel.class);
    }
    
    @NonNull
    @Override
    public List<BaseViewModel<ActivityMainBinding, ?>> collectionViewModels() {
        // 一定要传入，否则相关抽象方法不会调用
        // 设置已经初始化的ViewModel
        // 有多个需传递多个
        return Collections.singletonList(viewModel);
    }
    
    @Override
    protected void initView() {
        // 初始化视图
    }
    
    @Override
    protected void initViewListener() {
        // 初始化监听
        getBindView().test.setOnClickListener(this);
    }

    @Override
    protected void initModelListener() {
        // 初始化ViewModel通讯订阅
    }
    
    @Override
    public void antiShakingClick(View v) {
        super.antiShakingClick(v);
        // 点击事件
    }

    @Override
    public boolean isFastClick(int time) {
        // 传递需要的防抖时间阈值
        return super.isFastClick(time);
    }

    @Override
    public Class<ViewBinding> setBindViewClass() {
        // 反射动态布局
        Class<?> cls;
        if (i == 1) {
            cls = ActivityMainBinding.class;
        } else {
            cls = XxxBinding.class;
        }
        return (Class<ViewBinding>) cls;
    }
    
    @Override
    public ViewBinding inflateView(Object o, LayoutInflater layoutInflater) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 手动动态布局或手动初始化布局
        int i = 0;
        if (i == 0){
            return TestBinding.inflate(getLayoutInflater());
        }
        return Test1Binding.inflate(getLayoutInflater());
    }

    @Override
    public boolean isAsyncLoad() {
        // 是否异步加载视图
        return false;
    }
}
```

### BaseVmFragment

```java
// 基类已实现三种懒加载模式：
// DEFAULT：正常加载
// ONLY_LAZY_DATA：只懒加载数据
// LAZY_VIEW_AND_DATA：懒加载数据和页面
// 通过重写getLoadMode方法，返回不同的加载枚举
// 基类会执行不同的加载逻辑，不重写为DEFAULT
// 可通过isLazyInitSuccess方法，获取懒加载是否成功
// 基类已实现异步布局加载，如需异步加载，请重写isAsyncLoadView方法
// 并返回true
// 若异步加载需要加载弹窗，需要重写initSyncView，并返回实现AsyncLoadViewCallBack
// 默认没有异步加载弹窗
// 需要实现异步加载View动画，请实现AsyncLoadViewCallBack，并重写startAsyncAnimSetView
// 一定要回调animStart，否则基类不会把视图添加至根视图
// 接口的对象，供基类调用，显示和关闭
// 创建xml后，点击编译，填入需要绑定的视图和传递数据类型
// click监听已做快速点击处理，请重写antiShakingClick接口
// 设置Activity主题，请重写initTheme()方法
// 设置权限申请前置步骤，请重写initPermission(String[] permissions)方法
// 如果单个Fragment需要动态使用不同的布局文件，请给BaseFragment的泛型类型
// 传递ViewBinding，并重写setBindViewClass模板方法,传递不同ViewBinding类
// 如果不想用默认的反射去动态使用不同的布局，请重写inflateView模板方法
// 手动传递和实现ViewBinding类的实例
// 如果有反射加载视图慢的情况，请重写inflateView方法，手动实现ViewBinding类创建
// 需要更改点击防抖时间阈值，请重写isFastClick，在超类调用传递时间
// 需要获取权限，请重写permissions方法
// 新增isForcePermissionToInitData方法，判断是否有需要获取权限后才允许获取数据
// 新增BaseViewModel#onModelInitDataByPermission方法，用于需要获取权限后才可以获取数据的逻辑
public class MainFragment extends BaseVmFragment<FragmentMainBinding> {

    private TestViewModel viewModel;

    @Nullable
    @Override
    public List<IPermission> permissions() {
        // 设置需要动态获取的权限
        return super.permissions();
    }
    
    @Override
    public List<IPermission> isForcePermissionToInitData() {
        // 强制获取哪些权限才可执行InitData获取数据
        return super.isForcePermissionToInitData();
    }

    @Override
    public void dealPermission(Fragment fragment, List<IPermission> permissions, OnPermissionInterceptor interceptor, OnPermissionResult callBack) {
        super.dealPermission(fragment, permissions, interceptor, callBack);
        // 若使用基类获取权限，可重写此方法
        // 设置权限拦截器
        // 权限获取结果回调
    }

    @Override
    protected LoadMode getLoadMode() {
        // 设置加载模式
        return LoadMode.LAZY_VIEW_AND_DATA;
    }
    
    @Override
    protected void initFirst() {
        // 碎片第一次创建
    }
    
    @Override
    protected void initObject() {
        // 初始化对象
        // 如果使用的ViewModel必须继承自BaseViewModel
        // 可按照常规方法初始化viewModel，kotlin使用by viewModels()也行
        // 框架提供一个createActivityViewModel方法实例化
        viewModel = createViewModel(this, TestViewModel.class);
    }
    
    @NonNull
    @Override
    public List<BaseViewModel<ActivityMainBinding, ?>> collectionViewModels() {
        // 一定要传入，否则相关抽象方法不会调用
        // 设置已经初始化的ViewModel
        // 有多个需传递多个
        return Collections.singletonList(viewModel);
    }   
    
    @Override
    protected void initView() {
        // 初始化视图
    }
    
    @Override
    protected void initViewListener() {
        // 初始化监听
        getBindView().test.setOnClickListener(this);
    }
    
    @Override
    protected void initModelListener() {
        // 初始化ViewModel通讯订阅
    }
    
    @Override
    public void antiShakingClick(View v) {
        super.antiShakingClick(v);
        // 点击事件
    }

    @Override
    public boolean isFastClick(int time) {
        // 传递需要的防抖时间阈值
        return super.isFastClick(time);
    }

    @Override
    public ViewBinding inflateView(Object o, LayoutInflater layoutInflater) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        // 手动动态布局或手动初始化布局
        int i = 0;
        if (i == 0){
            return TestBinding.inflate(getLayoutInflater());
        }
        return Test1Binding.inflate(getLayoutInflater());
    }

    @Override
    public Class<ViewBinding> setBindViewClass() {
        // 动态布局
        Class<?> cls;
        if (i == 1) {
            cls = ActivityMainBinding.class;
        } else {
            cls = XxxBinding.class;
        }
        return (Class<ViewBinding>) cls;
    }

    @Override
    public boolean isAsyncLoad() {
        // 是否异步加载视图
        return false;
    }
}
```

### BaseMutual*

```kotlin
// 支持双向等待机制的基类
// 涵盖以上所有Activity和Fragment
// 机制只有在isAsyncLoad()为true的异步布局加载情况下生效
// 同步没必要双向等待
// 剩余功能完全一致
// 以MutualVpActivity为简单使用的例子
class TestActivity : BaseMutualVpActivity<ActivityMain3Binding>() {

    private val userData: MutableLiveData<User> = MutableLiveData()

    // 声明为true
    // 否则使用getBindManager()方法会抛出异常
    override fun isAsyncLoadView(): Boolean = true

    override fun initObject(savedInstanceState: Bundle?) {
        
    }

    override fun initView() {
        // 设置绑定器
        bindManager.setBinder { data, _ ->
            Log.e("TAG", "initDataListener: ${data.toString()}")
        }
    }

    override fun initViewListener() {
        
    }

    override fun initDataListener() {
        userData.observe(this) {
            bindManager.setData(it)
        }
    }

    override fun initData() {
        // 模拟异步发送数据
        lifecycleScope.launch(Dispatchers.IO) {
            delay(5000)
            withContext(Dispatchers.Main) {
                userData.value = User("小王", 18)
            }
        }
    }
}
```

### BaseDialog

```java
// 创建xml后，点击编译，填入需要绑定的视图
// 支持ViewBinding
// 自身支持Lifecycle
// 支持监听其他Lifcycle
// click监听已做快速点击处理，请重写antiShakingClick接口
// 如果单个Dialog需要动态使用不同的布局文件，请给BaseDialog的泛型类型
// 传递ViewBinding，并重写setBindViewClass模板方法,传递不同ViewBinding类
// 继承示例
public class TestDialog extends BaseDialog<PopTestBinding> {

    public TestDialog(@NonNull Context context) {
        super(context);
        // 需要观察的Lifecycle
        setNeedObserveLifecycle(Lifecycle lifecycle);
    }

    public TestDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        // 库里自带一个全屏Dialog主题BaseFullDialog
        // 如不合要求，请自定义后传入
    }

    @Override
    protected String setBindViewClass() {
        // 动态布局
        Class<?> cls;
        if (i == 1) {
            cls = ActivityMainBinding.class;
        } else {
            cls = XxxBinding.class;
        }
        return (Class<ViewBinding>) cls;
    }
    
    @Override
    protected PopTestBinding inflateView() {
        // 可重写后实现视图初始化或手动动态布局
        return super.inflateView();
    }
    
    @Override
    protected DialogSetting setDialogParam() {
        DialogSetting setting = new DialogSetting();
        // 按照顺序传入指定的X/Y/宽/高数值
        setting.setDialogParams(int[] dialogParams);
        // 传入Gravity的位置
        setting.setDialogPosition(int dialogPosition)
        // 是否清除边框
        setting.setClearPadding(boolean clearPadding)
        // 是否允许外部取消
        setting.setCancelOutside(boolean cancelOutside)
        // 是否允许拖动
        setting.setDrag(boolean drag)
        // 是否拥有阴影
        setting.setHasShadow(boolean hasShadow)
        // 设置Dialog的窗体标识
        setting.setSetFlag(int setFlag)
        return setting;
    }

    @Override
    protected void initObject(Bundle savedInstanceState) {
        // 初始化对象
    }
    
    @Override
    protected void initView() {
        // 初始化视图
    }
    
    @Override
    protected void initViewListener() {
        // 初始化视图监听
    }
    
    @Override
    protected void initDataListener() {
        // 初始化数据监听
    }

    @Override
    protected void initData() {
        // 初始化数据
    }
    
    @Override
    public boolean isFastClick(int time) {
        // 传递需要的防抖时间阈值
        return PublicEvent.super.isFastClick(time);
    }
    
    @Override
    public void antiShakingClick(View v) {
        // 点击处理
    }
    
    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        // 监听其他Lifecycle组件的声明周期标识符
    }
}
```

### BaseMutableLiveData

```java
// MutableLiveData聚合类
// 目的是解耦发散在Presenter或ViewModel中定义的MutableLiveData
// 已实现LifecycleEventObserver、LifecycleOwner接口
// 支持监听生命周期事件（被监听需自实现Lifecycle逻辑，并重写getLifecycle()）
// 定义的全局MutableLiveData变量默认无需手动实例化
// BaseMutableLiveData自动反射实例化子类定义的所有全局MutableLiveData变量
// 若不想反射，则在构造超类中，第二参数传递false后，手动实例化即可
// 新增实现UpdateObjEvent接口，用于在ViewModel中，旋转屏幕后更新VmObjManager实例
// 默认自动反射更新类中实现接口的全局变量，若需手动，重写update方法，将isAutoUpdate传入false
// 若全局变量实际对象被隐藏，如懒加载、Kotlin委托，请在对象打上注解@UpdateReflect
// 注解默认提供处理Kotlin委托变量的更新的事件，如需其他处理，可实现UpdateReflectEvent接口
// 在需要处理且实现UpdateObjEvent接口的类中，重写update方法，将实现接口的类，添加到events集合
// 若有其他额外对象需要更新，请重写update方法后填入逻辑
public class TestMutable extends BaseMutableLiveData {

    private MutableLiveData<Integer> testInteger;

    public TestMutable(LifecycleOwner lifecycleOwner) {
        // 默认构造超类，自动反射实例化MutableLiveData
        super(lifecycleOwner);
        // 传递false，取消自动反射实例化
        super(lifecycleOwner,false);
        // 添加单个需要永久监听的MutableLiveData
        setForeverObserve(LiveData<?> mutableLiveData);
        // 添加多个需要永久监听的MutableLiveData
        this.setForeverObserve(LiveData<?>... mutableLiveData);
        // 解除永久监听的MutableLiveData订阅（默认无需手动调用，已在生命周期回调中使用）
        this.clearAllForEverObserver(LifecycleOwner owner);
    }

    @Override
    public void onTerminate() {

    }
    
    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        super.onStateChanged(source, event);
    }
    
    @Override
    public void update(@NonNull VmObjManager<? extends ViewBinding> manager) {
        UpdateObjEvent.super.update(manager);
        //在此补充额外需要更新实例的逻辑
    }
    
    @Override
    public void update(@NonNull VmObjManager<? extends ViewBinding> manager, boolean isAutoUpdate) {
        //在此启用/停用自动更新
        UpdateObjEvent.super.update(manager, isAutoUpdate);
    }
    
    @Override
    public void update(Class<?> cls, Object obj, @NonNull VmObjManager<? extends ViewBinding> manager, boolean isAutoUpdate, List<UpdateReflectEvent> events) {
        //在此启用/停用自动更新
        //在此传入基于注解需要额外处理的反射的逻辑
        UpdateObjEvent.super.update(cls, obj, manager, isAutoUpdate, events);
    }

    public MutableLiveData<Integer> getTestInteger() {
        return testInteger;
    }
}
```

### BasePresenter

```java
// Presenter基类
// 已实现LifecycleEventObserver、LifecycleOwner接口
// 支持监听和生命周期事件（被监听需自实现Lifecycle逻辑，并重写getLifecycle()）
// 新增实现UpdateObjEvent接口，用于在ViewModel中，旋转屏幕后更新VmObjManager实例
// 默认自动反射更新类中实现接口的全局变量，若需手动，重写update方法，将isAutoUpdate传入false
// 若全局变量实际对象被隐藏，如懒加载、Kotlin委托，请在对象打上注解@UpdateReflect
// 注解默认提供处理Kotlin委托变量的更新的事件，如需其他处理，可实现UpdateReflectEvent接口
// 在需要处理且实现UpdateObjEvent接口的类中，重写update方法，将实现接口的类，添加到events集合
// 若有其他额外对象需要更新，请重写update方法后填入逻辑
public class TestPresenter extends BasePresenter<TestMutable> {

    private final TestMutable testMutable;

    public TestPresenter(LifecycleOwner observeLifecycle) {
        super(observeLifecycle);
        this.testMutable = new TestMutable(observeLifecycle);
    }

    @Override
    public TestMutable getMutable() {
        return testMutable;
    }

    @Override
    public void onTerminate() {

    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        super.onStateChanged(source, event);
    }
    
    @Override
    public void update(@NonNull VmObjManager<? extends ViewBinding> manager) {
        super.update(manager);
        //在此补充额外需要更新实例的逻辑
    }
    
    @Override
    public void update(@NonNull VmObjManager<? extends ViewBinding> manager, boolean isAutoUpdate) {
        //在此启用/停用自动更新
        UpdateObjEvent.super.update(manager, isAutoUpdate);
    }
    
    @Override
    public void update(Class<?> cls, Object obj, @NonNull VmObjManager<? extends ViewBinding> manager, boolean isAutoUpdate, List<UpdateReflectEvent> events) {
        //在此启用/停用自动更新
        //在此传入基于注解需要额外处理的反射的逻辑
        UpdateObjEvent.super.update(cls, obj, manager, isAutoUpdate, events);
    }
}
```

### BaseViewModel/BaseMutualViewModel

```java
// ViewModel基类
// 后者为使用双向等待基类所需使用的配套ViewModel，使用方式无异
// 已实现LifecycleEventObserver、LifecycleOwner接口
// 已支持监听和生命周期事件
// 可搭配presenter使用，进行数据获取解耦，model内只处理业务数据逻辑和页面数据绑定
// 新增实现UpdateObjEvent接口，用于在ViewModel中，旋转屏幕后更新VmObjManager实例
// 默认自动反射更新类中实现接口的全局变量，若需手动，重写update方法，将isAutoUpdate传入false
// 若全局变量实际对象被隐藏，如懒加载、Kotlin委托，请在对象打上注解@UpdateReflect
// 注解默认提供处理Kotlin委托变量的更新的事件，如需其他处理，可实现UpdateReflectEvent接口
// 在需要处理且实现UpdateObjEvent接口的类中，重写update方法，将实现接口的类，添加到events集合
// 若有其他额外对象需要更新，请重写update方法后填入逻辑
public class TestViewModel extends BaseViewModel<ActivityMain2Binding, TestMutable> {

    private TestMutable testMutable;

    private TestPresenter presenter;

    @Override
    public TestMutable getMutable() {
        return testMutable;
    }

    @Override
    public void onModelCreated() {
        //如果项目场景不存在旋转屏幕的情况
        //在这里初始化或全局初始化都可以
        //若存在旋转场景且不想activity重建后重新实例化
        //BaseMutableLiveData和BasePresenter已实现UpdateObjEvent接口，调用update方法更新即可
        //其他自定义类，实现UpdateObjEvent接口后自行展开逻辑
        this.testMutable = new TestMutable(getObserveLifecycleOwner());
        this.presenter = new TestPresenter(getObserveLifecycleOwner());
    }
    
    @Override
    public void onModelInitView() {
        // 视图准备完成
        // 初始化视图
    }
    
    @Override
    public void onModelInitListener() {
        // 初始化数据监听
        // 若是双向等待，这里可以设置双向监听
        presenter.getMutable().getTestInteger().observe(observeLifecycle(), integer -> getBindView().test.setText(integer + ""));
    }
    
    @Override
    public void onModelInitData() {
        
    }
    
    @Override
    public void onModelInitDataByPermission() {
        super.onModelInitDataByPermission()
    }

    @Override
    public void onTerminate() {

    }
    
    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        super.onStateChanged(source, event);
    }
    
    @Override
    public void update(@NonNull VmObjManager<? extends ViewBinding> manager) {
        super.update(manager);
        //在此补充额外需要更新实例的逻辑
    }
    
    @Override
    public void update(@NonNull VmObjManager<? extends ViewBinding> manager, boolean isAutoUpdate) {
        //在此启用/停用自动更新
        UpdateObjEvent.super.update(manager, isAutoUpdate);
    }
    
    @Override
    public void update(Class<?> cls, Object obj, @NonNull VmObjManager<? extends ViewBinding> manager, boolean isAutoUpdate, List<UpdateReflectEvent> events) {
        //在此启用/停用自动更新
        //在此传入基于注解需要额外处理的反射的逻辑
        UpdateObjEvent.super.update(cls, obj, manager, isAutoUpdate, events);
    }

    public void test() {
        presenter.testInteger();
    }
}
```

### BaseApplication

```java
// 支持应用前后台判断
// 已接入MyActivityManager，可直接使用
// 已接入MyApplicationManager，可直接使用
// 已实现ViewModelStoreOwner，可通过getGlobalViewModel()获取全局ViewModel
public class MyApplication extends BaseApplication {

    @Override
    protected void init() {
        // 初始化
    }
    
}

// 判断应用在前/后台
// true为前台
// false为后台
MyApplication.isAppForeground()
```

### ActivityManager

```java
// 使用BaseApplication可直接使用
// 单独使用，需要在ActivityLifecycleCallbacks回调中的onActivityResumed设置
// 支持弱引用，获取到的Activity已做弱引用处理
// 设置当前正在显示的Activity
MyActivityManager.INSTANCE.setCurrentActivity(Activity activity);
// 获取当前正在显示的Activity
MyActivityManager.INSTANCE.getCurrentActivity();
```

### ContextManager

```java
// 使用BaseApplication可直接使用
// 单独使用，需要在Application的onCreate设置
// 支持弱引用，获取到的Context已做弱引用处理
// 设置当前Context
MyApplicationManager.INSTANCE.setCurrentContext(Context context);
// 获取当前Context
MyApplicationManager.INSTANCE.getCurrentContext();
```

### CrashConfig

```java
CrashConfig.Builder
        .create()
        // 当应用程序处于后台时崩溃，也会启动错误页面
        .backgroundMode(CrashConfig.BACKGROUND_MODE_SHOW_CUSTOM)
        // 当应用程序处于后台崩溃时显示默认系统错误
        .backgroundMode(CrashConfig.BACKGROUND_MODE_CRASH)
        // 当应用程序处于后台时崩溃，默默地关闭程序
        .backgroundMode(CrashConfig.BACKGROUND_MODE_SILENT)
        // false表示对崩溃的拦截阻止。用它来禁用customactivityoncrash
        .enabled(true)
        // 这将隐藏错误活动中的"错误详细信息"按钮，从而隐藏堆栈跟踪,针对框架自带程序崩溃后显示的页面有用
        .showErrorDetails(true)
        // 是否可以重启页面,针对框架自带程序崩溃后显示的页面有用
        .showRestartButton(true)
        // 崩溃页面显示的图标
        .errorDrawable(R.mipmap.ic_launcher)
        .logErrorOnRestart(true)
        // 错误页面中显示错误详细信息
        .trackActivities(true)
        // 定义应用程序崩溃之间的最短时间，以确定我们不在崩溃循环中
        .minTimeBetweenCrashesMs(2000)
        // 重新启动后的页面
        .restartActivity(LoginActivity.class)
        .apply();
        
// 需要在AM文件中声明Provider，以启用崩溃组件
<provider
    android:name="cn.com.shadowless.baseview.crash.provider.CrashInitProvider"
    android:authorities="${applicationId}.customActivityOnCrashInitProvider"
    android:exported="false"
    android:initOrder="101" />
```

### BaseQuickLifecycle

```java
// 快速实现一个自身支持Lifecycle和支持监听其他Lifecycle的类
// 默认不重写getLifecycle时，lifecycle只跟监听的生命周期有关
// 重写并实现自身生命周期逻辑后，才区分自身与监听
public class Test implements BaseQuickLifecycle {

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event){
    
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        // 实现自己的Lifecycle事件逻辑
        return null;
    }
    
    @Nullable
    @Override
    public LifecycleOwner getObserveLifecycleOwner() {
        // 监听的生命周期
        return observeLifecycle;
    }
}
```

### SingleMutableLiveData<T>

```java
// 可动态变更是否粘性接收的LiveData
// 使用和正常的LiveData相同
// 新增方法：
// 设置是否粘性接收（发送前或接收后设置）
SingleMutableLiveData<?>.setSingleEvent(boolean singleEvent);
```
