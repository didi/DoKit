## How To Use
### 1： Use Cocoapods to Get latest version of DoraemonKit

```
    pod 'DoraemonKit/Core', '~> 1.1.7', :configurations => ['Debug']
    pod 'DoraemonKit/WithLogger', '~> 1.1.7', :configurations => ['Debug']
    pod 'DoraemonKit/WithGPS', '~> 1.1.7', :configurations => ['Debug']
    pod 'DoraemonKit/WithLoad', '~> 1.1.7', :configurations => ['Debug']
```

### 2： Access method using DoraemonKit's built-in toolset
Add code when the app starts.

```
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    #ifdef DEBUG
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




