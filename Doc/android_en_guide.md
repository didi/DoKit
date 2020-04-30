# Android Guide

|DoKit|new Version|Desc|
|-    |-      |-  |
|support Androidx|3.1.4|support Androidx from v3.1.0|
|supprot android support|3.0.3|For support Android support, one or two versions will be maintained, please hug Androidx as soon as possible|


#### 1. DoKit SDK Dependencie

```groovy
dependencies {
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit:3.1.4'
    releaseImplementation 'com.didichuxing.doraemonkit:doraemonkit-no-op:3.1.4'
}
```


**Note:**

If you cannot download the dependent library through jcenter and report the following error

```
ERROR: Failed to resolve: com.didichuxing.doraemonkit:doraemonkit:3.1.4
```

You can try again from the command line (take Mac system as an example under the project root directory)

```
./gradlew clean assembleDebug
```


[new versions](https://github.com/didi/DoraemonKit/blob/master/Doc/android-ReleaseNotes.md)



DoraemonKit currently supports Weex tools, including

* Console Log
* Storage Cache
* Weex Info
* DevTool

If you need to support Weex, you can directly add the following dependencies

```groovy
dependencies {
    …
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit-weex:3.1.4'
    releaseImplementation 'com.didichuxing.doraemonkit:doraemonkit-weex-no-op:3.1.4'
    …
}
```

If you need to integrate LeakCanary, you can directly add the following dependencies

```groovy
dependencies {
    …
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit-leakcanary:3.1.4'
    …
}
```
LeakCanary has been dynamically integrated in doraemonkit, you do n't need to do manual integration by yourself, just add the above dependency.


#### 2. Init

Initialize in the Application onCreate().

```Java
@Override
public void onCreate() {
    DoraemonKit.install(application,null,"pId");
}
```

#### 3. DoKit Plugin（Optional）
Plugin includes the following functions:

1.Longitude and latitude simulation of baidu, tencent and AMap maps
2.UrlConnection, Okhttp packet capture and subsequent interface hook function
3.App Launch Time
4.SlowMethod
5.BigImg

**Apply plugin**

```groovy
buildscript {
    dependencies {
        classpath 'com.didichuxing.doraemonkit:doraemonkit-plugin:3.1.4'

    }
}
```

Add plugin to the app's `build.gradle`.

```groovy
apply plugin: 'com.didi.dokit'
```

**Plugin configuration options:**
Added to the app module's build.gradle file at the same level as android {}
```groovy
dokitExt {
    //Plug-in switch for the entire dokit
    dokitPluginSwitch = true
    //Time Profiler switch
    slowMethodSwitch = true
    //BigImg Switch
    bigImgSwitch = true
    //Custom threshold for time-consuming function unit is ms 1000ms = 1s
    thresholdTime = 200
    //Insert code under the specified package name business code
    packageNames = ["com.didichuxing.doraemondemo"]
}
```



#### 4. Custom function components (optional)

Custom components need to inherit AbstractKit, which corresponds to the components in the Doraemon function panel.

Taking the passenger didi driver's terminal as an example, the components for implementing environment switching are as follows

```Java
public class EnvSwitchKit extends AbstractKit {
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

Register custom components during initialization.

```Java
@Override
public void onCreate() {
    kits.add(new EnvSwitchKit());
    DoraemonKit.install(application, kits);
}
```

**Other Api:**

```Java
@Override
public void onCreate() {
    kits.add(new EnvSwitchKit());
    DoraemonKit.install(application, kits);
    //false:hide icon default is true
    DoraemonKit.setAwaysShowMainIcon(false);
    …
}
```

**Call up the tool panel directly**

```Java
DoraemonKit.showToolPanel();
```

#### 5. FAQ

[Other Problems](SDKProblems.md)