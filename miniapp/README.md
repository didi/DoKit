# DoKit微信小程序研发助手SDK升级

## Doraemon mini program debugger

一个支持小程序端的调试工具

# 背景

对于小程序开发者和测试同学来说，很多临时性的调试功能需要单独开发去支持，比如查看小程序信息，手机信息以及用户信息，扫码打开页面等。这些功能对于每个小程序都是相似的，而且遇到类似的需求时都需要进行单独开发。DoKit在移动端发展，获得了众多开发者的好评，其中不乏很多一线大厂(阿里，字节，腾讯，百度...)的身影，同时给我们带来了很多宝贵的经验。在广大开发者的要求下，我们重新启动了小程序端sdk的升级.

此次版本升级主要提供了数据模拟功能，优化接入流程，降低用户接入成本，更好配合使用原生小程序以及第三方框架开发，提升开发同学的幸福感。

# 简单总结

DoKit小程序端调试工具，内置很多常用的工具，避免重复实现，一次接入，你将会拥有强大的工具集合。

# 新增功能演示

哆啦A梦小程序端apimock功能演示

<div align=center>
  <img width="200" height="350" src="https://pt-starimg.didistatic.com/static/starimg/img/4yXktFAckF1589275220286.png">
</div>

> 在我们的[平台端](https://www.dokit.cn/)注册，即可使用该功能，在sdk接入部分会有详细介绍。

<div align=center>
  <img width="200" height="350" src="https://pt-starimg.didistatic.com/static/starimg/img/YfLpeuu2MH1590650509070.png">
  <img width="200" height="350" src="https://pt-starimg.didistatic.com/static/starimg/img/HDibtAjCqq1590651365296.png">
</div>

点击工具中的数据模拟即可进入详情页，其中详情页分为mock数据和上传模板两块功能。

> **mock数据:** 你可以通过打开指定接口的开关并选择相应的场景,此时你无需改变你的网络请求代码即可对你的指定接口进行拦截并返回你在平台创建的接口数据。

> **上传模板:** 上传模板功能的适用场景是当你已经有了一个真实的接口，需要针对不同的用户场景进行测试但是同时接口返回的数据量比较庞大，所以我们为你提供了上传模板的功能。当你打开上传模板中指定接口的开关时，我们会拦截并保存你真实接口返回的数据并提供json预览功能。点击上传即可上传你的模板数据到Dokit平台端。

> **实现原理:** 使用Object.defineProperty()劫持小程序的wx.request(),然后重写次方法，添加上url匹配拦截逻辑和上传模板逻辑。在平台端更新接口时，会和小程序本地数据合并，即还原原先在本地操作的记录。

# 其他功能
- app信息

<div align=center>
  <img width="200" height="350" src="https://pt-starimg.didistatic.com/static/starimg/img/Eu9DFjS6cf1590657590624.png">
  <img width="200" height="350" src="https://pt-starimg.didistatic.com/static/starimg/img/hHlPfUAPgu1590657697205.png">
</div>

> 用于快速查看手机系统信息，小程序基本信息，用户信息，授权信息等基础信息，避免反复打开手机设置或者调用小程序原生api进行查看。

- 位置模拟

<div align=center>
  <img width="200" height="350" src="https://pt-starimg.didistatic.com/static/starimg/img/zFDfYgfTFJ1590659245437.png">
  <img width="200" height="350" src="https://pt-starimg.didistatic.com/static/starimg/img/jYGvr0fsyI1590659269350.png">
</div>

> 用于小程序端位置模拟，包括位置授权，位置查看，位置模拟，恢复位置设置等几大功能，可以通过简单的点击操作实现任意位置模拟和位置还原，该功能的实现原理是通过对wx.getLocation进行方法重写，进而进行位置模拟，位置模拟后，在小程序内所有调用位置查询的方法内都将返回你设定的位置，还原后将恢复原生方法

- 缓存管理


<div align=center>
  <img width="200" height="350" src="https://pt-starimg.didistatic.com/static/starimg/img/MUMXoRhSFl1590667273847.png">
  <img width="200" height="350" src="https://pt-starimg.didistatic.com/static/starimg/img/GflYwbHHXO1590667273704.png">
  <img width="200" height="350" src="https://pt-starimg.didistatic.com/static/starimg/img/MM53oF1Wtj1590667274519.png">
  <img width="200" height="350" src="https://pt-starimg.didistatic.com/static/starimg/img/21JTvLDLp71590667274523.png">
</div>

> 一个强大的缓存管理面板，集成了对缓存的所有操作功能，包括设置缓存，清除缓存，更新缓存值等，可以在小程序非常便利的进行缓存管理

- H5任意门
<div align=center>
  <img width="200" height="350" src="https://pt-starimg.didistatic.com/static/starimg/img/ZWGSKwm2ZJ1590667512289.png">
</div>

> 可以通过扫码和粘贴链接的方式在小程序中打开h5页面，操作简单方便

- 更新版本

> 当你的小程序进行代码更新时，为了获取最新的线上包需要重启小程序，该功能可以在你的小程序上通过点击更新操作，直接获取到最新的远程代码资源


# 快速上手

#### 如何接入

如果您是使用原生小程序的开发方式，请安如下方式接入

```
1. 通过npm安装 npm install dokit-miniapp, 然后从node_modules中将dokit-miniapp文件夹拷贝到自己的项目中，然后按如下方式进行使用
```

```
2. 在需要引用工具的页面 page.json 中引入组件(注意引用的路径)
```

```json
"usingComponents": {
  "dokit": "../../components/dokit-miniapp/dist/index/index"
}
```

```html
3. 在需要引用工具的页面 page.wxml 中引入使用组件
<dokit projectId="your projectId"></dokit>
```

如果您是使用小程序第三方框架的开发方式，可以做如下优化，

```js
在所需引入页面的js中，添加变量声明，例如
const isProd = process.env.NODE_ENV === '"production"'
```

```html
在<template></template>或者是render函数中，可以使用

isProd ? '' : <dokit projectId="your projectId"></dokit>
```

```js
如果框架暴露了webpack的相关打包配置，可以按照这样的配置，优化资源打包
compile: {
      exclude: [
        path.resolve(__dirname, '..', 'src/components/dokit-miniapp')
      ]
    }
```

> Tip: 1.由于微信小程序暂不支持开发环境和生产环境判断，请在生产环境手动删掉引用

> Tip: 2.第三方框架开始，要注意框架是否将process.env.NODE_ENV注入到的全局变量中，此外有的框架的兼容性并不友好，有些打包配置并没有支持，开发者要视情况而定


# 后续规划
1. 取色器
2. 接口请求性能分析
3. 授权开启管理工具


# 贡献

有任何意见或建议都欢迎提 issue

# github地址

https://github.com/didi/DoraemonKit/tree/master/miniapp 欢迎star

# 欢迎加入DoKit交流QQ群

<div align=center>
  <img width="190" height="260" src="https://pt-starimg.didistatic.com/static/starimg/img/MDnNgukM511590736490933.jpg">
</div>

# 欢迎关注我们的公众号

<div align=center>
  <img width="190" height="200" src="https://pt-starimg.didistatic.com/static/starimg/img/pYXiYtMEOl1591177821127.jpeg">
</div>