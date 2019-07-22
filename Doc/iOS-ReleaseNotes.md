# Release Notes

### 1.2.1
1、大图检测功能[0xd-cc](https://github.com/0xd-cc)

2、修复元素边框线crash的问题

3、添加应用内数据库调试功能 [y500](https://github.com/y500)

4、横屏适配bug fix

5、支持下CocoaPods1.7 的generate_multiple_pod_projects新特性

6、多次执行install方法保护，避免出现多次初始化UI界面出错

### 1.2.0
1. 加入Weex专项工具，包括：
2. **【console日志查看】** 方便在端上查看每一个Weex文件中的console日志，提供分级和搜索功能
3. **【storage缓存查看】** 将Weex中的storage模块的本地缓存数据可视化展示
4. **【容器信息】** 查看每一个打开的Weex页面的基本信息和性能数据
5. **【DevTool】** 快速开启Weex DevTool的扫码入口

### 1.1.9
1、H5任意门支持扫码和历史浏览记录功能 [feng562925462](https://github.com/feng562925462)

2、支持使用block的方式接入自定义测试模块 [csc-EricWu](https://github.com/csc-EricWu/)

3、卡顿监控支持查看卡顿时长 [k373379320](https://github.com/k373379320)

4、沙盒浏览，除了数据库文件之外，其他文件修改成 QLPreviewController 打开 [HuginChen](https://github.com/HuginChen)

5、支持framework直接接入，方便非pod管理的项目

6、性能曲线图代码重构,支持同时查看fps、内存、cpu和流量的信息。

7、解决toast在某一些情况无法显示的问题,改善用户体验

8、其他一些优化和代码整理，详见代码提交记录


### 1.1.8
1、支持国际化，中英文

2、适配横屏 @ [Hugin](https://github.com/HuginChen)

3、修复强持有 CLLocationManagerDelegate 不释放的问题 @[li6185377](https://github.com/li6185377)

4、iOS沙盒浏览器优化 @[csc-EricWu](https://github.com/csc-EricWu/)

5、支持主动控制隐藏显示DoKit入口

6、app信息页面 权限cell可以直接跳转到权限设置页面进行设置

7、其他一些优化和代码整理，详见代码提交记录


## 1.1.7
1、支持数据库文件本地预览功能

2、支持word、pdf、html文件本地预览功能

3、解决ipad上crash问题

4、解决低版本手机适配问题

5、mockGPS更加通用

6、其他的一些issues解决和PR合并，详见代码提交记录


## 1.1.6
1、去掉对于AFNetworking的多余依赖

2、颜色拾取器优化，方便更加准确的选取字体的颜色

3、大文本显示的时候，UIlabel在模拟器上会显示空白，使用TextView代替

4、pluginDidLoad回调，将itemData返回

5、解决和JGProgressHUD的layoutSubviews 循环调用的问题

6、适配iOS9状态栏不显示的问题

7、DoraemonLoadAnalyze改成可选的pod依赖

## 1.1.5
1、更新DoraemonLoadAnalyze.framework支持bitcode

2、修复 iOS11以下版本点击沙盒浏览功能崩溃

3、修复 NSLog 未适配iPhone X以上机型

4、流量详情的每一个cell中的数据支持复制

5、其他一些优化和bug fix，详见代码提交记录

## 1.1.4
1、新增Load函数耗时检测功能

2、新增元素边框线功能

3、H5任意门添加默认跳转实现

4、适配XR、XS、XS Max

5、其他一些优化和bug fix，详见代码提交记录


## 1.1.3
1、DorameonKit界面不占用keyWindow

2、https强制信任，解决某一些情况下https的请求无法拦截的问题

3、点击空白处收起键盘

4、流量监控支持WKWebView

## 1.1.2
1、沙盒浏览器支持多媒体资源预览

2、修复沙盒浏览器文件过长显示不全的问题

3、重建文件目录，把Demo和仓库都放在iOS目录下面。兼容andorid版本的到来

## 1.1.1
1、解决swift项目，编译不过的问题。

## 1.1.0
1、体验优化、UI大改版。

2、沙盒浏览器支持文件大小查看。

3、支持crash本地查看

4、新增清楚本地数据功能

5、新增NSLog客户端显示

6、新增卡顿检测

## 1.0.4
1、UIView_Positioning重命名

2、解决获取图片方法错误


## 1.0.3
1、BSBacktraceLogger改为pod依赖


## 1.0.2
1、UIView+Positioning分类加前缀


## 1.0.1
1、新增子线程UI检查操作

2、权限信息查看更多

3、完善网络测试Demo

4、沙盒浏览器支持文件删除


## 1.0.0

1、第一个版本上线。

2、通用工具：App信息、沙盒浏览、MockGPS、H5任意门、日志显示

3、性能工具：帧率、CPU、内存、流量、自定义

4、视觉工具：颜色吸管、组件检查、对齐标尺