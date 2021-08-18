## 介绍
一款面向前端生态的开发工具
### 愿景
让前端开发更美好

### 功能

#### 功能一览
<div align=center>
  <img width="200" src="https://pt-starimg.didistatic.com/static/starimg/img/rxidkzyycP1628441881971.png">
</div>

#### 日志

展示 `Console` 控制台的日志
<div align=center>
  <img width="200" src="https://pt-starimg.didistatic.com/static/starimg/img/eav7Ts12nH1628441945429.png">
</div>

##### 基础功能
- 支持所有的 `console` 方法
- 支持日志的筛选和清除
- 支持执行简易的 `javascript`命令
#### 接口抓取
展示所有的数据请求
<div align=center>
  <img width="200" src="https://pt-starimg.didistatic.com/static/starimg/img/ROL9A3CvOH1628442010241.png">
</div>

##### 基础功能
- 支持抓取常用的接口类型 `xhr` 和 `fetch`
- 展示请求的详细内容
- 展示响应的详细内容
#### 资源查看
展示页面的所有资源请求
<div align=center>
  <img width="200" src="https://pt-starimg.didistatic.com/static/starimg/img/KpRqRxDbBb1628442052029.png">
</div>

##### 基础功能
- 展示`javascript`资源
- 展示`css`资源
- 展示`image`等媒体资源

#### 应用存储
展示当前页面的存储信息

<div align=center>
  <img width="200" src="https://pt-starimg.didistatic.com/static/starimg/img/60jC26gBJI1628442340277.png">
</div>

##### 基础功能
- 支持查看并修改 `LocalStorage`
- 支持查看并修改 `SessionStorage`
- 支持查看 `Cookie`

#### 接口Mock
无侵入式 Mock 数据
<div align=center>
  <img width="200" src="https://pt-starimg.didistatic.com/static/starimg/img/t1qIRaZYeb1628443351551.png">
</div>

> 在我们的[平台端](https://www.dokit.cn/)注册，即可使用该功能，在sdk接入部分会有详细介绍。
点击工具中的数据模拟即可进入详情页，其中详情页分为mock数据和上传模板(开发中)两块功能。

> **mock数据:** 你可以通过打开指定接口的开关并选择相应的场景,此时你无需改变你的网络请求代码即可对你的指定接口进行拦截并返回你在平台创建的接口数据。

> **上传模板:** 上传模板功能的适用场景是当你已经有了一个真实的接口，需要针对不同的用户场景进行测试但是同时接口返回的数据量比较庞大，所以我们为你提供了上传模板的功能。当你打开上传模板中指定接口的开关时，我们会拦截并保存你真实接口返回的数据并提供json预览功能。点击上传即可上传你的模板数据到Dokit平台端。
##### 基础功能
- 支持无侵入式一键 Mock
- 支持自动同步真实请求数据到平台侧，并与快速创建


#### H5任意门
通过输入连接的方式从当前页面跳往任意页面，可快速添加和编辑页面query参数，同时可以缓存跳转历史
<div align=center>
  <img width="200" src="https://pt-starimg.didistatic.com/static/starimg/img/GjeLt3ykHO1628444700515.png">
</div>

#### 应用信息
查看与当前页面有关的应用信息
<div align=center>
  <img width="200" src="https://pt-starimg.didistatic.com/static/starimg/img/EnksDhWPtb1628443757848.png">
</div>

#### 页面标尺
利用可移动标尺查看元素位置及元素对齐情况
<div align=center>
  <img width="200" src="https://pt-starimg.didistatic.com/static/starimg/img/TNCHHhBWky1628443923215.png">
</div>

#### 屏幕录制 & 回放
> 功能设计中

#### 自动化测试
> 功能设计中

#### 如何接入DoKit For Web

在项目中引入vue3和dokit的cdn即可使用dokit相关基础功能，我们会自动实例化DoKit实例并挂载在全局上
如需使用平台侧增强功能如数据mock等功能则需要调用setProductId这个方法来设置productId
```html
<script src="https://unpkg.com/vue@next"></script>
<script src="https://cdn.jsdelivr.net/npm/@dokit/web@0.0.1-alpha.1/dist/dokit.min.js" type="text/javascript" charset="utf-8"></script>

<script>
  Dokit.setProductId('你的product id')
</script>
```


#### 自定义业务专区


通用的开发工具肯定无法满足各种各样的业务开发需要

所以我们提供了较为便捷的方式能够让前端同学能够定制属于自己的Dokit开发工具
你可以在本地clone DoKit项目，在Web模块下进行定制化开发，在DoKit For Web
中我们将整个项目分成三个模块：core/utils/web,利用lerna进行开发管理


- core主要提供了框架初始化能力，集成了简单的路由和状态管理功能
- utils中主要是一个工具方法和公共能力支持，如事件订阅发布、网络、拖拽等
- web中主要是对具体功能组件的实现，我们提供了两种组件/插件，第一种是基于自定义路由的依托于DoKit主容器的类路由view插件（日志、网络请求等），另一种是脱离DoKit主容器的独立插件（标尺对齐）

当你需要自定义一个业务工具是，你只需要进行三步即可构建属于你自己的Dokit
1. 在web文件夹的plugin中穿件一个新的plugin,然后实现你自己的vue组件
2. 在文件夹中添加一个index.js，用于将vue组件导出为路由插件/独立插件
3. 在web文件夹下的feature.js中添加你的插件
经过上面三步你就可以将你的插件集成进DoKit,利用我们提供的构建工具，可以在本地预览和调试，然后进行项目打包，将最终dist产物发布到npm后你就可以利用npm提供的cdn能力引入你的专属DoKit了

我们来实战一下，分别添加一个RouterPlugin和一个IndependPlugin，

我们写一个简单的vue组件只包含简单的视图和文字
<div align=center>
  <img src="https://pt-starimg.didistatic.com/static/starimg/img/Q3bQDwVGYX1628761222487.png">
</div>
然后将组件导出为路由插件
<div align=center>
  <img src="https://pt-starimg.didistatic.com/static/starimg/img/qluMSP6wlO1628761222376.png">
</div>

再写一个支持拖拽的可移动视图
<div align=center>
  <img src="https://pt-starimg.didistatic.com/static/starimg/img/bvka6ncI781628761228269.png">
</div>
将其导出为独立插件
<div align=center>
  <img src="https://pt-starimg.didistatic.com/static/starimg/img/QnBm6hKD6s1628761225271.png">
</div>
然后我们在feature.js中引入并声明这两个插件
<div align=center>
  <img src="https://pt-starimg.didistatic.com/static/starimg/img/ZFGI8KVTFG1628761226052.png">
</div>
启动dev服务，我们可以看到预览页面的DoKit中多了两个工具


<div align=center>
  <img width="200"  src="https://pt-starimg.didistatic.com/static/starimg/img/HnvW7LUw8u1628761227192.png">
</div>
第一个就是我们写的简单的仅包含文字的组件
<div align=center>
  <img width="200"  src="https://pt-starimg.didistatic.com/static/starimg/img/kG9oNBEaTS1628761227928.png">
</div>
第二个则是一个脱离了DoKit主容器的独立可拖拽插件
<div align=center>
  <img width="200"  src="https://pt-starimg.didistatic.com/static/starimg/img/eeugDnQgjw1628761960638.png">
</div>





# 贡献

有任何意见或建议都欢迎提 issue

# github地址

https://github.com/didi/DoraemonKit 欢迎star

