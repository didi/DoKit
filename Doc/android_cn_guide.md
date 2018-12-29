## 接入方式

#### 1. Gradle依赖

```
dependencies {
	...
    debugImplementation 'com.didichuxing.doraemonkit:kit:1.0.5'
    releaseImplementation 'com.didichuxing.doraemonkit:kit-no-op:1.0.5'
    ...
}
```

最新版本参见[这里](android-ReleaseNotes)。



#### 2. 初始化

在App启动的时候进行初始化。

```
@Override
public void onCreate() {
	...
    DoraemonKit.install(application）
     
    // H5任意门功能需要，非必须
    DoraemonKit.setWebDoorCallback(new WebDoorManager.WebDoorCallback() {
    @Override
    public void overrideUrlLoading(String s) {
        // 使用自己的H5容器打开这个链接
    }
    ...
} 
```



#### 3. 流量监控功能（可选）

在项目的build.gradle中添加classpath。

```
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        ...
        classpath 'com.didichuxing.doraemonkit:compiler:1.0.0'
        ...
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
```

在app的build.gradle中添加plugin。

```
...
apply plugin: 'com.doraemon.compiler.plugin'
```



#### 4. 自定义功能组件（可选）

自定义组件需要实现IKit接口，该接口对应哆啦A梦功能面板中的组件。

以黑马乘客端为例，实现环境切换组件如下。

```
public class EnvSwitchKit implements IKit {
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

```
@Override
public void onCreate() {
    kits.add(new EnvSwitchKit());
    DoraemonKit.install(application, kits);
    ...
}
```