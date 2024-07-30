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

[![](https://jitpack.io/v/com.gitee.shadowless_lhq/base-view.svg)](https://jitpack.io/#com.gitee.shadowless_lhq/base-view)

```
    dependencies {
        implementation 'com.gitee.shadowless_lhq:base-view:Tag'
        implementation 'com.gitee.shadowless_lhq:permission-lib:1.0.0'
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
      //基类已实现异步布局加载，如需异步加载，请重写isAsyncLoadView方法
      //并返回true
      //若异步加载需要加载弹窗，需要重写initSyncView，并返回实现AsyncLoadViewCallBack
      //默认没有异步加载弹窗
      //接口的对象，供基类调用，显示和关闭
      //创建xml后，点击编译，填入需要绑定的视图和传递数据类型
      //click监听已做快速点击处理，请重写antiShakingClick接口
      //设置Activity主题，请重写initTheme()方法
      //设置权限申请前置步骤，请重写initPermission(String[] permissions)方法
      //如果单个Activity需要动态使用不同的布局文件，请给BaseActivity的泛型类型
      //传递ViewBinding，并重写setBindViewClass模板方法,传递不同ViewBinding类的class
      //如果不想用默认的反射去动态使用不同的布局，请重写inflateView模板方法
      //手动传递和实现ViewBinding类的实例
      //如果有反射加载视图慢的情况，请重写inflateView方法，手动实现ViewBinding类创建
      //需要更改点击防抖时间阈值，请重写isFastClick，在超类调用传递时间
      //需要在获取权限，请重写normalPermissions和specialPermissions方法
      public class MainActivity extends BaseActivity<ActivityMainBinding> {
  
          @Nullable
          @Override
          public String[] normalPermissions() {
              //设置需要动态获取的普通权限，无需申请可传null或空数组
              //目前仅支持以下特殊权限：
              //Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
              //Settings.ACTION_MANAGE_OVERLAY_PERMISSION
              //Settings.ACTION_MANAGE_WRITE_SETTINGS
              //Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
              return super.normalPermissions();
          }
      
          @Nullable
          @Override
          public String[] specialPermissions() {
              //设置需要动态获取的特殊权限，无需申请可传null或空数组
              return super.specialPermissions();
          }
          
          @Override
          protected void initObject() {
              //初始化对象
          }
          
          @Override
          protected void initView() {
             //初始化视图
          }
          
          @Override
          protected void initViewListener() {
             //初始化监听
             getBindView().test.setOnClickListener(this);
          }
      
          @Override
          protected void initData() {
             //初始化数据
          }
          
          @Override
          protected void initDataListener() {
             //初始化数据回调
          }
          
          @Override
          public void antiShakingClick(View v) {
              super.antiShakingClick(v);
              //点击事件
          }
      
          @Override
          public boolean isFastClick(int time) {
              //传递需要的防抖时间阈值
              return super.isFastClick(time);
          }
      
          @Override
          public Class<ViewBinding> setBindViewClass() {
              //反射动态布局
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
              //手动动态布局或手动初始化布局
              int i = 0;
              if (i == 0){
                  return TestBinding.inflate(getLayoutInflater());
              }
              return Test1Binding.inflate(getLayoutInflater());
          }
      
          @Override
          public boolean isAsyncLoadView() {
              //是否异步加载视图
              return false;
          }
      
          @Override
          public void initPermission(FragmentActivity activity, LifecycleOwner owner, String[] permissions) {
              //去除超类
              //比如需要申请前的说明
              //封装后调用dealPermission(String[] permissions, PermissionCallBack callBack)方法
              //需要自己获取回调，需实现PermissionCallBack接口
              //不需要直接传null
              //任意控件事件动态申请权限，请使用PermissionUtils
              new AlertDialog.Builder(this)
                      .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              dealPermission(activity, owner, permissions, new PermissionCallBack() {
                                  @Override
                                  public void agree() {
      
                                  }
      
                                  @Override
                                  public void disagree(List<String> name) {
      
                                  }
      
                                  @Override
                                  public void ban(List<String> name) {
      
                                  }
      
                                  @Override
                                  public void fail(String msg, @Nullable Throwable e) {
      
                                  }
                              });
                          }
                      })
                      .show();
          }
      }
```

### BaseFragment

```
      //基类已实现三种懒加载模式：
      //DEFAULT：正常加载
      //ONLY_LAZY_DATA：只懒加载数据
      //LAZY_VIEW_AND_DATA：懒加载数据和页面
      //通过重写getLoadMode方法，返回不同的加载枚举
      //基类会执行不同的加载逻辑，不重写为DEFAULT
      //可通过isLazyInitSuccess方法，获取懒加载是否成功
      //基类已实现异步布局加载，如需异步加载，请重写isAsyncLoadView方法
      //并返回true
      //若异步加载需要加载弹窗，需要重写initSyncView，并返回实现AsyncLoadViewCallBack
      //默认没有异步加载弹窗
      //接口的对象，供基类调用，显示和关闭
      //创建xml后，点击编译，填入需要绑定的视图和传递数据类型
      //click监听已做快速点击处理，请重写antiShakingClick接口
      //设置Activity主题，请重写initTheme()方法
      //设置权限申请前置步骤，请重写initPermission(String[] permissions)方法
      //如果单个Fragment需要动态使用不同的布局文件，请给BaseFragment的泛型类型
      //传递ViewBinding，并重写setBindViewClass模板方法,传递不同ViewBinding类
      //如果不想用默认的反射去动态使用不同的布局，请重写inflateView模板方法
      //手动传递和实现ViewBinding类的实例
      //如果有反射加载视图慢的情况，请重写inflateView方法，手动实现ViewBinding类创建
      //需要更改点击防抖时间阈值，请重写isFastClick，在超类调用传递时间
      //需要在获取权限，请重写normalPermissions和specialPermissions方法
      public class MainFragment extends BaseFragment<FragmentMainBinding> {
  
          @Nullable
          @Override
          public String[] normalPermissions() {
              //设置需要动态获取的普通权限，无需申请可传null或空数组
              //目前仅支持以下特殊权限：
              //Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
              //Settings.ACTION_MANAGE_OVERLAY_PERMISSION
              //Settings.ACTION_MANAGE_WRITE_SETTINGS
              //Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
              return super.normalPermissions();
          }
      
          @Nullable
          @Override
          public String[] specialPermissions() {
              //设置需要动态获取的特殊权限，无需申请可传null或空数组
              return super.specialPermissions();
          }

          @Override
          protected LoadMode getLoadMode() {
              //设置加载模式
              return LoadMode.LAZY_VIEW_AND_DATA;
          }
          
          @Override
          protected void initFirst() {
              //碎片第一次创建
          }
          
          @Override
          protected void initObject() {
             //初始化对象
          }
          
          @Override
          protected void initView() {
             //初始化视图
          }
          
          @Override
          protected void initViewListener() {
             //初始化监听
             getBindView().test.setOnClickListener(this);
          }
          
          @Override
          protected void initData() {
             //初始化数据
          }
          
          @Override
          protected void initDataListener() {
             //初始化数据监听
          }
          
          @Override
          public void antiShakingClick(View v) {
              super.antiShakingClick(v);
              //点击事件
          }
      
          @Override
          public boolean isFastClick(int time) {
              //传递需要的防抖时间阈值
              return super.isFastClick(time);
          }
      
          @Override
          public ViewBinding inflateView(Object o, LayoutInflater layoutInflater) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
              //手动动态布局或手动初始化布局
              int i = 0;
              if (i == 0){
                  return TestBinding.inflate(getLayoutInflater());
              }
              return Test1Binding.inflate(getLayoutInflater());
          }
      
          @Override
          public Class<ViewBinding> setBindViewClass() {
              //动态布局
              Class<?> cls;
              if (i == 1) {
                  cls = ActivityMainBinding.class;
              } else {
                  cls = XxxBinding.class;
              }
              return (Class<ViewBinding>) cls;
          }
      
          @Override
          public boolean isAsyncLoadView() {
              //是否异步加载视图
              return false;
          }
      
          @Override
          public void initPermission(FragmentActivity activity, LifecycleOwner owner, String[] permissions) {
              //去除超类
              //比如需要申请前的说明
              //封装后调用dealPermission(String[] permissions, PermissionCallBack callBack)方法
              //需要自己获取回调，需实现PermissionCallBack接口
              //不需要直接传null
              //任意控件事件动态申请权限，请使用PermissionUtils
              new AlertDialog.Builder(this)
                      .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              dealPermission(activity, owner, permissions, new PermissionCallBack() {
                                  @Override
                                  public void agree() {
      
                                  }
      
                                  @Override
                                  public void disagree(List<String> name) {
      
                                  }
      
                                  @Override
                                  public void ban(List<String> name) {
      
                                  }
      
                                  @Override
                                  public void fail(String msg, @Nullable Throwable e) {
      
                                  }
                              });
                          }
                      })
                      .show();
          }
      }
```

### BaseDialog

```
      //创建xml后，点击编译，填入需要绑定的视图
      //支持ViewBinding
      //自身支持Lifecycle
      //支持监听其他Lifcycle
      //click监听已做快速点击处理，请重写antiShakingClick接口
      //如果单个Dialog需要动态使用不同的布局文件，请给BaseDialog的泛型类型
      //传递ViewBinding，并重写setBindViewClass模板方法,传递不同ViewBinding类
      //继承示例
      public class TestDialog extends BaseDialog<PopTestBinding> {
      
            public TestDialog(@NonNull Context context) {
                super(context);
                //需要观察的Lifecycle
                setNeedObserveLifecycle(Lifecycle lifecycle);
            }
        
            public TestDialog(@NonNull Context context, int themeResId) {
                super(context, themeResId);
                //库里自带一个全屏Dialog主题BaseFullDialog
                //如不合要求，请自定义后传入
            }
        
            @Override
            protected String setBindViewClass() {
                //动态布局
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
                //可重写后实现视图初始化或手动动态布局
                return super.inflateView();
            }
            
            @Override
            protected DialogSetting setDialogParam() {
                DialogSetting setting = new DialogSetting();
                //按照顺序传入指定的X/Y/宽/高数值
                setting.setDialogParams(int[] dialogParams);
                //传入Gravity的位置
                setting.setDialogPosition(int dialogPosition)
                //是否清除边框
                setting.setClearPadding(boolean clearPadding)
                //是否允许外部取消
                setting.setCancelOutside(boolean cancelOutside)
                //是否允许拖动
                setting.setDrag(boolean drag)
                //是否拥有阴影
                setting.setHasShadow(boolean hasShadow)
                //设置Dialog的窗体标识
                setting.setSetFlag(int setFlag)
                return setting;
            }
        
            @Override
            protected void initObject() {
               //初始化对象
            }
            
            @Override
            protected void initView() {
               //初始化视图
            }
            
            @Override
            protected void initViewListener() {
               //初始化视图监听
            }
        
            @Override
            protected void initData() {
               //初始化数据
            }
        
            @Override
            protected void initDataListener() {
               //初始化数据监听
            }
            
            @Override
            public boolean isFastClick(int time) {
                //传递需要的防抖时间阈值
                return PublicEvent.super.isFastClick(time);
            }
            
            @Override
            public void antiShakingClick(View v) {
                //点击处理
            }
            
            @Override
            public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
                //监听其他Lifecycle组件的声明周期标识符
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
      //快速实现一个支持Lifecycle和支持监听其他Lifecycle的类
      //适合无继承关系的任意类使用
      //有继承关系请使用BaseQuickLifecycle
      //直接继承BaseQuickLifecycleImpl
      //在此类的业务逻辑中的合适位置发送声明周期事件
      //自动监听/移除监听其他生命周期组件
      public class Test extends BaseQuickLifecycleImpl{
      
          public Test() {
              //此构造只支持自己实现Lifecycle，无法监听其他声明周期组件
              //默认构造调用时发送ON_CREATE、ON_START、ON_RESUME事件
              //需要自实现顺序和位请移除超类后自发送
              super();
          }
          
          public Test(Lifecycle observeLifecycle) {
              //监听其他拥有生命周期接口组件，且实现支持Lifecycle
              //默认构造调用的时候发送ON_CREATE、ON_START、ON_RESUME事件，需要自实现请移除超类后自发送
              super(observeLifecycle);
          }
          
           @NonNull
           @Override
           protected Lifecycle.Event setStopEvent() {
                return null;
           }
          
          @Override
          protected void onTerminate(Lifecycle.Event event) {
              //默认调用时发送ON_PAUSE、ON_STOP、ON_DESTROY事件，以达成完整生命周期顺序
              //需要自实现顺序和位置请移除超类后自发送
              //如果rxjava监听生命周期无法正常中止订阅，请检查生命周期状态是否分发完整
              //可在此中止当前Test类中正在进行的任务的其他相关逻辑
              super.onTerminate(event);
          }
      }
```