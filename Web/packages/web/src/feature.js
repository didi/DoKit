import Console from './components/ToolConsole'
import AppInfo from './components/ToolAppInfo'
import HelloWorld from './components/ToolHelloWorld'

export const BasicFeatures = {
  title: '基础功能',
  list: [{
    title: '日志',
    name: 'console',
    icon: '',
    component: Console
  }, {
    title: '应用信息',
    name: 'app-info',
    icon: '',
    component: AppInfo
  },  {
    title: '测试',
    name: 'hello',
    icon: '',
    component: HelloWorld
  }]
}

export const DokitFeatures = {
  title: '平台功能',
  list: [{
    title: '接口 Mock',
    name: 'mock',
    icon: '',
    component: HelloWorld
  }]
}
export const Features = [BasicFeatures, DokitFeatures]