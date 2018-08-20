## DoraemonKit

<div  align="center">    
 <img src="https://javer.oss-cn-shanghai.aliyuncs.com/doraemon/openSource/doraemon.jpeg" width = "150" height = "100" alt="图片名称" align=left />
</div>

<br/>
<br/>
<br/>
<br/>
<br/>
<br/>

一款适用于iOS App的线下测试工具集合。

## DoraemonKit具有哪些功能

1. 【App信息查看】快速查看手机信息，App信息，权限信息的渠道，避免去手机设置查找或者查看项目源代码的麻烦。
2. 【沙盒浏览】App内部文件浏览的功能，并且能童工airDrop上传到PC中，进行更加细致的操作。
3. 【MockGPS】App能定位到全国各地，支持地图地位和手动输入经纬度。
4. 【H5任意门】开发测试同学再也不需要找不到H5入口，无法调式而烦恼。
5. 【日志显示】每一条日志信息，都在在App的界面中显示出来，再也不需要导出日志这么麻烦。
6. 【帧率监控】App 帧率信息提供波形图查看功能，让帧率监控的趋势更加明显。
7. 【CPU监控】App CPU使用率信息提供波形图查看功能，让CPU监控的趋势更加形象。
8. 【内存监控】App 内存使用量信息提供波形图查看功能，让内存监控的趋势更加鲜明。
9. 【流量监控】拦截App内部流量信息，提供波形图展示、流量概要展示、流量列表展示、流量筛选、流量详情对流量信息统一拦截，成为我们app中自带的“Charles”。
10. 【卡顿监控】实时捕捉用户卡顿操作，并且可以把卡顿调用堆栈记录下来。
11. 【自定义监控】可以选择你要监控的选项，包括FPS、CPU、内存、流量。监控完毕之后，把数据保存到本地，你提议导出来做更加细致的分析。
12. 【颜色吸管】方便设计师UI捉虫的时候，查看每一个组件的颜色值是否设置正确。
13. 【组件检查】可以抓取任意一个UI控件，查看它们的详细信息，包括控件名称、控件位置、背景色、字体颜色、字体大小。
14. 【对齐标尺】参考android系统自带测试工具，能够实时捕获屏幕坐标，并且可以查看组件是否对齐。

## 效果演示
<div  align="center">    
 <img src="https://javer.oss-cn-shanghai.aliyuncs.com/doraemon/openSource/doraemonKit.jpeg" width = "300" height = "500" alt="图片名称" align=center />
</div>

上面两行是业务线自定义的工具，接入方可以自定义。除此之外都是内置工具集合。


## DoraemonKit如何使用

### 3.1： cocoapods依赖
包含两个subspec，一个是包含“日志显示”的“WithLogger” subspec

```
pod 'DoraemonKit',:subspecs => ['WithLogger'], :git => "https://github.com/didichuxing/DoraemonKit", :tag => '1.0.0'
```

一个是不包含“日志显示”的“Core” subspec

```
pod 'DoraemonKit',:subspecs => ['Core'], :git => "https://github.com/didichuxing/DoraemonKit", :tag => '1.0.0'
```

默认引入“Core” subspec。

tip：为什么要分区subspec呢？
因为日志显示模块是基于CocoaLumberjack这个三方库，如果你的项目日志不是基于CocoaLumberjack，那你就没有必要引入DoraemonKit中日志显示模块了。

### 3.2： 使用DoraemonKit内置工具集的接入方式
在App启动的时候添加一下代码

```
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    #ifdef DEBUG
        [[DoraemonManager shareInstance] addH5DoorBlock:^(NSString *h5Url) {
              //使用自己的H5容器打开这个链接
        }];

        [[DoraemonManager shareInstance] install];
    #endif
}
```

 通过以上步骤你就可以使用DorameonKit所有的内置工具集合。如果你想把自己与业务相关的一些工具代码加入到DoraemonKit中做统一管理的话，你可以按照3.3的步骤来做。

### 3.3: 添加自定义测试模块到Doraemon面板中（非必要）
比如我们要在Doraemon面板中添加一个环境切换的功能。

第一步：新建一个类，实现KDDoraemonPluginProtocol协议中的pluginDidLoad方法，该方法就是以后点击Doraemon工具面板中“环境切换”按钮触发的事件。

比如以代驾司机端为例，点击按钮之后会进入环境切换页面。

```
@implementation KDDoraemonEnvPlugin
- (void)pluginDidLoad{
    [APP_INTERACOTR.rootNav openURL:@"KDSJ://KDDoraemonSFViewController"];
    [[DoraemonManager shareInstance] hiddenHomeWindow];
}
 @end
```
 

第二步：在Doraemon初始化的地方添加第一步中添加的“环境切换”插件

调用DoraemonManager的以下方法：

```
[[DoraemonManager shareInstance] addPluginWithTitle:@"环境切换" icon:@"qiehuang" desc:@"用于app内部环境切换功能" pluginName:@"KDDoraemonEnvPlugin" atModule:@"业务专区"];
```

依次代表 集成到DoraemonKit面板中的标题，图标，描述，插件名称，和所属于的模块。

比如以代驾司机端为例：

```
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    #ifdef DEBUG
       [self configDoraemonKit];
    #endif
}
//配置Doraemon工具集
- (void)configDoraemonKit{
    [[DoraemonManager shareInstance] addPluginWithTitle:@"环境切换" icon:@"qiehuang" desc:@"用于app内部环境切换功能" pluginName:@"KDDoraemonEnvPlugin" atModule:@"业务专区"];
    [[DoraemonManager shareInstance] addH5DoorBlock:^(NSString *h5Url) {
        [APP_INTERACOTR.rootNav openURL:@"KDSJ://KDWebViewController" withQuery:@{@"urlString":h5Url}];
    }];
    [[DoraemonManager shareInstance] install];
}
```


## DoraemonKit相关文档

[英文介绍](https://github.com/didichuxing/DoraemonKit)






