## DoraemonKit如何使用

### 3.1： cocoapods依赖
包含两个subspec，一个是包含“日志显示”的“WithLogger” subspec

```
pod 'DoraemonKit/WithLogger', :configurations => ['Debug']
```

一个是不包含“日志显示”的“Core” subspec

```
pod 'DoraemonKit/Core', :configurations => ['Debug']
```

默认引入“Core” subspec。

tip1：为什么要分区subspec呢？
因为日志显示模块是基于CocoaLumberjack这个三方库，如果你的项目日志不是基于CocoaLumberjack，那你就没有必要引入DoraemonKit中日志显示模块了。

tip2：只在Debug环境中进行集成，不要带到线上。有一些hook操作会污染线上代码。

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

