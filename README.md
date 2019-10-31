<div align="center">    
 <img src="https://javer.oss-cn-shanghai.aliyuncs.com/doraemon/github/DoraemonKit_github.png" width = "150" height = "150" alt="DoraemonKit" align=left />
 <img src="https://img.shields.io/github/license/didi/DoraemonKit.svg" align=left />
 <img src="https://img.shields.io/badge/Android-1.2.8-blue.svg" align=left />
 <img src="https://img.shields.io/badge/iOS-1.2.4-yellow.svg" align=left />
 <img src="https://img.shields.io/badge/miniapp-0.0.1-red.svg" align=left />
 <img src="https://img.shields.io/badge/PRs-welcome-brightgreen.svg" align=left />
</div>


<br/>
<br/>
<br/>
<br/>
<br/>
<br/>
<br/>

**DoraemonKit** /'dɔ:ra:'emɔn/，简称`DoKit`，中文名 `哆啦A梦`，意味着能够像哆啦A梦一样提供给他的主人各种各样的工具。Just Do Kit

> [English Introduction](README_EN.md)

## 开发背景

每一个稍微有点规模的 App，总会自带一些线下的测试功能代码，比如环境切换功能、帧率查看功能等等，这些功能的切换入口往往放在各式各样的入口中，比如一些特殊的手势，双击 statusBar，双击某一个功能区块，或者新建一个 keyWindow 始终至于 App 最上方等等，而且每一个 App 里面的线下附带功能模块很多是相似的，比如帧率查看、内存和 CPU 监控等等，但是现在基本上都是每个 App 都是自己实现了一份，经历了以上的问题之后，DoraemonKit 就有了它存在的意义。

DoraemonKit 是一个功能集合面板，能够让每一个 App 快速接入一些常用的或者你没有实现的一些辅助开发工具、测试效率工具、视觉辅助工具，而且能够完美在 Doraemon 面板中接入你已经实现的与业务紧密耦合的一些非通有的辅助工具，功能强大，接入方便，便于扩展。

**简单总结**

1、DoraemonKit 能够快速让你的业务测试代码能够在这里统一管理，统一收口；   
2、DoraemonKit 内置很多常用的工具，避免重复实现，一次接入，你将会拥有强大的工具集合。

## 效果演示

<div align="center">    
  <img src="https://javer.oss-cn-shanghai.aliyuncs.com/2019/github/dokit2.png" width = "300" height = "780" alt="DoKit 首页效果演示" align=center />
</div>

## 功能模块

### 一、常用工具

1. **【App 信息查看】** 快速查看手机信息，App 信息，权限信息的渠道，避免去手机设置查找或者查看项目源代码的麻烦；
2. **【沙盒浏览】** App 内部文件浏览的功能，支持删除和预览, 并且能通过 AirDrop 或者其他分享方式上传到 PC 中，进行更加细致的操作；
3. **【MockGPS】** App 能定位到全国各地，支持地图地位和手动输入经纬度；
4. **【H5任意门】** 开发测试同学可以快速输入 H5 页面地址，查看该页面效果；
5. **【Crash查看】** 方便本地打印出出现 Crash 的堆栈；
6. **【子线程UI】** 快速定位哪一些 UI 操作在非主线程中进行渲染，避免不必要的问题；（iOS独有）
7. **【清除本地数据】** 一键删除沙盒中所有数据；
8. **【NSLog】** 把所有 NSLog 信息打印到UI界面，避免没有开发证书无法调式的尴尬；
9. **【Lumberjack】** 每一条 CocoaLumberjack 的日志信息，都在在 App 的界面中显示出来，再也不需要导出日志这么麻烦;（iOS独有）
10. **【DBView】** 通过网页方便快捷的操作应用内数据库，让数据库的调试变得非常优雅;
11. **【模拟弱网】** 限制网速，模拟弱网环境下App的运行情况。（android独有）

### 二、性能检测

1. **【帧率】** App 帧率信息提供波形图查看功能，让帧率监控的趋势更加明显；
2. **【CPU】** App CPU 使用率信息提供波形图查看功能，让 CPU 监控的趋势更加形象；
3. **【内存】** App 内存使用量信息提供波形图查看功能，让内存监控的趋势更加鲜明；
4. **【流量】** 拦截 App 内部流量信息，提供波形图展示、流量概要展示、流量列表展示、流量筛选、流量详情，对流量信息统一拦截，成为我们 App 中自带的 “Charles”；
5. **【卡顿】** 锁定 App 出现卡顿的时刻，打印出对应的代码调用堆栈；
6. **【大图检测】** 通过流量监测，找出所有的大小超标的图片，避免下载大图造成的流量浪费和渲染大图带来的CPU消耗。
7. **【自定义】** 可以选择你要监控的选项，包括 FPS、CPU、内存、流量。监控完毕之后，把数据保存到本地，按照页面进行维度进行分析；
8. **【启动耗时】** 无侵入的统计出App启动过程的总共耗时；
9. **【UI层级检查】** 检查出每一个页面中层级最深的元素；
10. **【函数耗时】** 从函数级别分析app性能瓶颈；
11. **【Load】** 找出所有的Load方法，并给出耗时分析；（iOS独有）
12. **【内存泄漏】** 找出App中所有的内存泄漏的问题。

### 三、视觉工具

1. **【颜色吸管】** 方便设计师 UI 捉虫的时候，查看每一个组件的颜色值是否设置正确；
2. **【组件检查】** 可以抓取任意一个UI控件，查看它们的详细信息，包括控件名称、控件位置、背景色、字体颜色、字体大小；
3. **【对齐标尺】** 参考 Android 系统自带测试工具，能够实时捕获屏幕坐标，并且可以查看组件是否对齐；
4. **【元素边框线】** 绘制出每一个 UI 组件的边框，对于组件布局有一定的参考意义。 

### 四、Weex专项工具（CML专项工具）

1. **【console日志查看】** 方便在端上查看每一个Weex文件中的console日志，提供分级和搜索功能；
2. **【storage缓存查看】** 将Weex中的storage模块的本地缓存数据可视化展示；
3. **【容器信息】** 查看每一个打开的Weex页面的基本信息和性能数据；
4. **【DevTool】** 快速开启Weex DevTool的扫码入口。

tips ： 如果使用我们滴滴优秀的开源跨端方案 [chameleon](https://github.com/didi/chameleon) 也可以集成该工具集合

### 五、支持自定义的业务工具集成到面板中

统一维护和管理所有的测试模块，详见接入手册

### 六、微信小程序专项工具

详见 [Doraemon mini program debugger](https://github.com/didi/DoraemonKit/tree/master/miniapp)



## 接入文档

- [iOS 接入文档](Doc/iOS_cn_guide.md)
- [Android 接入文档](Doc/android_cn_guide.md)
- [微信小程序 接入文档](https://github.com/didi/DoraemonKit/blob/master/miniapp/README.md)

## 相关文档
- [iOS 研发助手 DoraemonKit 技术实现（一）](https://www.jianshu.com/p/00763123dbc4)
- [iOS 研发助手 DoraemonKit 技术实现（二）](https://www.jianshu.com/p/4091870ca3f0)
- [DoKit支持iOS本地crash查看功能](https://juejin.im/post/5d76184ce51d4561d106cc65)
- [开源组件 DoraemonKit 之 Android 版本技术实现（一）](https://juejin.im/post/5c4dcfe8518825261e1f2978)
- [开源组件 DoraemonKit 之 Android 版本技术实现（二）](https://juejin.im/post/5c73db105188256ec63f13bb)
- [DoKit支持Activity启动耗时统计方案](https://juejin.im/post/5d70bc3051882571ed61e407)
- [DoKit 微信小程序SDK对外发布](https://juejin.im/post/5d9bf252518825095c3c5e32)

## 更新日志

- [iOS-ReleaseNotes](Doc/iOS-ReleaseNotes.md)
- [Android-ReleaseNotes](Doc/android-ReleaseNotes.md)
- [微信小程序-ReleaseNotes](Doc/miniapp-ReleaseNotes.md)

## QQ 交流群

<div align="center">    
 <img src="https://javer.oss-cn-shanghai.aliyuncs.com/doraemon/github/DoraemonKitQQ.jpeg" width = "160" height = "200" alt="QQ 交流群" align=left />
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

## 项目成员

### 发起者 / 负责人
[yixiangboy](https://github.com/yixiangboy)

### 内部核心成员
[LinJZong](https://github.com/LinJZong) 、
[wanglikun7342](https://github.com/wanglikun7342) 、
[jtsky](https://github.com/jtsky) 、
[wenquanlebao](https://github.com/wenquanlebao) 、
[hiXgb](https://github.com/hiXgb) 、 
[Chinnko](https://github.com/Chinnko) 、 
[y644938647](https://github.com/y644938647) 、 
[goolong](https://github.com/goolong) 、
[miracle9312](https://github.com/miracle9312) 、
[lwhsgz123](https://github.com/lwhsgz123)

### 外部贡献者
[huakucha](https://github.com/huakucha) 、
[HuginChen](https://github.com/HuginChen) 、
[feng562925462](https://github.com/feng562925462) 、
[azhon](https://github.com/azhon) 、
[rex26](https://github.com/rex26) 、
[csc-EricWu](https://github.com/csc-EricWu) 、
[dengyuhan](https://github.com/dengyuhan) 、
[0xd-cc](https://github.com/0xd-cc) 、
[k373379320](https://github.com/k373379320) 、
[fabcz](https://github.com/fabcz) 、
[y500](https://github.com/y500) 、
[Knight-ZXW](https://github.com/Knight-ZXW) 、
[boai](https://github.com/boai) 、
[klone1127](https://github.com/klone1127) 、
[DeveloperLY](https://github.com/DeveloperLY) 、
[sagdragon](https://github.com/sagdragon) 、
[ccworld1000](https://github.com/ccworld1000)


如何成为外部贡献者？ 提交有意义的PR，并被采纳。


## 协议

<img alt="Apache-2.0 license" src="https://lucene.apache.org/images/mantle-power.png" width="128">

DoraemonKit 基于 Apache-2.0 协议进行分发和使用，更多信息参见 [协议文件](LICENSE)。

## 使用提醒
为了统计本开源软件的外部使用情况，我们会上传用户app的相关信息，包括appName、bundleId。
这些信息我们收集绝不用于任何恶意用途。

iOS上传代码详见DoraemonStatisticsUtil类中的实现

Andoid上传代码详见DoraemonStatisticsUtil类中的实现

敬请各位用户知晓。

## 友情链接
1. 我们部门的另外一款开源工具，一个终端侧AI集成运行时环境 [AoE](https://github.com/didi/AoE)
2. 我们部门的技术公众号（普惠出行产品技术公众号），欢迎关注

![https://javer.oss-cn-shanghai.aliyuncs.com/2019/github/phgzh.jpg](https://javer.oss-cn-shanghai.aliyuncs.com/2019/github/phgzh.jpg)
