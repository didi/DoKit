import Console from './components/ToolConsole'
import AppInfo from './components/ToolAppInfo'
import HelloWorld from './components/ToolHelloWorld'

export const BasicFeatures = {
  title: '常用工具',
  list: [{
    title: '日志',
    name: 'console',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/PbNXVyzTbq1618997544543.png',
    component: Console
  }, {
    title: '应用信息',
    name: 'app-info',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
    component: AppInfo
  },{
    title: 'H5任意门',
    name: 'h5-door',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/FHqpI3InaS1618997548865.png',
    component: AppInfo
  }]
}

export const DokitFeatures = {
  title: '平台功能',
  list: [{
    title: 'Mock数据',
    name: 'mock',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/aDn77poRDB1618997545078.png',
    component: HelloWorld
  }]
}

export const UIFeatures = {
  title: '视觉功能',
  list: [{
    title: '取色器',
    name: 'color-selector',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/QYUvEE8FnN1618997536890.png',
    component: HelloWorld
  },{
    title: '对齐标尺',
    name: 'align-ruler',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/a5UTjMn6lO1618997535798.png',
    component: HelloWorld
  },{
    title: 'UI结构',
    name: 'view-selector',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/XNViIWzG7N1618997548483.png',
    component: HelloWorld
  }]
}
export const Features = [BasicFeatures, DokitFeatures,UIFeatures]