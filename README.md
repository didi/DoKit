<div  align="center">    
 <img src="https://javer.oss-cn-shanghai.aliyuncs.com/doraemon/github/DoraemonKit_github.png" width = "150" height = "150" alt="图片名称" align=left />
 <img src="https://img.shields.io/github/license/didi/DoraemonKit.svg" align=left />
 <img src="https://img.shields.io/badge/Android-1.0.5-blue.svg" align=left />
 <img src="https://img.shields.io/badge/iOS-1.1.4-yellow.svg" align=left />
 <img src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg" align=left />
</div>

<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>

**DoraemonKit** /'dɔ:ra:'emɔn/，简称`DoKit`，中文名 `哆啦A梦`，意味着能够像哆啦A梦一样提供给他的主人各种各样的工具。

## 背景
每一个稍微有点规模的App，总会自带一些线下的测试功能代码，比如环境切换功能、帧率查看功能等等，这些功能的切换入口往往放在各式各样的入口中，比如一些特殊的手势，双击statusBar，双击某一个功能区块，或者新建一个keyWindow始终至于app最上方等等，而且每一个app里面的线下附带功能模块很多是相似的，比如帧率查看、内存和cpu监控等等，但是现在基本上都是每个app都是自己实现了一份，经历了以上的问题之后，DoraemonKit就有了它存在的意义。

DoraemonKit是一个功能集合面板，能够让每一个app快速接入一些常用的或者你没有实现的一些辅助开发工具、测试效率工具、视觉辅助工具，而且能够完美在Doraemon面板中接入你已经实现的与业务紧密耦合的一些非通有的辅助工具，功能强大，接入方便，便于扩展。

**简单总结**

1、DoraemonKit能够快速让你的业务测试代码能够在这里统一管理，统一收口。

2、DoraemonKit内置很多常用的工具，避免重复实现，一次接入，你将会拥有强大的工具集合。

## 效果演示
<div  align="center">    
  <img src="https://javer.oss-cn-shanghai.aliyuncs.com/2019/DoraemonKitHome.jpg" width = "300" height = "565" alt="DoKit首页效果演示" align=center />
</div>

## DoraemonKit具有哪些功能
### 一、常用工具
1. **【App信息查看】** 快速查看手机信息，App信息，权限信息的渠道，避免去手机设置查找或者查看项目源代码的麻烦。
2. **【沙盒浏览】** App内部文件浏览的功能，支持删除和预览, 并且能通过airDrop或者其他分享方式上传到PC中，进行更加细致的操作。
3. **【MockGPS】** App能定位到全国各地，支持地图地位和手动输入经纬度。
4. **【H5任意门】** 开发测试同学可以快速输入H5页面地址，查看该页面效果。
5. **【Crash查看】** 方便本地打印出出现crash的堆栈。
6. **【子线程UI】** 快速定位哪一些UI操作在非主线程中进行渲染，避免不必要的问题。
7. **【清除本地数据】** 一键删除沙盒中所有数据。
8. **【NSLog】** 把所有NSLog信息打印到UI界面，避免没有开发证书无法调式的尴尬。
9. **【Lumberjack】** 每一条CocoaLumberjack的日志信息，都在在App的界面中显示出来，再也不需要导出日志这么麻烦。

### 二、性能检测
1. **【帧率】** App 帧率信息提供波形图查看功能，让帧率监控的趋势更加明显。
2. **【CPU】** App CPU使用率信息提供波形图查看功能，让CPU监控的趋势更加形象。
3. **【内存】** App 内存使用量信息提供波形图查看功能，让内存监控的趋势更加鲜明。
4. **【流量】** 拦截App内部流量信息，提供波形图展示、流量概要展示、流量列表展示、流量筛选、流量详情，对流量信息统一拦截，成为我们app中自带的“Charles”。
5. **【卡顿】** 锁定App出现卡顿的时刻，打印出对应的代码调用堆栈。
6. **【自定义】** 可以选择你要监控的选项，包括FPS、CPU、内存、流量。监控完毕之后，把数据保存到本地，也可以导出来做更加细致的分析。
7. **【Load耗时】** Load函数耗时是iOS启动性能优化中重要的一项，该功能可以打印出所有Load函数的耗时，给开发者以参考。

### 三、视觉工具
1. **【颜色吸管】** 方便设计师UI捉虫的时候，查看每一个组件的颜色值是否设置正确。
2. **【组件检查】** 可以抓取任意一个UI控件，查看它们的详细信息，包括控件名称、控件位置、背景色、字体颜色、字体大小。
3. **【对齐标尺】** 参考Android系统自带测试工具，能够实时捕获屏幕坐标，并且可以查看组件是否对齐。
4. **【元素边框线】** 绘制出每一个UI组件的边框，对于组件布局有一定的参考意义。 

## DoraemonKit接入文档

[iOS接入文档](https://github.com/didi/DoraemonKit/blob/master/Doc/iOS_cn_guide.md)

[Android接入文档](https://github.com/didi/DoraemonKit/blob/master/Doc/android_cn_guide.md)


## QQ交流群

<div  align="center">    
 <img src="https://javer.oss-cn-shanghai.aliyuncs.com/doraemon/github/DoraemonKitQQ.jpeg" width = "160" height = "200" alt="图片名称" align=left />
</div>

<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>


## DoraemonKit相关文档

[iOS-ReleaseNotes](https://github.com/didi/DoraemonKit/blob/master/Doc/iOS-ReleaseNotes.md)

[Android-ReleaseNotes](https://github.com/didi/DoraemonKit/blob/master/Doc/android-ReleaseNotes.md)

[英文介绍](https://github.com/didi/DoraemonKit)

[iOS研发助手DoraemonKit技术实现（一）](https://www.jianshu.com/p/00763123dbc4)

[iOS研发助手DoraemonKit技术实现（二）](https://www.jianshu.com/p/4091870ca3f0)

[iOS研发助手DoraemonKit技术实现之Crash查看](https://www.jianshu.com/p/5b17d78b9c7b)

[开源组件DoraemonKit之Android版本技术实现（一）](https://juejin.im/post/5c4dcfe8518825261e1f2978)

## 成员
### 发起者 负责人
[yixiangboy](https://github.com/yixiangboy)

### 贡献者
[wenquanlebao](https://github.com/wenquanlebao)  ，
[hiXgb](https://github.com/hiXgb)  ，
[teethandnail](https://github.com/teethandnail)  ，
[wanglikun7342](https://github.com/wanglikun7342)  ，
[Chinnko](https://github.com/Chinnko)  ，
[LinJZong](https://github.com/LinJZong)  ，
[y644938647](https://github.com/y644938647)
