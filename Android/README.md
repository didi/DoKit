# Android接入指南

## DoKit Android 最新版本
**由于jcenter事件的影响，我们需要将DoKit For Android迁移到mavenCentral()，但是需要更改groupId.所以大家要注意一下，具体的更新信息如下：**

**lastversion:3.5.0；kotlin编译插件为1.4.32 ；支持Gradle 6.8及以上**
**lastversion:3.5.0.1； kotlin编译插件为1.3.72； 支持Gradle 6.8及以下**

|DoKit|最新版本|描述|
|-|-|-|
|3.3.5及以后的Androidx|debugImplementation "io.github.didi.dokit:${aarName}: ${lastversion}"|(1)dokitx的library和plugin的groupId及版本号需要保持一致;(2)AGP最低版本要求3.3.0+|
|3.3.5及以前的Androidx版本|debugImplementation "com.didichuxing.doraemonkit:${aarName}:3.3.5"|（1）dokitx的library和plugin的groupId及版本号需要保持一致； (2)AGP最低版本要求3.3.0+|
|支持android support|debugImplementation "com.didichuxing.doraemonkit:${aarName}:3.3.5"|support放弃更新，请大家尽快升级和适配Androidx|

**${aarName}需要改为指定的名称，参考如下:**
```
//核心模块

debugImplementation "io.github.didi.dokit:dokitx:${lastversion}"

//文件同步模块

debugImplementation "io.github.didi.dokit:dokitx-ft:${lastversion}"

//一机多控模块

debugImplementation "io.github.didi.dokit:dokitx-mc:${lastversion}"

//weex模块

debugImplementation "io.github.didi.dokit:dokitx-weex:${lastversion}"

//no-op 模块

releaseImplementation "io.github.didi.dokit:dokitx-no-op:${lastversion}"
```

**debugImplementation 需要根据自己的构建改成对应的productFlavor**


**下面所有的例子均用dokitx举例。要使用support版本请将dokitx改为dokit即可。
v3.3.5以后的版本需要添加mavenCentral()仓库**


## 接入步骤
#### 1. Gradle 依赖

```groovy
dependencies {
    debugImplementation 'io.github.didi.dokit:dokitx:${lastversion}'
    releaseImplementation 'io.github.didi.dokit:dokitx-no-op:${lastversion}'
}
```

**滴滴内部业务:**

滴滴内部业务线接入请添加模块


```
//数据mock内部网络库支持
debugImplementation 'io.github.didi.dokit:dokitx-rpc:${lastversion}'
//一机多控内部网络库支持
debugImplementation 'io.github.didi.dokit:dokitx-rpc-mc:${lastversion}'
```



最新版本参见[这里](https://github.com/didi/DoraemonKit/blob/master/Doc/android-ReleaseNotes.md)。


#### 2. 初始化

在 App 启动的时候进行初始化。

```kotlin
overide fun onCreate() { 
   DoKit.Builder(this)
            .productId("需要使用平台功能的话，需要到dokit.cn平台申请id")
            .build()
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
        classpath 'io.github.didi.dokit:dokitx-plugin:${lastversion}'
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
        //调用栈模式配置 对应gradle.properties中DOKIT_METHOD_STRATEGY=0
        stackMethod {
            //默认值为 5ms 小于该值的函数在调用栈中不显示
            thresholdTime 10
            //调用栈函数入口 千万不要用我默认的配置 如果有特殊需求修改成项目中自己的入口 假如不需要可以去掉该字段
            enterMethods = ["com.didichuxing.doraemondemo.MainDebugActivity.test1"]
            //黑名单 粒度最小到类 暂不支持到方法  千万不要用我默认的配置 如果有特殊需求修改成项目中自己的入口 假如不需要可以去掉该字段
            methodBlacklist = ["com.facebook.drawee.backends.pipeline.Fresco"]
        }
        //普通模式配置 对应gradle.properties中DOKIT_METHOD_STRATEGY=1
        normalMethod {
            //默认值为 500ms 小于该值的函数在运行时不会在控制台中被打印
            thresholdTime 500
            //需要针对函数插装的包名 千万不要用我默认的配置 如果有特殊需求修改成项目中自己的项目包名 假如不需要可以去掉该字段
            packageNames = ["com.didichuxing.doraemondemo"]
            //不需要针对函数插装的包名&类名 千万不要用我默认的配置 如果有特殊需求修改成项目中自己的项目包名 假如不需要可以去掉该字段
            methodBlacklist = ["com.didichuxing.doraemondemo.dokit"]
        }
    }
}
```

其中**strategy**和**methodSwitch**配置项已经弃用。新的配置开关位于项目根目录下的**gradle.properties**中。

具体的配置如下所示：
```
// dokit全局配置
// 插件开关
DOKIT_PLUGIN_SWITCH=true
// DOKIT读取三方库会和booster冲突 如果你的项目中也集成了booster 建议将开关改成false
DOKIT_THIRD_LIB_SWITCH=true
// 插件日志
DOKIT_LOG_SWITCH=true
// 自定义Webview的全限定名 主要是作用于h5 js抓包和数据mock
DOKIT_WEBVIEW_CLASS_NAME=com/didichuxing/doraemonkit/widget/webview/MyWebView
// dokit 慢函数开关
DOKIT_METHOD_SWITCH=true
// dokit 函数调用栈层级
DOKIT_METHOD_STACK_LEVEL=4
// 0:默认模式 打印函数调用栈 需添加指定入口  默认为application onCreate 和attachBaseContext
// 1:普通模式 运行时打印某个函数的耗时 全局业务代码函数插入
DOKIT_METHOD_STRATEGY=0
```

**理由**:
为了减少项目的编译时间，所以慢函数的默认开关为false。再加上plugin的transform注册必须早于project.afterEvaluate。所以无法通过原先的配置项拿到配置信息，只能通过在全局的gradle.properties中的配置可以拿到。

**tips：**
当修改完DoKit插件的相关配置以后一定要clean一下重新编译才能生效。这是AS的缓存增量编译导致的，暂时没有其他好的解决方案。



#### 4. 自定义功能组件（可选）

自定义组件需要实现 IKit 接口，该接口对应哆啦A梦功能面板中的组件。

以代驾乘客端为例，实现环境切换组件如下。

```kotlin
class DemoKit : AbstractKit() {
    override val category: Int
        get() = Category.BIZ
    override val name: Int
        get() = R.string.dk_kit_demo
    override val icon: Int
        get() = R.mipmap.dk_sys_info

    override fun onClickWithReturn(activity: Activity): Boolean {
        SimpleDoKitStarter.startFloating(DemoDokitView::class.java)
        return true
    }

    override fun onAppInit(context: Context?) {
    }

}
```

在初始化的时候注册自定义组件。

```kotlin
override fun onCreate() {
    DoKit.Builder(this)
            .productId("需要使用平台功能的话，需要到dokit.cn平台申请id")
	    .customKits(mapKits)
            .build()
}
```

**DoKit入口api**
```kotlin
public class DoKit private constructor() {
    companion object {
        


        /**
         * 主icon是否处于显示状态
         */
        @JvmStatic
        val isMainIconShow: Boolean
            get() = false


        /**
         * 显示主icon
         */
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

        /**
         * 隐藏主icon
         */
        @JvmStatic
        fun hide() {
        }

        /**
         * 启动悬浮窗
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        @JvmOverloads
        fun launchFloating(
            targetClass: Class<out AbsDokitView>,
            mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE,
            bundle: Bundle? = null
        ) {
        }


        /**
         * 启动悬浮窗
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        @JvmOverloads
        fun launchFloating(
            targetClass: KClass<out AbsDokitView>,
            mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE,
            bundle: Bundle? = null
        ) {
        }

        /**
         * 移除悬浮窗
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        fun removeFloating(targetClass: Class<out AbsDokitView>) {
        }

        /**
         * 移除悬浮窗
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        fun removeFloating(targetClass: KClass<out AbsDokitView>) {
        }

        /**
         * 移除悬浮窗
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        fun removeFloating(dokitView: AbsDokitView) {
        }


        /**
         * 启动全屏页面
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        @JvmOverloads
        fun launchFullScreen(
            targetClass: Class<out BaseFragment>,
            context: Context? = null,
            bundle: Bundle? = null,
            isSystemFragment: Boolean = false
        ) {
        }

        /**
         * 启动全屏页面
         * @JvmStatic:允许使用java的静态方法的方式调用
         * @JvmOverloads :在有默认参数值的方法中使用@JvmOverloads注解，则Kotlin就会暴露多个重载方法。
         */
        @JvmStatic
        @JvmOverloads
        fun launchFullScreen(
            targetClass: KClass<out BaseFragment>,
            context: Context? = null,
            bundle: Bundle? = null,
            isSystemFragment: Boolean = false
        ) {
        }


        @JvmStatic
        fun <T : AbsDokitView> getDoKitView(
            activity: Activity?,
            clazz: Class<out T>
        ): T? {
            return null
        }

        @JvmStatic
        fun <T : AbsDokitView> getDoKitView(
            activity: Activity?,
            clazz: KClass<out T>
        ): T? {
            return null
        }

        /**
         * 发送自定义一机多控事件
         */
        @JvmStatic
        fun sendCustomEvent(
            eventType: String,
            view: View? = null,
            param: Map<String, String>? = null
        ) {
        }
        /**
         * 获取一机多控类型
         */
        @JvmStatic
        fun mcMode(): WSMode {
            return WSMode.UNKNOW
        }
    }


    class Builder(private val app: Application) {
        private var productId: String = ""
        private var mapKits: LinkedHashMap<String, List<AbstractKit>> = linkedMapOf()
        private var listKits: List<AbstractKit> = arrayListOf()

        init {
        }

        fun productId(productId: String): Builder {
            return this
        }

        /**
         * mapKits & listKits 二选一
         */
        fun customKits(mapKits: LinkedHashMap<String, List<AbstractKit>>): Builder {
            return this
        }

        /**
         * mapKits & listKits 二选一
         */
        fun customKits(listKits: List<AbstractKit>): Builder {
            return this
        }

        /**
         * H5任意门全局回调
         */
        fun webDoorCallback(callback: WebDoorManager.WebDoorCallback): Builder {
            return this
        }

        /**
         * 禁用app信息上传开关，该上传信息只为做DoKit接入量的统计，如果用户需要保护app隐私，可调用该方法进行禁用
         */
        fun disableUpload(): Builder {
            return this
        }

        fun debug(debug: Boolean): Builder {
            return this
        }

        /**
         * 是否显示主入口icon
         */
        fun alwaysShowMainIcon(alwaysShow: Boolean): Builder {
            return this
        }

        /**
         * 设置加密数据库密码
         */
        fun databasePass(map: Map<String, String>): Builder {
            return this
        }

        /**
         * 设置文件管理助手http端口号
         */
        fun fileManagerHttpPort(port: Int): Builder {
            return this
        }

        /**
         * 一机多控端口号
         */
        fun mcWSPort(port: Int): Builder {
            return this
        }

        /**
         * 一机多控自定义拦截器
         */
        fun mcClientProcess(interceptor: McClientProcessor): Builder {
            return this
        }

        /**
         *设置dokit的性能监控全局回调
         */
        fun callBack(callback: DoKitCallBack): Builder {
            return this
        }


        /**
         * 设置扩展网络拦截器的代理对象
         */
        fun netExtInterceptor(extInterceptorProxy: DokitExtInterceptor.DokitExtInterceptorProxy): Builder {
            return this
        }


        fun build() {
        }
    }
}
```
开启插件调试
./gradlew :app:assembleDebug -Dorg.gradle.daemon=false -Dorg.gradle.debug=true


#### 5. FAQ

参考[这里](http://xingyun.xiaojukeji.com/docs/dokit/#/SDKProblems)
