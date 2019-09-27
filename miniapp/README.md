<p align="center">
    <img width="200" src="//pt-starimg.didistatic.com/static/starimg/img/W8OeOO6Pue1561556055823.png">
</p>

# Doraemon mini program debugger
一个支持小程序端的调试工具
## 开发背景
对于小程序开发者和测试同学来说，很多临时性的调试功能需要单独开发去支持，比如查看小程序信息，手机信息
以及用户信息，扫码打开页面等。这些功能对于每个小程序都是类似的，而且遇到类似的需求时都需要进行单独开发。

我们内部通过对这些通用功能进行梳理沉淀，形成一个功能集合-哆啦A梦小程序端调试工具，以通用工具的形式开放对外，能够让每个小程序
都可以快速的接入这些通用并且与业务代码无关的功能，如H5任意门，小程序基本信息，位置模拟等等。

## 简单总结

Doraemon小程序端调试工具，内置很多常用的工具，避免重复实现，一次接入，你将会拥有强大的工具集合。

## 效果演示
哆啦A梦小程序端首页效果演示<br>
<img width="200" src="./docs/assets/preview.png">

## 内置功能模块

- app信息<br>
<img width="200" src="./docs/assets/appinfo.jpg"><br>
用于快速查看手机系统信息，小程序基本信息，用户信息，授权信息等基础信息，避免反复打开手机设置
或者调用小程序原生api进行查看。

- 位置模拟<br>
<img width="200" src="./docs/assets/position.jpg"><br>
用于小程序端位置模拟，包括位置授权，位置查看，位置模拟，恢复位置设置等几大功能，通过对wx.getLocation进行方法
重写，实现了位置模拟，进行位置模拟后，在小程序内所有调用位置查询的方法内都将返回你设定的位置，还原后将恢复原生方法。

- 缓存管理<br>
<img width="200" src="./docs/assets/storage.jpg"><br>
一个强大的缓存管理面板，集成了对缓存的所有操作功能，包括设置缓存，清除缓存，更新缓存值等，可以在小程序非常便利的进行
缓存的管理

- H5任意门<br>
<img width="200" src="./docs/assets/h5door.jpg"><br>
可以通过扫码和粘贴链接的方式在小程序中打开h5页面，操作非常便利

- 更新版本<br>
当你的小程序进行代码更新时，为了获取最新的线上包需要重启小程序，该功能可以在你的小程序上
通过点击更新操作，直接获取到最新的远程代码资源


# 快速上手

## 使用之前

在开始使用之前，你需要先阅读 [微信小程序自定义组件](https://developers.weixin.qq.com/miniprogram/dev/framework/custom-component/) 的相关文档。

## 如何使用

1. 通过 `npm` 安装，同时依赖开发者工具的 `npm` 构建。具体详情可查阅 [官方 npm 文档](https://developers.weixin.qq.com/miniprogram/dev/devtools/npm.html)。

### 在需要引用工具的页面 page.json 中引入组件

```json
"usingComponents": {
    "dokit": "../../dist/index/index"
}
```

### 在 page.wxml 中使用组件

```html
<dokit/>
```

### 在应用app.json中通过分包的方式注册需要依赖的页面
 ```js
"subpackages": [
    {
      "root": "dist",
      "pages": [
        "appInformation",
        "debug",
        "h5door",
        "logs",
        "positionSimulation",
        "storage",
        "index"
      ]
    }
  ]
```

## 贡献

有任何意见或建议都欢迎提 issue

## License

MIT
