# BaseView

#### 软件架构

个人Android项目快速搭建框架基类

#### 安装教程

Step 1. 添加maven仓库地址和配置

```
     //旧AndroidStudio版本
     //build.gradle
     allprojects {
         repositories {
            ...
              maven { url 'https://jitpack.io' }
         }
     }
     
     //新AndroidStudio版本
     //settings.gradle
     dependencyResolutionManagement {
          repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
          repositories {
            ...
             maven { url 'https://jitpack.io' }
          }
      }
```

```
     //主项目的build.gradle中加入
     //新AndroidStudio版本
     android {
      ...
       buildFeatures {
         viewBinding true
          }
     }
     
     //主项目的build.gradle中加入
     //旧AndroidStudio版本
     android {
      ...
       viewBinding {
         enable = true
          }
     }
```

Step 2. 添加依赖

a、克隆引入

直接下载源码引入model

b、远程仓库引入

[[![](https://jitpack.io/v/com.gitee.shadowless_lhq/base-view.svg)](https://jitpack.io/#com.gitee.shadowless_lhq/base-view)

```
    dependencies {
        implementation 'com.gitee.shadowless_lhq:base-view:Tag'
        implementation 'io.reactivex.rxjava2:rxjava:2.2.21'
        implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
        implementation 'com.github.liujingxing.rxlife:rxlife-rxjava2:2.2.2'
    }
```

c、混淆规则

```
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

#### 使用说明

### BaseActivity

```
      //创建xml后，点击编译，填入需要绑定的视图和传递数据类型
      //click监听已做快速点击处理
      //填入传递数据类型
      //设置Activity主题，请重写initTheme()方法
      //设置initData所处线程，请重写setScheduler()方法
      //设置权限申请前置步骤，请重写initPermission(String[] permissions)方法
      public class MainActivity extends BaseActivity<ActivityMainBinding,String> {
    
          @Nullable
          @Override
          protected String[] permissions() {
             //设置需要获取的权限，无需申请可传null或空数组
             return null;
          }
      
          @NonNull
          @Override
          protected ActivityMainBinding setBindView() {
             //回传ViewBinding绑定的视图
             return ActivityMainBinding.inflate(getLayoutInflater());
          }
      
          @Override
          protected void initData(@NonNull InitDataCallBack<String> initDataCallBack) {
             //初始化数据
             【注】：若在initData()中需要同时从多个接口获取数据，可以使用RxJava的zip操作符，将数据进行集中处理后，再通过InitDataCallBack回调自己的装箱数据
             //若有数据给视图绑定，使用initViewWithData
             //若无数据给视图绑定，使用initViewWithOutData
             initDataCallBack.initViewWithData("1");
             initDataCallBack.initViewWithOutData();
          }
          
          @Override
          protected void initListener() {
             //初始化监听
             getBindView().test.setOnClickListener(this);
          }
      
          @Override
          protected void initSuccessView(@Nullable String data) {
             //默认在主线程
             //初始化界面控件
             getBindView().test.setText(data);
          }
          
          @Override
          protected void initFailView(@Nullable String error, @Nullable Throwable e) {
              //处理数据失败
              //错误页面
          }
          
          @Override
          protected void click(View v) {
              //检查权限
              hasPermission(Manifest.permission.CAMERA);
          }
          
          @Override
          protected void initPermission(String[] permissions) {
              //去除超类
              //比如需要申请前的说明
              //包裹后调用dealPermission(String[] permissions, PermissionCallBack callBack)方法
              //需要自己获取回调，需实现PermissionCallBack接口
              //不需要直接传null
              //任意控件事件动态申请权限，请使用PermissionUtils
              new AlertDialog.Builder(this)
                      .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              dealPermission(permissions, new PermissionCallBack() {
                                  @Override
                                  public void agree() {
      
                                  }
      
                                  @Override
                                  public void disagree(List<String> name) {
      
                                  }
      
                                  @Override
                                  public void ban(List<String> name) {
      
                                  }
                              });
                          }
                      })
                      .show();
          }
          
          @Override
          protected Scheduler[] setScheduler() {
              //去除超类
              //参数1为注册+注销线程
              //参数2为订阅线程
              return new Scheduler[]{
                       Schedulers.io(),
                       AndroidSchedulers.mainThread()
              };
          }
      }
```

### BaseFragment

```
      //创建xml后，点击编译，填入需要绑定的视图和传递数据类型
      //click监听已做快速点击处理
      //填入传递数据类型
      //设置Activity主题，请重写initTheme()方法
      //设置initData所处线程，请重写setScheduler()方法
      //设置权限申请前置步骤，请重写initPermission(String[] permissions)方法
      public class MainFragment extends BaseFragment<FragmentMainBinding,String> {
  
          @Nullable
          @Override
          protected String[] permissions() {
             //设置需要获取的权限，无需申请可传null或空数组
             return null;
          }
      
          @NonNull
          @Override
          protected FragmentMainBinding setBindView() {
              //回传ViewBinding绑定的视图
              return FragmentMainBinding.inflate(getLayoutInflater());
          }
      
          @Override
          protected void initData(@NonNull InitDataCallBack<String> initDataCallBack) {
             //初始化数据
             【注】：若在initData()中需要同时从多个接口获取数据，可以使用RxJava的zip操作符，将数据进行集中处理后，再通过InitDataCallBack回调自己的装箱数据
             Toast.makeText(getBindActivity(), "可用Activity对象", Toast.LENGTH_SHORT).show();
             //若有数据给视图绑定，使用initViewWithData
             //若无数据给视图绑定，使用initViewWithOutData
             initDataCallBack.initViewWithData("1");
             initDataCallBack.initViewWithOutData();
          }
          
          @Override
          protected void initListener() {
             //初始化监听
             getBindView().test.setOnClickListener(this);
          }
      
          @Override
          protected void initSuccessView(@Nullable String map) {
             //默认在主线程
             //初始化界面控件
             getBindView().test.setText(map);
          }
          
          @Override
          protected void initFailView(@Nullable String error, @Nullable Throwable e) {
      
          }
          
          @Override
          protected void click(View v) {
              //检查权限
              hasPermission(Manifest.permission.CAMERA);
          }
          
          @Override
          protected void initPermission(String[] permissions) {
              //去除超类
              //重写例子，比如需要申请前的说明
              //包裹后调用dealPermission(String[] permissions, PermissionCallBack callBack)方法
              //需要自己获取回调，需实现PermissionCallBack接口
              //不需要直接传null
              //任意控件事件动态申请权限，请使用PermissionUtils
              new AlertDialog.Builder(getActivity())
                      .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              dealPermission(permissions, new PermissionCallBack() {
                                  @Override
                                  public void agree() {
      
                                  }
      
                                  @Override
                                  public void disagree(List<String> name) {
      
                                  }
      
                                  @Override
                                  public void ban(List<String> name) {
      
                                  }
                              });
                          }
                      })
                      .show();
          }
          
          @Override
          protected Scheduler[] setScheduler() {
              //去除超类
              //参数1为注册+注销线程
              //参数2为订阅线程
              return new Scheduler[]{
                       Schedulers.io(),
                       AndroidSchedulers.mainThread()
              };
          }
      }
```

### BaseDialog

```
      //创建xml后，点击编译，填入需要绑定的视图
      //支持ViewBinding
      //自身支持Lifecycle
      //支持监听其他Lifcycle
      //click监听已做快速点击处理
      //继承示例
      public class TestDialog extends BaseDialog<PopTestBinding> {
      
            public TestDialog(@NonNull Context context) {
                super(context);
                //需要观察的Lifecycle
                setNeedObserveLifecycle(Lifecycle lifecycle);
            }
        
            public TestDialog(@NonNull Context context, int themeResId) {
                super(context, themeResId);
            }
        
            @NonNull
            @Override
            protected PopTestBinding setBindView() {
                return PopTestBinding.inflate(getLayoutInflater());
            }
        
            @Override
            protected int[] dialogParams() {
                //按照顺序传入指定的X/Y/宽/高数值
                return new int[0];
            }
        
            @Override
            protected int dialogPosition() {
                //传入Gravity的位置
                return 0;
            }
        
            @Override
            protected boolean clearPadding() {
                //是否清除边框
                return false;
            }
        
            @Override
            protected boolean cancelOutside() {
                //是否允许外部取消
                return false;
            }
        
            @Override
            protected boolean isDrag() {
                //是否允许拖动
                return false;
            }
        
            @Override
            protected boolean hasShadow() {
                //是否拥有阴影
                return false;
            }
        
            @Override
            protected int setFlag() {
                //设置Dialog的窗体标识
                return 0;
            }
        
            @Override
            protected void initView() {
               
            }
        
            @Override
            protected void initListener() {
        
            }
        
            @Override
            protected void click(@NonNull View v) {
        
            }
            
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                //监听其他Lifecycle的声明周期
            }
      }
```

### BaseApplication

```
      //支持应用前后台判断
      //已接入MyActivityManager，可直接使用
      //已接入MyApplicationManager，可直接使用
      public class MyApplication extends BaseApplication {
      
          @Override
          protected void init() {
             //初始化
          }
          
      }
      
      //判断应用在前/后台
      //true为前台
      //false为后台
      MyApplication.isAppForeground()
```

### ActivityManager

```
      //使用BaseApplicatio可直接使用
      //单独使用，需要在ActivityLifecycleCallbacks回调中的onActivityResumed设置
      //支持弱引用，获取到的Activity已做弱引用处理
      //设置当前正在显示的Activity
      MyActivityManager.INSTANCE.setCurrentActivity(Activity activity);
      //获取当前正在显示的Activity
      MyActivityManager.INSTANCE.getCurrentActivity();
```

### ContextManager

```
      //使用BaseApplicatio可直接使用
      //单独使用，需要在Application的onCreate设置
      //支持弱引用，获取到的Context已做弱引用处理
      //设置当前Context
      MyApplicationManager.INSTANCE.setCurrentContext(Context context);
      //获取当前Cotext
      MyApplicationManager.INSTANCE.getCurrentContext();
```

### CrashConfig

```
      CrashConfig.Builder
                .create()
                //当应用程序处于后台时崩溃，也会启动错误页面
                .backgroundMode(CrashConfig.BACKGROUND_MODE_SHOW_CUSTOM)
                //当应用程序处于后台崩溃时显示默认系统错误
                .backgroundMode(CrashConfig.BACKGROUND_MODE_CRASH)
                //当应用程序处于后台时崩溃，默默地关闭程序
                .backgroundMode(CrashConfig.BACKGROUND_MODE_SILENT)
                //false表示对崩溃的拦截阻止。用它来禁用customactivityoncrash
                .enabled(true)
                //这将隐藏错误活动中的“错误详细信息”按钮，从而隐藏堆栈跟踪,针对框架自带程序崩溃后显示的页面有用
                .showErrorDetails(true)
                //是否可以重启页面,针对框架自带程序崩溃后显示的页面有用
                .showRestartButton(true)
                //崩溃页面显示的图标
                .errorDrawable(R.mipmap.ic_launcher)
                .logErrorOnRestart(true)
                //错误页面中显示错误详细信息
                .trackActivities(true)
                //定义应用程序崩溃之间的最短时间，以确定我们不在崩溃循环中
                .minTimeBetweenCrashesMs(2000)
                //重新启动后的页面
                .restartActivity(LoginActivity.class)
                .apply();
```

### BaseQuickLifecycle

```
      //快速实现一个支持Lifecycle和支持监听其他Lifecycle的类
      public class Test implements BaseQuickLifecycle {
      
          @Override
          public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event){
          
          }
      
          @NonNull
          @Override
          public Lifecycle getLifecycle() {
              //实现自己的Lifecycle注册
              return null;
          }
      }
```

### BaseQucikLifeleImpl

```
      //超快速实现一个可以使用的支持Lifecycle和支持监听其他Lifecycle的类
      //适合无继承关系的任意类使用
      //有继承关系请使用BaseQuickLifecycle
      //直接继承BaseQuickLifecycleImpl
      //在此类的业务逻辑中的合适位置发送声明周期事件
      public class Test extends BaseQuickLifecycleImpl{
      
          public Test() {
              //默认构造调用的时候发送ON_CREATE事件，不需要请移除超类后自发送
              super();
          }
      
          @Override
          public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
             //发送生命周期事件
             getLifecycleRegistry().handleLifecycleEvent(Lifecycle.Event event);
          }
      }
```

### PermissionUtils

```
      //获取权限观察者
      PermissionUtils.getPermissionObservable(Fragment fragment, View view, String[] permissions)
      PermissionUtils.getPermissionObservable(FragmentActivity activity, View view, String[] permissions)
      PermissionUtils.getPermissionObservable(Fragment fragment, LifecycleOwner owner, String[] permissions)
      PermissionUtils.getPermissionObservable(FragmentActivity activity, LifecycleOwner owner, String[] permissions);
      //处理权限
      PermissionUtils.dealPermission(FragmentActivity activity, View view, String[] permissions, PermissionCallBack callBack)
      PermissionUtils.dealPermission(Fragment fragment, View view, String[] permissions, PermissionCallBack callBack)
      PermissionUtils.dealPermission(Fragment fragment, LifecycleOwner owner, String[] permissions, PermissionCallBack callBack)
      PermissionUtils.dealPermission(FragmentActivity activity, LifecycleOwner owner, String[] permissions, PermissionCallBack callBack)
```
