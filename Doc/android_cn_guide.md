## 接入方式

**Tip:**

为了更好的支持android官方androidx和support，dokit从3.3.1版本开始正式对sdk名字进行更新。
具体如下：

**androidx===>com.didichuxing.doraemonkit:dokitx:3.3.4**

**support===>com.didichuxing.doraemonkit:dokit:3.3.4**

**下面所有的日志均用dokitx举例。要使用support版本请将dokitx改为dokit即可。**

|DoKit|最新版本|描述|
|-|-|-|
|支持Androidx|3.3.4|从v3.3.1版本开始更名为dokitx|
|支持android support|3.3.4|3.0.8.0版本对应3.2.0的功能，后期support将会不定期更新，主要还是看社区的反馈，请大家尽快升级和适配Androidx|



#### 1. Gradle 依赖

```groovy
dependencies {
    …
    debugImplementation 'com.didichuxing.doraemonkit:dokitx:3.3.4'
    releaseImplementation 'com.didichuxing.doraemonkit:dokitx-no-op:3.3.4'
    …
}
```

**滴滴内部业务:**

滴滴内部业务线接入请将
```
debugImplementation 'com.didichuxing.doraemonkit:dokitx:3.3.4'
```

替换为

```
debugImplementation 'com.didichuxing.doraemonkit:dokitx-rpc:3.3.4'
```

**注意:** 
 假如你无法通过 jcenter 下载到依赖库并报了以下的错误 

```
ERROR: Failed to resolve: com.didichuxing.doraemonkit:dokitx:3.3.4
```

建议你可以尝试挂载VPN或通过命令行重试(以Mac系统为例 项目根目录下)

```
./gradlew clean assembleDebug
```


最新版本参见[这里](https://github.com/didi/DoraemonKit/blob/master/Doc/android-ReleaseNotes.md)。

**注意:**

>安卓版本DoKit从3.1.0版本开始全面拥抱Androidx,假如你的项目还没有升级到androidx你可以选择依赖3.0.2版本
安卓版DoKit从3.0.2版本开始将逐渐放弃对Android Support版本的支持，请大家全面拥抱androidx吧

DoraemonKit目前已支持Weex工具，包括

* Console 日志查看
* Storage 缓存查看
* 容器信息
* DevTool

如果有需要支持Weex的需求可以直接添加下面依赖

```groovy
dependencies {
    …
    debugImplementation 'com.didichuxing.doraemonkit:dokitx-weex:3.3.4'
    …
}
```


`LeakCanary` 已经被废弃 大家可以自行集成leakCanary的2.0+版本。


#### 2. 初始化

在 App 启动的时候进行初始化。

```Java
@Override
public void onCreate() {
   
    DoraemonKit.install(application,null,"pId");
   
} 
```
**滴滴内部业务**

```Java
@Override
public void onCreate() {
   
    DoraemonKitRpc.install(application,null,"pId")
  
} 
```


#### 3. 流量监控以及其他AOP功能（可选）
AOP包括以下几个功能:
1)百度、腾讯、高德地图的经纬度模拟
2)UrlConnection、Okhttp 抓包以及后续的接口hook功能
3)App 启动耗时统计
4)慢函数
5)大图

在项目的 `build.gradle` 中添加 classpath。

```groovy
buildscript {
    dependencies {
        …
        classpath 'com.didichuxing.doraemonkit:dokitx-plugin:3.3.4'
        …
    }
}
```

在 app 的 `build.gradle` 中添加 plugin。

```groovy
apply plugin: 'com.didi.dokit'

```

**插件配置选项:**
添加到app module 的build.gradle文件下 与android {}处于同一级
```groovy
dokitExt {
    //通用设置
    comm {
        //地图经纬度开关
        gpsSwitch true
        //网络开关
        networkSwitch true
        //大图开关
        bigImgSwitch true
        //webView js 抓包
        webViewSwitch true
    }

    slowMethod {
        //0:默认模式 打印函数调用栈 需添加指定入口  默认为application onCreate 和attachBaseContext
        //1:普通模式 运行时打印某个函数的耗时 全局业务代码函数插入
        strategy 0
        //函数功能开关
        methodSwitch true

        //调用栈模式配置
        stackMethod {
            //默认值为 5ms 小于该值的函数在调用栈中不显示
            thresholdTime 10
            //调用栈函数入口
            enterMethods = ["com.didichuxing.doraemondemo.MainDebugActivity.test1"]
            //黑名单 粒度最小到类 暂不支持到方法
            methodBlacklist = ["com.facebook.drawee.backends.pipeline.Fresco"]
        }
        //普通模式配置
        normalMethod {
            //默认值为 500ms 小于该值的函数在运行时不会在控制台中被打印
            thresholdTime 500
            //需要针对函数插装的包名
            packageNames = ["com.didichuxing.doraemondemo"]
            //不需要针对函数插装的包名&类名
            methodBlacklist = ["com.didichuxing.doraemondemo.dokit"]
        }
    }
}
```
项目根目录下的gradle.properties文件进行以下配置
```
#dokit全局配置
# 插件开关
DOKIT_PLUGIN_SWITCH=true
# 插件日志
# 自定义Webview的全限定名
DOKIT_WEBVIEW_CLASS_NAME=com/didichuxing/doraemonkit/widget/webview/MyWebView
DOKIT_LOG_SWITCH=true
#dokit 慢函数开关
DOKIT_METHOD_SWITCH=true
#dokit 函数调用栈层级
DOKIT_METHOD_STACK_LEVEL=4
#0:默认模式 打印函数调用栈 需添加指定入口  默认为application onCreate 和attachBaseContext
#1:普通模式 运行时打印某个函数的耗时 全局业务代码函数插入
DOKIT_METHOD_STRATEGY=0
```

#### 4. 自定义功能组件（可选）

自定义组件需要实现 IKit 接口，该接口对应哆啦A梦功能面板中的组件。

以代驾乘客端为例，实现环境切换组件如下。

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
    DoraemonKit.install(application, kits);
    …
}
```

**DoraemonKit入口api**
```
object DoraemonKit {
    @JvmStatic
    fun install(app: Application) {
    }

    @JvmStatic
    fun install(app: Application, productId: String) {
    }

    @JvmStatic
    fun install(app: Application, mapKits: LinkedHashMap<String, MutableList<AbstractKit>>) {
    }

    @JvmStatic
    fun install(app: Application, mapKits: LinkedHashMap<String, MutableList<AbstractKit>>, productId: String) {
    }

    @JvmStatic
    fun install(app: Application, listKits: MutableList<AbstractKit>) {
    }

    @JvmStatic
    fun install(app: Application, listKits: MutableList<AbstractKit>, productId: String) {
    }

    /**
     * @param app
     * @param mapKits  自定义kits  根据用户传进来的分组 建议优先选择mapKits 两者都传的话会选择mapKits
     * @param listKits  自定义kits 兼容原先老的api
     * @param productId Dokit平台端申请的productId
     */
    @JvmStatic
    private fun install(app: Application, mapKits: LinkedHashMap<String, MutableList<AbstractKit>>? = linkedMapOf(), listKits: MutableList<AbstractKit>? = mutableListOf(), productId: String? = "") {

    }

    @JvmStatic
    fun setWebDoorCallback(callback: WebDoorManager.WebDoorCallback?) {
    }

    @JvmStatic
    fun show() {
    }

    /**
     * 直接显示工具面板页面
     */
    @JvmStatic
    fun showToolPanel() {
    }

    /**
     * 直接隐藏工具面板
     */
    @JvmStatic
    fun hideToolPanel() {
    }

    @JvmStatic
    fun hide() {
    }

    /**
     * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
     */
    @JvmStatic
    fun disableUpload() {
    }

    @JvmStatic
    val isShow: Boolean
        get() = false

    @JvmStatic
    fun setDebug(debug: Boolean) {
    }

    /**
     * 是否显示主入口icon
     */
    @JvmStatic
    fun setAwaysShowMainIcon(awaysShow: Boolean) {
    }

    /**
     * 设置加密数据库密码
     */
    @JvmStatic
    fun setDatabasePass(map: Map<String, String>) {
    }

    /**
     * 设置文件管理助手http端口号
     */
    @JvmStatic
    fun setFileManagerHttpPort(port: Int) {
    }
}

```


#### 5. FAQ

参考[这里](http://xingyun.xiaojukeji.com/docs/dokit#/SDKProblems)