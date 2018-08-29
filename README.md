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


A collection of testing tools for iOS App development.

## Function List

1. Including an entrance to examine information of device, app and app's permissions.
2. Including a file browser, with a convenient way to transfer file through airDrop.
3. Including a tool for mocking GPS location.
4. Including a built-in web browser.
5. Including a log printer, with every log printed in app
6. Including profilers for App's FPS, CPU usage, memory usage and network traffic. All of them are shown in charts and can be saved for further analysis.
7. Including a color picker, obtaining color values of every pixel in an easy manner.
8. Including a UI viewer, obtaining properties, such as name, position, background color and font size, of every view.
9. Including a coordinate ruler, a useful tool to acquire screen coordinates and to check the alignment of views.

## Function Show
<div  align="center">    
 <img src="https://javer.oss-cn-shanghai.aliyuncs.com/doraemon/openSource/github/DoraemonKit.png" width = "300" height = "565" alt="图片名称" align=center />
</div>
tips: Tools in the top two lines are user-defined. Others are built-in.

## How To Use
### 1： Use Cocoapods to Get latest version of DoraemonKit

DoraemonKit contains two subspecs.
One is the "WithLogger" subspec that contains the log display  function based on ‘CocoaLumberjack’.

```
pod 'DoraemonKit/WithLogger', '~> 1.0.0'
```

The other one is the "Core" subspec that does not contain the log display function.

```
pod 'DoraemonKit/Core', '~> 1.0.0'
```

The "Core" subspec is introduced by default.

Tip: Why do you want to partition the subspec?

Because the log display module is based on the third-party library "CocoaLumberjack", if you don't need it, use "Core" subspec.

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

  Through the above steps you can use all of the built-in tools of DorameonKit. If you want to add some of your customized tools, see chapter 3.
  
### 3: Add a custom test module to the Doraemon panel (non-essential)
For example, we want to add an environment switch module to the Doraemon panel.

Step 1: create a new class, implement the pluginDidLoad method in the KDDoraemonPluginProtocol protocol, this method is to be called when the "Environment Switch" button is clicked.

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

It in turn shows the title, icon, description, plugin name, and the module it belongs to.

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

[Chinese Readme](https://github.com/didi/DoraemonKit/blob/master/Doc/ChineseReadme.md)




