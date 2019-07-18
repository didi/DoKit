## 接入方式

#### 1. Gradle依赖

```
dependencies {
	...
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit:1.1.8'
    releaseImplementation 'com.didichuxing.doraemonkit:doraemonkit-no-op:1.1.8'
    ...
}
```

最新版本参见[这里](android-ReleaseNotes.md)。

DoraemonKit目前已支持Weex工具，包括

* Console日志查看
* Storage缓存查看
* 容器信息
* DevTool

如果有需要支持Weex的需求可以直接添加下面依赖

```
dependencies {
	...
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit-weex:0.0.1'
    releaseImplementation 'com.didichuxing.doraemonkit:doraemonkit-weex-no-op:0.0.1'
    ...
}
```


#### 2. 初始化

在App启动的时候进行初始化。

```
@Override
public void onCreate() {
	...
    DoraemonKit.install(application)
     
    // H5任意门功能需要，非必须
    DoraemonKit.setWebDoorCallback(new WebDoorManager.WebDoorCallback() {
    @Override
    public void overrideUrlLoading(Context context, String s) {
        // 使用自己的H5容器打开这个链接
    }
    ...
} 
```

如果已接入了Weex工具（暂不支持自定义功能组件），使用下面方式初始化

```
@Override
public void onCreate() {
	...
    DKWeexInstance.install(application)
     
    // H5任意门功能需要，非必须
    DoraemonKit.setWebDoorCallback(new WebDoorManager.WebDoorCallback() {
    @Override
    public void overrideUrlLoading(Context context, String s) {
        // 使用自己的H5容器打开这个链接
    }
    ...
} 
```


#### 3. 流量监控功能（可选）

在项目的build.gradle中添加classpath。

```
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        ...
        classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.4'
        ...
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
```

在app的build.gradle中添加plugin和引用。新版本中已经将插件用到的注解类提取到单独的aar中，用以解决
和其他AspectJ插件冲突问题。如果项目中引用了其他AspectJ插件，请勿引用本插件，改为手动注册。

```
...
apply plugin: 'android-aspectjx'
dependencies {
	...
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit-aop:1.0.0'
    ...
}
```

注：
使用插件有两个目的：1是实现网络请求的自动监控和模拟弱网功能，不需要手动写其他代码。2是可以实现三方jar包内的请求的hook。
但使用插件会稍微影响到编译速度。如果不需要这个功能，可以通过手动添加DoraemonInterceptor的方式进行OkHttp的监控,如下：

```
OkHttpClient client = new OkHttpClient().newBuilder()
                //用于模拟弱网的拦截器
                .addNetworkInterceptor(new DoraemonWeakNetworkInterceptor())
                //网络请求监控的拦截器
                .addInterceptor(new DoraemonInterceptor()).build();
```
#### 4. 自定义功能组件（可选）

自定义组件需要实现IKit接口，该接口对应哆啦A梦功能面板中的组件。

以黑马乘客端为例，实现环境切换组件如下。

```
public class EnvSwitchKit implements IKit {
    @Override
    public int getCategory() {
        return Category.BIZ;
    }
 
    @Override
    public int getName() {
        return R.string.bh_env_switch;
    }
 
    @Override
    public int getIcon() {
        return R.drawable.bh_roadbit;
    }
 
    @Override
    public void onClick(Context context) {
        DebugService service = ServiceManager.getInstance().getService(context, DebugService.class);
        PageManager.getInstance().startFragment(service.getContainer(), EnvSwitchFragment.class);
    }
 
    @Override
    public void onAppInit(Context context) {
    
    }
}
```

在初始化的时候注册自定义组件。

```
@Override
public void onCreate() {
    kits.add(new EnvSwitchKit());
    DoraemonKit.install(application, kits);
    ...
}
```



#### 5. FAQ

**1. 为什么接入后看不到悬浮窗入口？**

> 先确认是否打开应用的悬浮窗权限，同时确认是否错误引用no-op版本。

**2. com.didichuxing.doraemonkit:kit和com.didichuxing.doraemonkit:kit-no-op有什么区别？**

> no-op版本提供空实现，DoraemonKit不推荐集成到线上版本使用的，可通过gradle配置动态切换正常版本和no-op版本。

**3. 流量监控无数据**

> 目前流量监测功能只支持OkHttp3和HttpUrlConnection的自动注入，其他网络库暂不支持。
> 其他网络库可以使用NetworkPrinterHelper类进行请求数据的手动注入，使用参考demo。

**4. 性能监控中，CPU、RAM无数据**

> 旧版本SDK的CPU监控实现在8.0系统读取不到数据。新版本已修改实现方式，可以正常读取。

**5. 帧率、CPU、RAM数据不准确**

> 由于读取cpu、内存数据本身需要消耗cpu和内存，加上心跳图绘制需要消耗性能，所以开启这几项功能后，数据比app实际使用值有升高。

**6. com.hujiang.aspectjx:gradle-android-plugin-aspectjx是否必须?**

> 这个插件的用处是在编译阶段在okhttp和httpurlconnection的调用处进行插桩，用来收集网络请求数据从而实现流量监控功能，如果不需要流量监控功能或者使用的是非okhttp和httpurlconnection网络库，可以不引用这个插件。
> 因为这个插件会涉及到字节码的修改，同时会插入一些代码到网络请求中，对性能和稳定性有影响，所以非常不推荐在线上版本中使用。在线上版本中，务必去除该插件的引用。

**7. 沙盒游览功能能否打开数据库、sp文件?**

> 目前已经支持