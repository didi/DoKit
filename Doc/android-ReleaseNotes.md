DoraemonKit-Android releases
===

## 3.5.0.1

1、bug fixed

2、DoKit For Android Kotlin 编译插件版本为1.3.72，支持Gradle 6.8及以下版本

3、相关功能对应3.5.0

## 3.5.0

1、bug fixed

2、DoKit For Android Kotlin 编译插件版本为1.4.32，支持Gradle 6.8及以上版本


## 3.5.0.1-beta01

1、Support AGP 7.0

2、DoKit For Android Kotlin 编译插件版本为1.3.72，支持Gradle 6.8及以下版本

3、相关功能对应3.5.0-beta01

## 3.5.0-beta01

1、Support AGP 7.0

2、DoKit For Android Kotlin 编译插件版本为1.4.32，支持Gradle 6.8及以上版本


## 3.4.3.1

1、DoKit For Android Kotlin 编译插件版本为1.3.72，支持Gradle 6.8及以下版本

2、相关功能对应3.4.3

## 3.4.3

1、DoKit For Android Kotlin 编译插件版本为1.4.32，支持Gradle 6.8及以上版本

2、Bug Fixed


## 3.4.2.1

1、DoKit For Android Kotlin 编译插件版本为1.3.72，支持Gradle 6.8及以下版本

2、相关功能对应3.4.2

## 3.4.2

1、DoKit For Android Kotlin 编译插件版本为1.4.32，支持Gradle 6.8及以上版本

2、一机多控新增数据录制功能，主要解决页面一致性的问题

3、一机多控新增自定义事件接口和Client端统一回调，主要解决特殊控件手势等自定义长连接事件。

4、DoKit 对外API收敛

5、Bug Fixed


## 3.4.0-alpha03

1、去掉kotlin-android-exensions插件

2、开放dokit性能数据(cpu、fps、内存、网络)，通过入口函数callback接口返回，具体参考android接入文档

3、新增debug环境okhttp 扩展拦截器，通过入口函数netExtInterceptor设置，具体参考android接入文档


## 3.4.0-alpha02

由于jcenter事件的影响，我们需要将DoKit For Android迁移到mavenCentral，但是需要更改groupId.所以大家要注意一下，具体的更新信息如下：

1、变更GroupId(io.github.didi.dokit)

 //核心模块

debugImplementation "io.github.didi.dokit:dokitx:3.4.0-alpha02"

//文件同步模块

debugImplementation "io.github.didi.dokit:dokitx-ft:3.4.0-alpha02"

//一机多控模块

debugImplementation "io.github.didi.dokit:dokitx-mc:3.4.0-alpha02"

//weex模块

debugImplementation "io.github.didi.dokit:dokitx-weex:3.4.0-alpha02"

//no-op 模块

releaseImplementation "io.github.didi.dokit:dokitx-no-op:3.4.0-alpha02"

2、适配AS4.2

3、去掉AndroidUtil库，减少编译冲突

4、新增LBS专区

5、DoKit 悬浮主Icon更新

6、其他github issuebug修复

**tips:**

**1、v.3.3.5及以前的版本还是在jcenter维护，所以无需变更GroupId，3.4.0及以后的版本需要将GroupId变更为：io.github.didi.dokit**

**2、需要添加mavenCentral()仓库**

**3、由于本次内部框架变更比较多，所以当前是alpha02版本，欢迎大家给我提Bug**


## 3.3.5（dokitx 对应androidx，dokit对应Android Support）

1.为了更好的支持android官方androidx和support，dokit从3.3.1版本开始正式对sdk名字进行更新。具体如下：

androidx===>com.didichuxing.doraemonkit:dokitx:3.3.5;

support===>com.didichuxing.doraemonkit:dokit:3.3.5;

2.修复与booster的冲突。相关issues:https://github.com/didi/booster/issues/202


## 3.3.4（dokitx 对应androidx，dokit对应Android Support）

1.为了更好的支持android官方androidx和support，dokit从3.3.1版本开始正式对sdk名字进行更新。具体如下：

androidx===>com.didichuxing.doraemonkit:dokitx:3.3.4;

support===>com.didichuxing.doraemonkit:dokit:3.3.4;

2.兼容AS Gradle v4.1.0

3.dokit v3.3.4插件底层依赖库Booster升级到 v3.0.0版本


## 3.3.3（dokitx 对应androidx，dokit对应Android Support）

1.为了更好的支持android官方androidx和support，dokit从3.3.1版本开始正式对sdk名字进行更新。具体如下：

androidx===>com.didichuxing.doraemonkit:dokitx:3.3.3;

support===>com.didichuxing.doraemonkit:dokit:3.3.3;

2.新增三方依赖库搜索

3.解决H5助手崩溃的问题

4.兼容booster，dokit v3.3.3插件底层依赖booster v2.3.0版本



## 3.3.1（dokitx 对应androidx，dokit对应Android Support）
1.为了更好的支持android官方androidx和support，dokit从3.3.1版本开始正式对sdk名字进行更新。具体如下：

androidx===>com.didichuxing.doraemonkit:dokitx:3.3.1;

support===>com.didichuxing.doraemonkit:dokit:3.3.1;

2.新增H5助手功能:针对webview中的js请求进行数据Mock和抓包

3.新增三方库信息:将项目中依赖的三方库进行统一收集展现。

4.兼容okhttp v3.+ 以及 v4.+

5.大量github issues 问题修复。

## 3.2.0 & 3.0.8.0
1.文件同步助手功能已推出。
详细文档参考：
http://xingyun.xiaojukeji.com/docs/dokit/#/FileList

2.github issues fixed

## 3.1.8 & 3.0.7.2
1.适配AGP 4.0

2.github issues fixed

## 3.1.6
1.更换dokit的底层插件框架为[booster](https://github.com/didi/booster),兼容性和编译速度得到显著提升

2.github issues fixed

## 3.0.6
1.对齐androidx dokit v3.1.5 版本功能


## 3.1.5
1.更加精细化的插件控制(具体的插件配置信息可参考www.dokit.cn Android接入指南)

2.新增用户自定义管理kit和自定义业务专区分组api(具体的插件配置信息可参考www.dokit.cn Android接入指南)

3.核心Api升级成kotlin，同时欢迎大家和我一起参与dokit kotlin改造

4.github issues fixed

## 3.1.4
1.DoKit项目结构调整,对外暴露的api已全部移到com.didichuxing.doraemonkit.kit.core包名下,如果你有自定义Kit，请重新引入一下包名

2.修复okhttp拦截器被重复调用的问题

3.github issues fixed

## 3.1.3
1.DoKit项目结构调整,对外暴露的api已全部移到com.didichuxing.doraemonkit.kit.core包名下,如果你有自定义Kit，请重新引入一下包名

2.DoKit数据Mock模块支持post模式匹配

3.github issues fixed

## 3.1.2
1.安卓版本DoKit从3.1.0版本开始全面拥抱Androidx,假如你的项目还没有升级到androidx你可以选择依赖3.0.3版本

2.修复大图检测圆角失效的问题

## 3.1.1
1.修复网络工具里由于androidx的ViewPage包名不一致导致的进入页面崩溃的bug

2.修复其他github issues

## 3.1.0
1.安卓版本DoKit从3.1.0版本开始全面拥抱Androidx,假如你的项目还没有升级到androidx你可以选择依赖3.0.2版本

## 3.0.5
1.该版本为支持Android support的最后一个版本，后期也将不在提供维护，请大家尽快升级和适配Androidx

2.DoKit数据Mock模块支持post模式匹配

3.github issues fixed

## 3.0.3
1.修复大图检测圆角失效的问题

## 3.0.2
1.安卓版DoKit从3.0.2版本开始将逐渐放弃对Android Support版本的支持，请大家全面拥抱androidx吧

## 3.0.0
1.优化dokit 慢函数代码插件性能

2.优化大图检测实现方式，不再需要手动注入代码(支持Glide4.0+、Fresco、Picasso、ImageLoader)

3.优化安卓端启动性能

4.其他功能优化

## 3.0_beta3
1.优化dokit插件

## 3.0_beta2
1.新增日志一键导出功能

2.bug fix

## 3.0_beta1
1.新增2个平台端功能 接口mock和健康体检

2.优化内部架构，提升性能和代码稳定性


## 2.2.2

1. 更新视图检查工具，展示符合当前页面位置条件的所有视图


## 2.2.1

1. 升级 AOP 方案，原先采用Aspectj，现已升级为ASM方案，提升编译性能和兼容性
   
2. 新增高德、腾讯、百度的经纬度模拟功能
   
3. 合并pr以及大量issue解决


## 2.0.1

1. 修复 Dokit 在 EMUI9.1 等少数系统上存在的兼容性问题

## 2.0.0

1. 修复 V2.0.0 前置试用版 V1.2.8 的相关 issues, 先正是推出 V2.0.0 版本，请大家升级使用

## 1.2.8

1. 解决 app_name 被覆盖的问题

## 1.2.7

1. 新增 `LeakCanary`
2. 新增 DBView
3. 修改悬浮窗模式
4. 优化性能

## 1.2.1

1. 位置模拟某些场景不可用修复
2. 性能监测 Crash 修复

## 1.2.0

1. Crash 记录支持分享导出
2. 重启 APP 后模拟定位依然生效
3. 网络库代码重构
4. RN 页面布局层级功能 Crash 修复
5. 提供设置手动申请悬浮窗权限的能力

## 1.1.8

1. Crash 捕获功能重构，改为私有存储，交互体验优化
2. 页面启动耗时兼容 __Android P__
3. 问题修复，稳定性优化

## 1.1.7

1. 新增模拟弱网功能
2. 帧率统计改为固定时间帧数法，结果更稳定平滑
3. 问题修复，稳定性优化

## 1.1.6

1. 日志查看功能优化，更好的显示体验
2. 沙盒浏览支持修改 sharedprefs 文件
3. 反馈问题修复，包括文件删除后列表无刷新等
4. 稳定性优化

## 1.1.5

1. 沙盒浏览支持视频格式，支持文件删除
2. 位置模拟支持地图选取经纬度
3. 缓存清理增加缓存大小显示，交互优化
4. H5 任意门增加默认实现
5. 其他反馈的问题修复，稳定性优化

## 1.1.3

1. 优化数据库预览的用户体验
2. 修复内存实时图表显示异常
3. 优化位置模拟功能的交互体验
4. 其他反馈的问题修复

## 1.1.2

1. no-op 版本增加悬浮窗打开、关闭方法
2. 增加了 App 启动耗时、页面跳转耗时的统计功能
3. 增加了数据库文件的可视化查看功能
4. 其他 bug 修复

## 1.1.1

1. 体验优化
2. bug fix

## 1.1.0

1. 优化 CPU 和 RAM 数据读取，支持 8.0 以上系统
2. 优化取色器功能，取色功能更准确
3. mockGPS 兼容高精度定位模式
4. 视觉工具新增布局边框、布局层级
5. 体验细节优化

## 1.0.5

1. 提供 no-op 版本
2. 体验优化

## 1.0.1

1. 修复资源冲突与覆盖导致的编译问题

## 1.0.0

1. 常用工具：App 信息、文件浏览、位置模拟、H5 任意门、日志显示、Crash 查看、缓存清理
2. 性能监控：帧率、CPU、RAM、网络流量、卡顿检测
3. 视觉工具：取色器、控件检查、对齐标尺

