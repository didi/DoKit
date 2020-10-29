## DoraemonKit如何使用

### 一、集成方式

#### 1.1: cocoapods依赖

```
    pod 'DoraemonKit/Core', '~> 3.0.4', :configurations => ['Debug'] //必选
    pod 'DoraemonKit/WithLogger', '~> 3.0.4', :configurations => ['Debug'] //可选
    pod 'DoraemonKit/WithGPS', '~> 3.0.4', :configurations => ['Debug'] //可选
    pod 'DoraemonKit/WithLoad', '~> 3.0.4', :configurations => ['Debug'] //可选
    pod 'DoraemonKit/WithWeex', '~> 3.0.4', :configurations => ['Debug'] //可选
    pod 'DoraemonKit/WithDatabase', '~> 3.0.4', :configurations => ['Debug'] //可选
    pod 'DoraemonKit/WithMLeaksFinder', '3.0.4', :configurations => ['Debug'] //可选
```
Core subspec作为核心，必须引入。

如果你的日志是基于CocoaLumberjack，那你也可以引入WithLogger subspec。

如果你想使用模拟定位的功能，请单独接入WithGPS subspec。

如果你要集成Load耗时检测的话，那就请接入WithLoad subspec。

如果你要集成Weex的相关专项工具的话，那就请接入WithWeex subspec。

如果你要使用[YYDebugDatabase](https://github.com/y500/iOSDebugDatabase)在网页端调式数据库的话，那就请接入WithDatabase subspec。

如果你要使用[MLeaksFinder](https://github.com/Tencent/MLeaksFinder)查找内存泄漏的问题的话，那就请接入WithMLeaksFinder subspec。

#### 1.2: Carthage依赖

```
git "https://github.com/didi/DoraemonKit.git"  "c3.0.4"
    或者
    github "didi/DoraemonKit"
```
**tip**：只在Debug环境中进行集成，不要带到线上。有一些hook操作会污染线上代码。


### 二、使用DoraemonKit内置工具集的接入方式
在App启动的时候添加一下代码

```objective-c
#ifdef DEBUG
#import <DoraemonKit/DoraemonManager.h>
#endif

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    #ifdef DEBUG
        //默认
        [[DoraemonManager shareInstance] install];
        // 或者使用传入位置,解决遮挡关键区域,减少频繁移动
        //[[DoraemonManager shareInstance] installWithStartingPosition:CGPointMake(66, 66)];
    #endif
}
```

 通过以上步骤你就可以使用DorameonKit所有的内置工具集合。如果你想把自己与业务相关的一些工具代码加入到DoraemonKit中做统一管理的话，你可以按照3的步骤来做。

### 三、添加自定义测试模块到Doraemon面板中（非必要）
比如我们要在Doraemon面板中添加一个环境切换的功能。

第一步：新建一个类，实现DoraemonPluginProtocol协议中的pluginDidLoad方法，该方法就是以后点击Doraemon工具面板中"环境切换"按钮触发的事件。

比如以代驾司机端为例，点击按钮之后会进入环境切换页面。

```
@implementation KDDoraemonEnvPlugin
- (void)pluginDidLoad{
    [APP_INTERACOTR.rootNav openURL:@"KDSJ://KDDoraemonSFViewController"];
    [[DoraemonManager shareInstance] hiddenHomeWindow];
}
 @end
```


第二步：在Doraemon初始化的地方添加第一步中添加的"环境切换"插件

调用DoraemonManager的以下方法：

```
[[DoraemonManager shareInstance] addPluginWithTitle:@"环境切换" icon:@"doraemon_default" desc:@"用于app内部环境切换功能" pluginName:@"KDDoraemonEnvPlugin" atModule:@"业务专区"];
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
    [[DoraemonManager shareInstance] addPluginWithTitle:@"环境切换" icon:@"doraemon_default" desc:@"用于app内部环境切换功能" pluginName:@"KDDoraemonEnvPlugin" atModule:@"业务专区"];
    [[DoraemonManager shareInstance] addH5DoorBlock:^(NSString *h5Url) {
        [APP_INTERACOTR.rootNav openURL:@"KDSJ://KDWebViewController" withQuery:@{@"urlString":h5Url}];
    }];
    [[DoraemonManager shareInstance] install];
}
```

**tips**:目前也支持使用block方式接入自定义测试模块，使用方式如下：

```

    [[DoraemonManager shareInstance] addPluginWithTitle:@"标题" icon:@"doraemon_default" desc:@"测试插件" pluginName:@"TestPlugin(可以为空)" atModule:DoraemonLocalizedString(@"业务工具") handle:^(NSDictionary *itemData) {
        NSLog(@"handle block plugin");
    }];
    
```

### 四、swift 接入方式
pod 同 OC 一样

#### swift 4.0 4.2 5.0 接入方式都一样

```
import UIKit

#if DEBUG
    import DoraemonKit
#endif

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?


    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        
    #if DEBUG
        DoraemonManager.shareInstance().install()
    #endif
        return true
    }
    
}
```

