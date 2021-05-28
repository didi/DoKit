import Console from './plugins/console/index'
import AppInfo from './plugins/app-info/index'
import Storage from './plugins/storage/index'
import DemoPlugin from './plugins/demo-plugin/index'
import DemoIndependPlugin from './plugins/demo-single-plugin/index'
import H5DoorPlugin from './plugins/h5-door/index'
import HelloWorld from './components/ToolHelloWorld'
import {IndependPlugin, RouterPlugin} from '@dokit/web-core'

export const BasicFeatures = {
  title: '常用工具',
  list: [Console, AppInfo, Storage, DemoPlugin, DemoIndependPlugin, H5DoorPlugin]
}

export const DokitFeatures = {
  title: '平台功能',
  list: [new RouterPlugin({
    nameZh: 'Mock数据',
    name: 'mock',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/aDn77poRDB1618997545078.png',
    component: HelloWorld
  })]
}

export const UIFeatures = {
  title: '视觉功能',
  list: [new RouterPlugin({
    nameZh: '对齐标尺',
    name: 'align-ruler',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/a5UTjMn6lO1618997535798.png',
    component: HelloWorld
  }), new RouterPlugin({
    nameZh: 'UI结构',
    name: 'view-selector',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/XNViIWzG7N1618997548483.png',
    component: HelloWorld
  })]
}
export const Features = [BasicFeatures, DokitFeatures, UIFeatures]