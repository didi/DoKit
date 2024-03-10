<div align="center">    
 <img src="https://javer.oss-cn-shanghai.aliyuncs.com/doraemon/github/DoraemonKit_github.png" width = "150" height = "150" alt="DoraemonKit" align=left />
 <img src="https://img.shields.io/github/license/didi/DoraemonKit.svg" align=left />
 <img src="https://img.shields.io/badge/Android-3.5.0.svg" align=left />
 <img src="https://img.shields.io/badge/iOS-3.0.4-yellow.svg" align=left />
 <img src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg" align=left />
</div>

<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>

A full-featured App (iOS & Android) development assistant. You deserve it.

> [中文文档](README.md)

**community activity**

>DoKit official community satisfaction survey

link:https://page.juyanwenjuan.com/jy_0CMpJzlu.html

<div align="center">    
  <img src="https://pt-starimg.didistatic.com/static/starimg/img/INLjGkp9wN1597062733948.jpg" width = "250" alt="DoKit 首页效果演示" align=center />
</div>
 
## Introduction

In the development stage of the App, in order to improve the efficiency of the developer and tester， we have developed a collection of tools with full-featured functions. I can use it to simulate the positioning of the App; preview the content of the sandbox file; view the information and logs of the App; test the performance of the App and view the detailed information of the view, etc. Each tool solves every problem in our app development. 
And our UI interface is simple and beautiful, and the user experience is good.

At present, we provide a total of more than 30+ built-in tools, including 2 platform tools; 10 common tools; 12 performance tools; and 5 ui tools. At the same time, you can also add your own tools in our DoKit panel for unified management.

DoKit is rich in functions, easy to access, and easy to expand. Everyone is welcome to try and feedback.


## SDK Show
<div align="center">    
 <img src="https://javer.oss-cn-shanghai.aliyuncs.com/2020/dokit/dokiten1.png" width = "250" alt="Demonstration" align=center />
</div>

## Feature List
### Common Tools
* App Settings： quickly open the setting page of the specific app
* App Info：view mobile phone information, device information, permission information of the current App
* Sanbox：support sandbox files for viewing, previewing, deleting, sharing and other operations
* Mock GPS：You can uniformly modify the latitude and longitude callbacks inside the App
* Browser：quickly enter the html address to view the effect of the page, and support scan code;
* Clear Sanbox： delete all data in the sandbox
* Log：print all logs to the UI interface for easy viewing
* UserDefaults（iOS）: add, delete, and modify the NSUserDefaults file
* DBView：perform more detailed operations on the DB file on the web
* JavaScript（iOS）：execute scripts in the web view


### Performance Tools
* FPS：view the real-time fps of the app through floating window 
* CPU：view the real-time cpu of the app through floating window 
* Memory：view the real-time memory of the app through floating window 
* Network：view the real-time network of the app through floating window，and analysis of all network data
* Crash：convenient to print out the code stack where Crash appears
* Sub Thread UI：quickly locate UI operations in some sub-threads
* ANR：when the app appears anr, print out the corresponding code call stack
* BigImg：Through network monitoring, find out all the images with oversized size, to avoid the waste of network caused by downloading large images and the CPU consumption caused by rendering large images
* Weak Network：view the running status of the App when the network is not good
* Launch Time：show app launch time
* UI Hierrachy：find the deepest element in each page
* Time Profiler：analyze app performance bottlenecks at the function level
* Memory Leak：quickly locate App memory leaks
* Load（iOS）：check out all "+load" functions in iOS, and time-consuming statistics


### UI Tools

* Color Picker：capture the color value of every point in the app in real time
* View Check：you can touch any view and view their detailed information, including view name, view position, background color, font color, font size
* Align Ruler：ability to capture screen coordinates in real time and see if views are aligned
* View Border：draw the border of each view

### Platform Tools

* Mock Data： App network mock solution, provides a set of network mock solutions based on App network interception, and can complete the mock for network data without modifying the code
* Health Check： integration of multiple DoKit tools, data visualization, quick and accurate positioning of problems, let you know the performance of the app

**tip：** Platform tools need to be used in conjunction with [https://www.dokit.cn/](https://www.dokit.cn/)

## Installation

### iOS
#### Cocoapods
```
    pod 'DoraemonKit/Core', '~> 3.0.2', :configurations => ['Debug'] #Required
    pod 'DoraemonKit/WithGPS', '~> 3.0.2', :configurations => ['Debug'] #Optional
    pod 'DoraemonKit/WithLoad', '~> 3.0.2', :configurations => ['Debug'] #Optional
```
#### Example Usage

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

### Android
#### 1、Download
To use DoKit , add the plugin to your buildscript:
```
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        classpath 'com.didichuxing.doraemonkit:dokitx-plugin:3.3.5'
    }
}

```
and then apply it in your app module

```
apply plugin: 'com.didi.dokit'
```

and then implementation DoKit SDK
```
debugImplementation "com.didichuxing.doraemonkit:dokitx:3.3.5"
releaseImplementation "com.didichuxing.doraemonkit:dokitx-no-op:3.3.5"
```

#### 2、SDK Init
```
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DoraemonKit.install(this);
    }
}
```

##  Product Manual

If you want to know more details about DoKit, please visit 
**[https://www.dokit.cn/](https://www.dokit.cn/)**.



## License

<img alt="Apache-2.0 license" src="https://www.apache.org/img/ASF20thAnniversary.jpg" width="128">

DoraemonKit is available under the Apache-2.0 license. See the [LICENSE](LICENSE) file for more info.
