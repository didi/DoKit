<div align="center">    
 <img src="https://javer.oss-cn-shanghai.aliyuncs.com/doraemon/github/DoraemonKit_github.png" width = "150" height = "150" alt="DoraemonKit" align=left />
 <img src="https://img.shields.io/github/license/didi/DoraemonKit.svg" align=left />
 <img src="https://img.shields.io/badge/Android-1.1.7-blue.svg" align=left />
 <img src="https://img.shields.io/badge/iOS-1.1.8-yellow.svg" align=left />
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
  <img src="https://javer.oss-cn-shanghai.aliyuncs.com/2019/DoraemonKitHome.jpg" width = "300" height = "565" alt="DoKit 首页效果演示" align=center />
</div>

## 功能模块

### 一、常用工具

1. **【App 信息查看】** 快速查看手机信息，App 信息，权限信息的渠道，避免去手机设置查找或者查看项目源代码的麻烦；
2. **【沙盒浏览】** App 内部文件浏览的功能，支持删除和预览, 并且能通过 AirDrop 或者其他分享方式上传到 PC 中，进行更加细致的操作；
3. **【MockGPS】** App 能定位到全国各地，支持地图地位和手动输入经纬度；
4. **【H5任意门】** 开发测试同学可以快速输入 H5 页面地址，查看该页面效果；
5. **【Crash查看】** 方便本地打印出出现 Crash 的堆栈；
6. **【子线程UI】** 快速定位哪一些 UI 操作在非主线程中进行渲染，避免不必要的问题；
7. **【清除本地数据】** 一键删除沙盒中所有数据；
8. **【NSLog】** 把所有 NSLog 信息打印到UI界面，避免没有开发证书无法调式的尴尬；
9. **【Lumberjack】** 每一条 CocoaLumberjack 的日志信息，都在在 App 的界面中显示出来，再也不需要导出日志这么麻烦。

### 二、性能检测

1. **【帧率】** App 帧率信息提供波形图查看功能，让帧率监控的趋势更加明显；
2. **【CPU】** App CPU 使用率信息提供波形图查看功能，让 CPU 监控的趋势更加形象；
3. **【内存】** App 内存使用量信息提供波形图查看功能，让内存监控的趋势更加鲜明；
4. **【流量】** 拦截 App 内部流量信息，提供波形图展示、流量概要展示、流量列表展示、流量筛选、流量详情，对流量信息统一拦截，成为我们 App 中自带的 “Charles”；
5. **【卡顿】** 锁定 App 出现卡顿的时刻，打印出对应的代码调用堆栈；
6. **【自定义】** 可以选择你要监控的选项，包括 FPS、CPU、内存、流量。监控完毕之后，把数据保存到本地，也可以导出来做更加细致的分析；
7. **【Load 耗时】** Load 函数耗时是 iOS 启动性能优化中重要的一项，该功能可以打印出所有 Load 函数的耗时，给开发者以参考。

### 三、视觉工具

1. **【颜色吸管】** 方便设计师 UI 捉虫的时候，查看每一个组件的颜色值是否设置正确；
2. **【组件检查】** 可以抓取任意一个UI控件，查看它们的详细信息，包括控件名称、控件位置、背景色、字体颜色、字体大小；
3. **【对齐标尺】** 参考 Android 系统自带测试工具，能够实时捕获屏幕坐标，并且可以查看组件是否对齐；
4. **【元素边框线】** 绘制出每一个 UI 组件的边框，对于组件布局有一定的参考意义。 

## 接入文档

- [iOS 接入文档](Doc/iOS_cn_guide.md)
- [Android 接入文档](Doc/android_cn_guide.md)

## 相关文档

- [iOS 研发助手 DoraemonKit 技术实现（一）](https://www.jianshu.com/p/00763123dbc4)
- [iOS 研发助手 DoraemonKit 技术实现（二）](https://www.jianshu.com/p/4091870ca3f0)
- [iOS 研发助手 DoraemonKit 技术实现之 Crash 查看](https://www.jianshu.com/p/5b17d78b9c7b)
- [开源组件 DoraemonKit 之 Android 版本技术实现（一）](https://juejin.im/post/5c4dcfe8518825261e1f2978)
- [开源组件 DoraemonKit 之 Android 版本技术实现（二）](https://juejin.im/post/5c73db105188256ec63f13bb)

## 更新日志

- [iOS-ReleaseNotes](Doc/iOS-ReleaseNotes.md)
- [Android-ReleaseNotes](Doc/android-ReleaseNotes.md)

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

<table id='team'>
    <tr>
        <td id='yixiangboy'>
            <a href='https://github.com/yixiangboy'>
        		<img src='https://github.com/yixiangboy.png?v=3&s=330' width="165" height="165">
        	</a>
            <h4 align='center'>
        		<a href='https://github.com/yixiangboy'>yixiangboy</a>
        	</h4>
        </td>
    </tr>
</table>

### 贡献者

<table id='team'>
    <tr>
        <td id='wenquanlebao'>
            <a href='https://github.com/wenquanlebao'>
        		<img src='https://github.com/wenquanlebao.png?v=3&s=330' width="165" height="165">
        	</a>
            <h4 align='center'>
        		<a href='https://github.com/wenquanlebao'>wenquanlebao</a>
        	</h4>
        </td>
        <td id='hiXgb'>
            <a href='https://github.com/hiXgb'>
        		<img src='https://github.com/hiXgb.png?v=3&s=330' width="165" height="165">
        	</a>
            <h4 align='center'>
        		<a href='https://github.com/hiXgb'>hiXgb</a>
        	</h4>
        </td>
        <td id='teethandnail'>
            <a href='https://github.com/teethandnail'>
        		<img src='https://github.com/teethandnail.png?v=3&s=330' width="165" height="165">
        	</a>
            <h4 align='center'>
        		<a href='https://github.com/teethandnail'>teethandnail</a>
        	</h4>
        </td>
        <td id='wanglikun7342'>
            <a href='https://github.com/wanglikun7342'>
        		<img src='https://github.com/wanglikun7342.png?v=3&s=330' width="165" height="165">
        	</a>
            <h4 align='center'>
        		<a href='https://github.com/wanglikun7342'>wanglikun7342</a>
        	</h4>
        </td>
        <td id='Chinnko'>
            <a href='https://github.com/Chinnko'>
        		<img src='https://github.com/Chinnko.png?v=3&s=330' width="165" height="165">
        	</a>
            <h4 align='center'>
        		<a href='https://github.com/Chinnko'>Chinnko</a>
        	</h4>
        </td>
    </tr>
    <tr>
        <td id='LinJZong'>
            <a href='https://github.com/LinJZong'>
        		<img src='https://github.com/LinJZong.png?v=3&s=330' width="165" height="165">
        	</a>
            <h4 align='center'>
        		<a href='https://github.com/LinJZong'>LinJZong</a>
        	</h4>
        </td>
        <td id='y644938647'>
            <a href='https://github.com/y644938647'>
        		<img src='https://github.com/y644938647.png?v=3&s=330' width="165" height="165">
        	</a>
            <h4 align='center'>
        		<a href='https://github.com/y644938647'>y644938647</a>
        	</h4>
        </td>
        <td id='huakucha'>
            <a href='https://github.com/huakucha'>
        		<img src='https://github.com/huakucha.png?v=3&s=330' width="165" height="165">
        	</a>
            <h4 align='center'>
        		<a href='https://github.com/huakucha'>huakucha</a>
        	</h4>
        </td>
    </tr>
</table>

## 协议

<img alt="Apache-2.0 license" src="https://lucene.apache.org/images/mantle-power.png" width="128">

DoraemonKit 基于 Apache-2.0 协议进行分发和使用，更多信息参见 [协议文件](LICENSE)。

## 使用提醒
为了统计本开源软件的外部使用情况，我们会上传用户app的相关信息，包括appName、bundleId、appVersion。appVersion会在下个版本中去掉。
这些信息我们收集绝不用于任何恶意用途。

iOS上传代码详见DoraemonStatisticsUtil类中的实现

Andoid上传代码详见DoraemonStatisticsUtil类中的实现

敬请各位用户知晓。
