## 接入方式

#### 1. Gradle 依赖

```groovy
dependencies {
    …
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit:3.0_beta2'
    releaseImplementation 'com.didichuxing.doraemonkit:doraemonkit-no-op:3.0_beta2'
    …
}
```
注意:  
 假如你无法通过 jcenter 下载到依赖库并报了以下的错误 

```
ERROR: Failed to resolve: com.didichuxing.doraemonkit:doraemonkit:3.0_beta2
```

建议你可以尝试挂载VPN或通过命令行重试(以Mac系统为例 项目根目录下)

```
./gradlew clean assembleDebug
```


最新版本参见[这里](android-ReleaseNotes.md)。

DoraemonKit目前已支持Weex工具，包括

* Console 日志查看
* Storage 缓存查看
* 容器信息
* DevTool

如果有需要支持Weex的需求可以直接添加下面依赖

```groovy
dependencies {
    …
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit-weex:3.0_beta2'
    releaseImplementation 'com.didichuxing.doraemonkit:doraemonkit-weex-no-op:3.0_beta2'
    …
}
```

如果有需要集成 `LeakCanary` 的需求可以直接添加下面依赖

```groovy
dependencies {
    …
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit-leakcanary:3.0_beta2'
    …
}
```
`LeakCanary` 已经在 doraemonkit 中动态集成，不需要自己再进行手动集成，只需要添加上面的依赖即可。


#### 2. 初始化

在 App 启动的时候进行初始化。

```Java
@Override
public void onCreate() {
    …
    DoraemonKit.install(application)
     
    // H5任意门功能需要，非必须
    DoraemonKit.setWebDoorCallback(new WebDoorManager.WebDoorCallback() {
    @Override
    public void overrideUrlLoading(Context context, String s) {
        // 使用自己的H5容器打开这个链接
    }
    …
} 
```


#### 3. 流量监控以及其他AOP功能（可选）
AOP包括以下几个功能:
1)百度、腾讯、高德地图的经纬度模拟
2)UrlConnection、Okhttp 抓包以及后续的接口hook功能
3)App 启动耗时统计

在项目的 `build.gradle` 中添加 classpath。

```groovy
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        …
        classpath 'com.didichuxing.doraemonkit:doraemonkit-plugin:3.0_beta2'
        …
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
```

在 app 的 `build.gradle` 中添加 plugin。

```groovy
apply plugin: 'com.didi.dokit'

```

**注意:
升级方案：
dokit的aop方案已经全面升级为ASM方式,性能和兼容性更好。原先的aspectj方案已经废弃不用。大家在升级过程中需要去掉aspectj的插件引入（包括classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.8'和doraemonkit-aop）。
Android Studio支持:
插件只在Android Studio 3.0及以上的IDE中进行测试，如果有IDE报错的建议升级为3.0及以上。**

注：
使用插件有两个目的：  
1. 是实现网络请求的自动监控和模拟弱网功能，不需要手动写其他代码。  
2. 是可以实现三方 jar 包内的请求的 hook。但使用插件会稍微影响到编译速度。  
   如果不需要这个功能，可以通过手动添加DoraemonInterceptor的方式进行OkHttp的监控,如下：

    ```Java
    OkHttpClient client = new OkHttpClient().newBuilder()
            //用于模拟弱网的拦截器
            .addNetworkInterceptor(new DoraemonWeakNetworkInterceptor())
            //网络请求监控的拦截器
            .addInterceptor(new DoraemonInterceptor()).build();
    ```


#### 4. 自定义功能组件（可选）

自定义组件需要实现 IKit 接口，该接口对应哆啦A梦功能面板中的组件。

以黑马乘客端为例，实现环境切换组件如下。

```Java
public class EnvSwitchKit extends AbstractKit {
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

```Java
@Override
public void onCreate() {
    kits.add(new EnvSwitchKit());
    DoraemonKit.install(application, kits,"pId");
    …
}
```
**注意:
社区中有人反馈希望可以app 启动时不显示浮标icon的api，现在可以通过以下api来操作:**

```Java
@Override
public void onCreate() {
    kits.add(new EnvSwitchKit());
    DoraemonKit.install(application, kits,"pId");
    //false:不显示入口icon 默认为true
    DoraemonKit.setAwaysShowMainIcon(false);
    …
}
```
**直接调起工具面板**

```Java
DoraemonKit.showToolPanel();
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