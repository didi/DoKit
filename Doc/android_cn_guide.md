## 接入方式

#### 1. Gradle依赖

```
dependencies {
	...
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit:1.1.2'
    releaseImplementation 'com.didichuxing.doraemonkit:doraemonkit-no-op:1.1.2'
    ...
}
```

最新版本参见[这里](android-ReleaseNotes.md)。



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



#### 3. 流量监控功能（可选）

在项目的build.gradle中添加classpath。

```
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        ...
        classpath 'com.didichuxing.doraemonkit:compiler:1.0.1'
        ...
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
```

在app的build.gradle中添加plugin。

```
...
apply plugin: 'com.doraemon.compiler.plugin'
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

> 先看下是否打开了app的悬浮窗权限，另外看看是否错误引用了no-op版本

**2. com.didichuxing.doraemonkit:kit和com.didichuxing.doraemonkit:kit-no-op有什么区别？**

> no-op版本仅提供了DoraemonKit类的空实现，因为DoraemonKit是不推荐集成到线上版本使用的，可通过gradle配置动态切换正常版本和no-op版本。

**3. 没有H5任意门入口**

> H5任意门需要在调用DoraemonKit.setWebDoorCallback实现回调，打开自己App内的Webview，这是因为一般app都有自己的JsBridge逻辑，使用通用的Webview打开Url并没什么意义。如果接入时未设置回调，则在菜单页不会显示H5任意门

**4. 流量监控无数据**

> 目前流量监测功能只支持OkHttp3和HttpUrlConnection的自动注入，其他网络库暂不支持。
> 其他网络库可以使用NetworkPrinterHelper类进行请求数据的手动添加，使用方式参照demo。

**5. 性能监控中，CPU、RAM无数据**

> 旧版本的CPU监控是通过读取/proc实现的，在8.0系统上这个方法读取不到数据。新版本已修改了实现方式，可以正常读取。

**6. 帧率、CPU、RAM数据不准确**

> 由于读取cpu、内存数据本身需要消耗cpu和内存，加上心跳图的绘制也是耗性能的，所以开启这几项功能后，数据比app实际使用值有一定提升。

**7. com.doraemon.compiler.plugin是必须的吗**

> 这个插件目前的用处是在编译阶段在okhttp和httpurlconnection的调用处进行插桩，用以收集网络请求数据从而实现流量监控功能，如果不需要流量监控功能或者使用的是非okhttp和httpurlconnection网络库，则可以不引用这个插件。另外，这个插件会在用户自定义的Application内插入DoraemonKit.install方法，实现sdk的自动注册，引用了这个插件就不需要手动注册sdk了。
> 因为这个插件会涉及到字节码的修改，同时会插入一些代码到网络请求中，对性能和稳定性有轻微影响，所以极度不推荐线上版本中使用。在线上版本中，务必去除该插件的引用。

**8. 沙盒游览功能能否打开数据库、sp文件**

> 目前沙盒功能是以文本形式打开数据库文件和sp文件，sp文件是xml格式，可以正常浏览，但是数据库文件打开会有乱码，后续会提供类似Root Explorer的数据库查看功能。