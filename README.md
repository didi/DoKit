- [简介](#简介)
- [领域生态](#领域生态)
- [使用手册](#使用手册)
- [更新日志](#更新日志)
- [社区活动](#社区活动)
- [开发背景](#开发背景)
- [功能模块](#功能模块)
  - [一、平台工具(www.dokit.cn)](#一平台工具wwwdokitcn)
  - [二、常用工具](#二常用工具)
  - [三、性能检测](#三性能检测)
  - [四、视觉工具](#四视觉工具)
  - [五、Weex专项工具（CML专项工具）](#五weex专项工具cml专项工具)
  - [六、支持自定义的业务工具集成到面板中](#六支持自定义的业务工具集成到面板中)
  - [七、微信小程序专项工具](#七微信小程序专项工具)
- [相关文档](#相关文档)
- [微信交流群(一群满员，已开二群)](#一群满员，已开二群)
- [QQ 交流群](#qq-交流群)
- [微信公众号](#微信公众号)
- [项目成员](#项目成员)
- [使用提醒](#使用提醒)
- [友情链接](#友情链接)
- [协议](#协议)

## 简介
<div align="center">    
 <img src="https://pt-starimg.didistatic.com/static/starimg/img/J0WO7tUi9U1616143930629.png" width = "250"  alt="DoraemonKit" align=left />
 <img src="https://img.shields.io/github/license/didi/DoraemonKit.svg" align=left />
 <img src="https://img.shields.io/badge/Android-3.5.0-blue.svg" align=left />
 <img src="https://img.shields.io/badge/iOS-3.0.7-yellow.svg" align=left />
 <img src="https://img.shields.io/badge/Flutter-0.6.0-blue.svg" align=left />
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

DoKit诞生于滴滴城运服体验技术部，是一款面向泛前端产品研发全生命周期的效率平台。经过两年的发展，当前DoKit已经发展成了一个相对完整的生态，比如DoKit For Android、DoKit For iOS、DoKit For 小程序、DoKit For Flutter、DoKit For Web。同时我们的项目被BAT以及滴滴、字节、快手、京东等等头部独角兽企业广泛使用并获得良好的口碑。随着dokit.cn平台端的推出，标志着DoKit已经从单纯的效率工具正式进入了效率工具平台的阶段。与此同时我们一直都未停下持续探索的精神，积极地在更多平台领域进行尝试，不给自己设限。我们相信DoKit的未来充满无限可能。

> [English Readme](README_EN.md)

## 领域生态

<div align="center">    
  <img src="https://pt-starimg.didistatic.com/static/starimg/img/WRUdiWDsw71626696344680.jpg" width = "900" alt="DoKit 首页效果演示" align=center />
</div>


## 使用手册
访问[DoKit官网](https://www.dokit.cn/)，点击"[使用中心](http://xingyun.xiaojukeji.com/docs/dokit/#/intro)"。

**温馨提示：当前DoKit的所有功能都只针对Debug环境，Release环境未经过实际验证，所以请大家严格按照官方文档来集成，也不建议大家在Release环境上使用DoKit的任何功能。如果大家一定要在Release环境上使用，请自行进行充分的测试和验证，DoKit官方将不承担任何责任和损失。** 
## 更新日志

- [Android-ReleaseNotes](http://xingyun.xiaojukeji.com/docs/dokit/#/AndroidReleaseNote)
- [iOS-ReleaseNotes](Doc/iOS-ReleaseNotes.md)
- [微信小程序-ReleaseNotes](Doc/miniapp-ReleaseNotes.md)
- [DoKit For Flutter-ReleaseNotes](Flutter/README.md)

## 社区活动

**DoKit调研问卷**
亲爱的DoKit用户,动动你的小手指参与一下我们的官方调研活动吧。我们极度渴望听到你们的声音:

链接:https://page.juyanwenjuan.com/jy_0CMpJzlu.html

<div align="center">    
  <img src="https://pt-starimg.didistatic.com/static/starimg/img/INLjGkp9wN1597062733948.jpg" width = "250" alt="DoKit 首页效果演示" align=center />
</div>



## 开发背景

每一个稍微有点规模的 App，总会自带一些线下的测试功能代码，比如环境切换功能、帧率查看功能等等，这些功能的切换入口往往放在各式各样的入口中，比如一些特殊的手势，双击 statusBar，双击某一个功能区块，或者新建一个 keyWindow 始终至于 App 最上方等等，而且每一个 App 里面的线下附带功能模块很多是相似的，比如帧率查看、内存和 CPU 监控等等，但是现在基本上都是每个 App 都是自己实现了一份，经历了以上的问题之后，DoKit 就有了它存在的意义。

DoKit 是一个功能平台，能够让每一个 App 快速接入一些常用的或者你没有实现的一些辅助开发工具、测试效率工具、视觉辅助工具，而且能够完美在 Doraemon 面板中接入你已经实现的与业务紧密耦合的一些非通有的辅助工具，并搭配我们的[dokit](https://www.dokit.cn)平台，让功能得到延伸，接入方便，便于扩展。

**简单总结**

1、DoKit 能够快速让你的业务测试代码能够在这里统一管理，统一收口；  

2、DoKit 内置很多常用的工具，避免重复实现，一次接入，你将会拥有强大的工具集合；

3、搭配dokit平台，借助[接口Mock](https://www.dokit.cn/#/index/dataMockPage)、[健康体检](https://www.dokit.cn/#/index/checkDataPage)、[文件同步助手](https://www.dokit.cn/#/index/fileSyncPage)、[一机多控]()让你方便和他人协同，极大的提升研发过程中的效率。



## 功能模块

### 一、平台工具(www.dokit.cn)
1. **【数据Mock】** App接口Mock解决方案，提供一套基于App网络拦截的接口Mock方案，无需修改代码即可完成对于接口数据的Mock。
2. **【健康体检】** 一键式操作，整合DoKit多项工具，数据可视化，快速准确定位问题，让你对app的性能了如指掌。
3. **【文件同步助手】** 通过终端服务，让你的终端空间在平台端完整的展现并提供强大的文件以及数据库操作能力。
4. **【一机多控】** 主从同步，释放人力，让研发测试效率提升看得见

### 二、常用工具

1. **【App 信息查看】** 快速查看手机信息，App 基础信息、签名相关、权限信息的渠道，避免去手机设置查找或者查看项目源代码的麻烦；
2. **【开发者选项 Android特有】** 一键跳转开发者选项，避免安卓由于平台差异导致的入口不一致
3. **【本地语言】** 一键跳转本地语言，避免安卓由于平台差异导致的入口不一致
4. **【沙盒浏览】** App 内部文件浏览的功能，支持删除和预览, 并且能通过 AirDrop 或者其他分享方式上传到 PC 中，进行更加细致的操作；
5. **【MockGPS】** App 能定位到全国各地，支持地图地位和手动输入经纬度；
6. **【H5任意门】** 开发测试同学可以快速输入 H5 页面地址，查看该页面效果；
7. **【Crash查看】** 方便本地打印出出现 Crash 的堆栈；
8. **【子线程UI】** 快速定位哪一些 UI 操作在非主线程中进行渲染，避免不必要的问题；（iOS独有）
9. **【清除本地数据】** 一键删除沙盒中所有数据；
10. **【NSLog】** 把所有 NSLog 信息打印到UI界面，避免没有开发证书无法调试的尴尬；
11. **【Lumberjack】** 每一条 CocoaLumberjack 的日志信息，都在在 App 的界面中显示出来，再也不需要导出日志这么麻烦;（iOS独有）
12. **【DBView】** 通过网页方便快捷的操作应用内数据库，让数据库的调试变得非常优雅;
13. **【模拟弱网】** 限制网速，模拟弱网环境下App的运行情况;（android独有）
14. **【JS脚本】** 在指定WebView运行JS脚本。（iOS独有）

### 三、性能检测

1. **【帧率】** App 帧率信息提供波形图查看功能，让帧率监控的趋势更加明显；
2. **【CPU】** App CPU 使用率信息提供波形图查看功能，让 CPU 监控的趋势更加形象；
3. **【内存】** App 内存使用量信息提供波形图查看功能，让内存监控的趋势更加鲜明；
4. **【流量监控】** 拦截 App 内部流量信息，提供波形图展示、流量概要展示、流量列表展示、流量筛选、流量详情，对流量信息统一拦截，成为我们 App 中自带的 "Charles"；
5. **【卡顿】** 锁定 App 出现卡顿的时刻，打印出对应的代码调用堆栈；
6. **【大图检测】** 通过流量监测，找出所有的大小超标的图片，避免下载大图造成的流量浪费和渲染大图带来的CPU消耗。
7. **【启动耗时】** 无侵入的统计出App启动过程的总共耗时；
8. **【UI层级检查】** 检查出每一个页面中层级最深的元素；
9. **【函数耗时】** 从函数级别分析app性能瓶颈；
10. **【Load】** 找出所有的Load方法，并给出耗时分析；（iOS独有）
11. **【内存泄漏】** 找出App中所有的内存泄漏的问题。

### 四、视觉工具

1. **【颜色吸管】** 方便设计师 UI 捉虫的时候，查看每一个组件的颜色值是否设置正确；
2. **【组件检查】** 可以抓取任意一个UI控件，查看它们的详细信息，包括控件名称、控件位置、背景色、字体颜色、字体大小；
3. **【对齐标尺】** 参考 Android 系统自带测试工具，能够实时捕获屏幕坐标，并且可以查看组件是否对齐；
4. **【元素边框线】** 绘制出每一个 UI 组件的边框，对于组件布局有一定的参考意义。

### 五、Weex专项工具（CML专项工具）

1. **【console日志查看】** 方便在端上查看每一个Weex文件中的console日志，提供分级和搜索功能；
2. **【storage缓存查看】** 将Weex中的storage模块的本地缓存数据可视化展示；
3. **【容器信息】** 查看每一个打开的Weex页面的基本信息和性能数据；
4. **【DevTool】** 快速开启Weex DevTool的扫码入口。

tips ： 如果使用我们滴滴优秀的开源跨端方案 [chameleon](https://github.com/didi/chameleon) 也可以集成该工具集合

### 六、支持自定义的业务工具集成到面板中

统一维护和管理所有的测试模块，详见接入手册

### 七、微信小程序专项工具

详见 [Doraemon mini program debugger](https://github.com/didi/DoraemonKit/tree/master/miniapp)



## 相关文档
- [iOS 研发助手 DoKit 技术实现（一）](https://www.jianshu.com/p/00763123dbc4)
- [iOS 研发助手 DoKit 技术实现（二）](https://blog.csdn.net/weixin_33847182/article/details/91472599)
- [DoKit支持iOS本地crash查看功能](https://juejin.im/post/5d76184ce51d4561d106cc65)
- [开源组件 DoKit 之 Android 版本技术实现（一）](https://blog.csdn.net/weixin_33711647/article/details/88004429)
- [开源组件 DoKit 之 Android 版本技术实现（二）](https://blog.csdn.net/weixin_34148456/article/details/88004414)
- [DoKit支持Activity启动耗时统计方案](https://juejin.im/post/5d70bc3051882571ed61e407)
- [DoKit 微信小程序SDK对外发布](https://juejin.im/post/5d9bf252518825095c3c5e32)
- [滴滴DoKit2.0 - 泛前端开发者的百宝箱](https://juejin.im/post/5dc3cdfa51882538d22d5948)
- [滴滴正式发布开源客户端研发助手 DoKit 3.0，新特性解读](https://juejin.im/post/5e818e29f265da4804697d25)
- [滴滴DoKit Android核心原理揭秘之函数耗时](https://juejin.im/post/5eba5ce15188256d6f268c98)
- [滴滴DoKit Android核心原理揭秘之AOP字节码实现](https://juejin.im/post/6891887877613944840)



## QQ 技术交流群

<div align="center">    
 <img src="https://dpubstatic.udache.com/static/dpubimg/z73LRuuFdxTmFLJq8c2qt_WechatIMG483.jpeg" width = "225" height = "400" alt="QQ 交流群" align=left />
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

**创始人**
[yixiangboy(易翔)](https://github.com/yixiangboy)
**负责人**
[小枫](https://github.com/bbssyyuui) 

**内部核心成员**
[小枫](https://github.com/bbssyyuui) 、
[ChasonTang](https://github.com/ChasonTang) 、
[AdamCaoQAQ](https://github.com/AdamCaoQAQ) 、
[fangyeqing123](https://github.com/fangyeqing123) 、
[RealOnlyone](https://github.com/RealOnlyone) 、
[HeyCFarmer](https://github.com/HeyCFarmer) 、
[yFeii](https://github.com/yFeii) 、
[卡布达](https://github.com/kabda) 、
[maxiee](https://github.com/maxiee) 、
[zhugeafanti](https://github.com/zhugeafanti)

**贡献者榜单**
[yixiangboy(易翔)](https://github.com/yixiangboy)、
[jtsky(金台)](https://github.com/jtsky) 、
[LinJZong](https://github.com/LinJZong) 、
[wanglikun7342](https://github.com/wanglikun7342) 、
[jayconscious](https://github.com/jayconscious) 、
[jellybean](https://github.com/excitedhaha) 、
[linusflow](https://github.com/xu984386604) 、
[wangzhipeng](https://github.com/WangLao100)、
[BzCoder](https://github.com/BzCoder) 、 
[changzuozhen](https://github.com/changzuozhen)、
[momoxiangbei](https://github.com/momoxiangbei)、
[wenquanlebao](https://github.com/wenquanlebao) 、 
[hiXgb](https://github.com/hiXgb) 、 
[Chinnko](https://github.com/Chinnko) 、 
[y644938647](https://github.com/y644938647) 、
[wm219](https://github.com/wm219)、 
[goolong](https://github.com/goolong) 、
[miracle9312](https://github.com/miracle9312) 、
[lwhsgz123](https://github.com/lwhsgz123)、
[huakucha](https://github.com/huakucha) 、
[HuginChen](https://github.com/HuginChen) 、
[feng562925462](https://github.com/feng562925462) 、
[azhon](https://github.com/azhon) 、
[rex26](https://github.com/rex26) 、
[csc-EricWu](https://github.com/csc-EricWu) 、
[xiandanin](https://github.com/xiandanin) 、
[0xd-cc](https://github.com/0xd-cc) 、
[k373379320](https://github.com/k373379320) 、
[fabcz](https://github.com/fabcz) 、
[y500](https://github.com/y500) 、
[Knight-ZXW](https://github.com/Knight-ZXW) 、
[boai](https://github.com/boai) 、
[klone1127](https://github.com/klone1127) 、
[DeveloperLY](https://github.com/DeveloperLY) 、
[sagdragon](https://github.com/sagdragon) 、
[ccworld1000](https://github.com/ccworld1000) 、
[HDB-Li](https://github.com/HDB-Li)、
[yu-jianfeng](https://github.com/yu-jianfeng)、
[ydlsl](https://github.com/ydlsl)

如何成为外部贡献者？ 提交有意义的PR，并被采纳。


## 使用提醒
因为SDK目前会配合[dokit.cn](http://www.dokit.cn/)平台, 会产生一些网络数据，这些信息我们收集绝不用于任何恶意用途。

**以下为所有涉及到网络请求的部分**

1. 统计有多少用户集成了dokit

    Android : DoraemonStatisticsUtil#uploadUserInfo

    iOS : DoraemonStatisticsUtil#upLoadUserInfo

2. 统计每个内置kit的使用情况

    Android : DataPickManager#realPost 

    iOS : DoraemonBuriedPointManager#uploadData

3. 上传健康体检的相关数据

    Android : AppHealthInfoUtil#post

    iOS : DoraemonHealthManager#upLoadData

4. 数据mock的相关网络请求

    Android : NetWorkMockFragment 里涉及到接口mock的相关网络请求
    
    iOS : DoraemonMockManager#queryMockData&uploadSaveData


敬请各位用户知晓。



## 友情链接
1. [Hummer](https://github.com/didi/Hummer)，Hummer 是一套高性能高可用的跨端开发框架，一套代码可以同时支持开发 Android 和 iOS 应用。现已经支持 Vue/TypeScript/JavaScript 三种语法，面向大前端开发人员，总有一款适合你。

2. [Chameleon]( https://github.com/didi/chameleon)，简写CML，中文意思变色龙，意味着就像变色龙一样能适应不同环境的跨端整体解决方案，达到真正意义上"一套代码，多端运行"的终极目标
   
3. [Booster](https://github.com/didi/booster) 是一款专门为移动应用设计的易用、轻量级且可扩展的质量优化框架，其目标主要是为了解决随着 APP 复杂度的提升而带来的性能、稳定性、包体积等一系列质量问题。Booster 提供了性能检测、多线程优化、资源索引内联、资源去冗余、资源压缩、系统 Bug 修复等一系列功能模块，可以使得稳定性能够提升 15% ~ 25%，包体积可以减小 1MB ~ 10MB。**同时DoKit插件的底层也是基于Booster进行开发的。**

4. [AoE](https://github.com/didi/AoE)，一个终端侧AI集成运行时环境 
   
5. [Mand Mobile](https://github.com/didi/mand-mobile) 一款优秀的面向金融场景的 移动端UI组件库
   

6. 我们团队的技术公众号【滴滴OrangeLab】，欢迎关注，我们会在这里持续输出团队内部比较有深度的技术沉淀和经验分享，欢迎一起交流。
<img src="https://pt-starimg.didistatic.com/static/starimg/img/cPlaMme4g81616682162304.jpg" width = "250" />

## 协议

<img alt="Apache-2.0 license" src="https://www.apache.org/img/ASF20thAnniversary.jpg" width="128">

DoKit 基于 Apache-2.0 协议进行分发和使用，更多信息参见 [协议文件](LICENSE)。
