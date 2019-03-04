# Release Notes
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