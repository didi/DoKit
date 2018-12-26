## How To Use
### 1： Use Cocoapods to Get latest version of DoraemonKit

DoraemonKit contains two subspecs.
One is the "WithLogger" subspec that contains the log display  function based on ‘CocoaLumberjack’.

```
pod 'DoraemonKit/WithLogger','1.1.3', :configurations => ['Debug']
```

The other one is the "Core" subspec that does not contain the log display function.

```
pod 'DoraemonKit/Core','1.1.3', :configurations => ['Debug']
```

The "Core" subspec is introduced by default.

Tip 1: Why do you want to partition the subspec?

Because the log display module is based on the third-party library "CocoaLumberjack", if you don't need it, use "Core" subspec.

Tip 2: Use DoraemonKit in debug model.

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




