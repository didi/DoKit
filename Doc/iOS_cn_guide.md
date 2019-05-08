## DoraemonKit如何使用

### 1、cocoapods依赖

```
    pod 'DoraemonKit/Core', '~> 1.1.7', :configurations => ['Debug']
    pod 'DoraemonKit/WithLogger', '~> 1.1.7', :configurations => ['Debug']
    pod 'DoraemonKit/WithGPS', '~> 1.1.7', :configurations => ['Debug']
    pod 'DoraemonKit/WithLoad', '~> 1.1.7', :configurations => ['Debug']
```
Core subspec作为核心，必须引入。

如果你的日志是基于CocoaLumberjack，那你也可以引入WithLogger subspec。

MockGPS存在一些兼容性问题（绝大部分情况是好的，问题详见[https://github.com/didi/DoraemonKit/issues/35](https://github.com/didi/DoraemonKit/issues/35)）, 如果你的app接入MockGPS存在问题的话，可以不用引入WithGPS subspec。


**tip**：只在Debug环境中进行集成，不要带到线上。有一些hook操作会污染线上代码。

### 2、使用DoraemonKit内置工具集的接入方式
在App启动的时候添加一下代码

```
#ifdef DEBUG
#import <DoraemonKit/DoraemonManager.h>
#endif

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    #ifdef DEBUG
        [[DoraemonManager shareInstance] install];
    #endif
}
```

 通过以上步骤你就可以使用DorameonKit所有的内置工具集合。如果你想把自己与业务相关的一些工具代码加入到DoraemonKit中做统一管理的话，你可以按照3的步骤来做。

### 3、添加自定义测试模块到Doraemon面板中（非必要）
比如我们要在Doraemon面板中添加一个环境切换的功能。

第一步：新建一个类，实现DoraemonPluginProtocol协议中的pluginDidLoad方法，该方法就是以后点击Doraemon工具面板中“环境切换”按钮触发的事件。

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
### 4、swift 接入方式
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

