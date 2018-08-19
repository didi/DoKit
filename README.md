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


A collection of tools for iOS App offline test.

## Function List

1. View phone information, app information, phone permissions quickly.
2. Browse sanbox file， and upload files to PC by airdrop.
3. Change phone‘s GPS.
4. H5 url entrance.
5. Logger file show.
6. FPS show.
7. CPU usage show.
8. Memory usage show.
9. Network traffic monitoring.
10. Anr monitoring.
11. Mobile phone performance monitoring by myself.
12. Color picker.
13. View check.
14. Align ruler.

## Function Show
<div  align="center">    
 <img src="https://javer.oss-cn-shanghai.aliyuncs.com/doraemon/openSource/doraemonKit.jpeg" width = "300" height = "500" alt="图片名称" align=center />
</div>
tips: The above two lines are customizable tools by youself. Other than this, they are built-in tools.

## How To Use
### 1： Use Cocoapods to Get latest version of DoraemonKit

DoraemonKit contains two subspecs.
One is the "WithLogger" subspec that contains the log display  function based on ‘CocoaLumberjack’.

```
pod 'DoraemonKit',:subspecs => ['WithLogger'], :git => "DoraemonKit Git地址", :tag => 'tag'
```

The other one is the "Core" subspec that does not contain the log display function.

```
pod 'DoraemonKit',:subspecs => ['Core'], :git => "DoraemonKit Git地址", :tag => 'tag'
```

The "Core" subspec is introduced by default.

Tip: Why do you want to partition the subspec?

Because the log display module is based on the three-party library "CocoaLumberjack", if your project log is not based on "CocoaLumberjack", then you do not need to use the log display module in DoraemonKit.

### 2： Access method using DoraemonKit's built-in toolset
Add code when the app starts.

```
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    #ifdef DEBUG
        [[DoraemonManager shareInstance] addH5DoorBlock:^(NSString *h5Url) {
              //Open this link with your H5 container
        }];

        [[DoraemonManager shareInstance] install];
    #endif
}
```

  Through the above steps you can use all of the built-in tools collection of DorameonKit. If you want to add some of your business-related tool to DoraemonKit for unified management, you can follow the steps in 3.
  
### 3: Add a custom test module to the Doraemon panel (not necessary)
For example, we need to add an environment switch function to the Doraemon panel.

The first step: create a new class, implement the pluginDidLoad method in the KDDoraemonPluginProtocol protocol, this method is to trigger the event by the "Environment Switch" button in the Doraemon tool panel.

Taking our app as an example, after clicking the button, it will enter the environment switching page.

```
@implementation KDDoraemonEnvPlugin
- (void)pluginDidLoad{
    [APP_INTERACOTR.rootNav openURL:@"KDSJ://KDDoraemonSFViewController"];
    [[DoraemonManager shareInstance] hiddenHomeWindow];
}
 @end
```
 
Step 2: Add the "Environment Switching" plugin added in the first step where Doraemon is initialized.


```
[[DoraemonManager shareInstance] addPluginWithTitle:@"环境切换" icon:@"qiehuang" desc:@"用于app内部环境切换功能" pluginName:@"KDDoraemonEnvPlugin" atModule:@"业务专区"];
```

It in turn represents the title, icon, description, plugin name, and the module it belongs to the DoraemonKit panel.

Take our App as an example:

```
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    #ifdef DEBUG
       [self configDoraemonKit];
    #endif
}
//Initialize the Doraemon toolset
- (void)configDoraemonKit{
    [[DoraemonManager shareInstance] addPluginWithTitle:@"环境切换" icon:@"qiehuang" desc:@"用于app内部环境切换功能" pluginName:@"KDDoraemonEnvPlugin" atModule:@"业务专区"];
    [[DoraemonManager shareInstance] addH5DoorBlock:^(NSString *h5Url) {
        [APP_INTERACOTR.rootNav openURL:@"KDSJ://KDWebViewController" withQuery:@{@"urlString":h5Url}];
    }];
    [[DoraemonManager shareInstance] install];
}
```

## Related documents

[Chinese Readme](ChineseReadme.md地址)




