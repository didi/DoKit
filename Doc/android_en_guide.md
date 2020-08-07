# Android Guide

|DoKit|new Version|Desc|
|-    |-      |-  |
|support Androidx|3.2.0|support Androidx from v3.1.0|
|supprot android support|3.0.8.0|Version 3.0.8.0 corresponds to the function of 3.2.0. Later support will be updated from time to time, mainly based on community feedback, please upgrade and adapt to Androidx as soon as possible|


#### 1. DoKit SDK Dependencie

```groovy
dependencies {
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit:3.2.0'
    releaseImplementation 'com.didichuxing.doraemonkit:doraemonkit-no-op:3.2.0'
}
```


**Note:** 

If you cannot download the dependent library through jcenter and report the following error

```
ERROR: Failed to resolve: com.didichuxing.doraemonkit:doraemonkit:3.2.0
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
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit-weex:3.2.0'
    …
}
```

If you need to integrate LeakCanary, you can directly add the following dependencies

```groovy
dependencies {
    …
    debugImplementation 'com.didichuxing.doraemonkit:doraemonkit-leakcanary:3.2.0'
    …
}
```
LeakCanary has been dynamically integrated in doraemonkit, you do n’t need to do manual integration by yourself, just add the above dependency.


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
        classpath 'com.didichuxing.doraemonkit:doraemonkit-plugin:3.2.0'
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
    //dokit plugin switch
    comm {
        
        gpsSwitch true
        
        networkSwitch true
        
        bigImgSwitch true
    }

    slowMethod {
        //0: The default mode prints the function call stack. The specified entry needs to be added. The default is application onCreate and attachBaseContext
        //1:Normal mode Time-consuming printing of a function at runtime Global business code function insertion
        strategy 0
        //Function switch
        methodSwitch true

        //Call stack mode configuration
        stackMethod {
            //The default value is 5ms. Functions smaller than this value are not displayed in the call stack
            thresholdTime 10
            //Call stack function entry
            enterMethods = ["com.didichuxing.doraemondemo.MainDebugActivity.test1"]
        }
        //Normal mode configuration
        normalMethod {
            //The default value is 500ms. Functions smaller than this value will not be printed in the console at runtime
            thresholdTime 500
            //The name of the package to be inserted for the function
            packageNames = ["com.didichuxing.doraemondemo"]
            //No package name & class name for function insertion
            methodBlacklist = ["com.didichuxing.doraemondemo.dokit"]
        }
    }
}
```
The gradle.properties file in the project root directory is configured as follows
```
# dokit global configuration
# Plug-in switch
DOKIT_PLUGIN_SWITCH=true
# Plugin log
DOKIT_LOG_SWITCH=true
# dokit slow function switch
DOKIT_METHOD_SWITCH=true
# dokit function call stack level
DOKIT_METHOD_STACK_LEVEL=4
# 0:The default mode is to print the function call stack, and the specified entry needs to be added. The default is application onCreate and attachBaseContext
# 1:Normal mode prints the time consuming of a certain function when running, inserts global business code functions
DOKIT_METHOD_STRATEGY=0
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

**DoraemonKit入口api**
```
object DoraemonKit {
    
    @JvmStatic
    fun install(app: Application) {
    }

   
    @JvmStatic
    fun install(app: Application, productId: String) {
    }
    
   //User-defined user zone grouping
    @JvmStatic
    fun install(app: Application, mapKits: LinkedHashMap<String, MutableList<AbstractKit>>, productId: String) {
    }
   
   //Default user zone grouping
    @JvmStatic
    fun install(app: Application, listKits: MutableList<AbstractKit>, productId: String) {
    }

    /**
     * @param app
     * @param mapKits Custom kits are grouped according to users. It is recommended to choose mapKits first. If both are passed, mapKits will be selected.
     * @param listKits  Custom kits are compatible with old APIs
     * @param productId ProductId applied on the Dokit platform
     */
    @JvmStatic
    private  fun install(app: Application, mapKits: LinkedHashMap<String, MutableList<AbstractKit>>? = linkedMapOf(), listKits: MutableList<AbstractKit>? = mutableListOf(), productId: String? = "") {

    }
    
    //h5 arbitrary door callback
    @JvmStatic
    fun setWebDoorCallback(callback: WebDoorManager.WebDoorCallback?) {
    }

    //Show mainIcon
    @JvmStatic
    fun show() {
    }

   //hide mainIcon
    @JvmStatic
    fun hide() {
    }

    /**
     * Directly display the tool panel page
     */
    @JvmStatic
    fun showToolPanel() {
    }

    /**
     * Hide the tool panel directly
     */
    @JvmStatic
    fun hideToolPanel() {
    }

    

    /**
     * Disable the app information upload switch, this upload information is only for the statistics of DoKit access, if users need to protect app privacy, you can call this method to disable
     */
    @JvmStatic
    fun disableUpload() {
    }

    @JvmStatic
    val isShow: Boolean
        get() = false

    @JvmStatic
    fun setDebug(debug: Boolean) {
    }

    /**
     * Whether to display the main entrance icon
     */
    @JvmStatic
    fun setAwaysShowMainIcon(awaysShow: Boolean) {
    }
}

```

#### 5. FAQ

[Other Problems](http://xingyun.xiaojukeji.com/docs/dokit_en#/SDKProblems)