## Document

#### 1. Dependency

```
dependencies {
	...
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit:1.0.5'
    releaseImplementation 'com.didichuxing.doraemonkit:doraemonkit-no-op:1.0.5'
    ...
}
```

Please use [the lastest release](android-ReleaseNotes.md)。



#### 2. Install

Install `DoraemonKit` in `Application::onCreate()`.

```
@Override
public void onCreate() {
	...
    DoraemonKit.install(application）
     
    // for web container debug, optional
    DoraemonKit.setWebDoorCallback(new WebDoorManager.WebDoorCallback() {
    @Override
    public void overrideUrlLoading(String s) {
        // use your web container open the link
    }
    ...
} 
```



#### 3. Network Monitor（Optional）

Add a dependency in `build.gradle` in root of host project as following.

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

Apply plugin in application module of `build.gradle`

```
...
apply plugin: 'com.doraemon.compiler.plugin'
```



#### 4. Custom Component（Optional）

Define a class implement the interface IKit，the interface describe a component in DoraemonKit panel.

An environment switch component can be defined as following.

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

Register the environment switch component when DoraemonKit installed.

```
@Override
public void onCreate() {
    kits.add(new EnvSwitchKit());
    DoraemonKit.install(application, kits);
    ...
}
```