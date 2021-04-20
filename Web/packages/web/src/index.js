import Console from './components/ToolConsole'
import AppInfo from './components/ToolAppInfo'
import HelloWorld from './components/ToolHelloWorld'

import {Dokit} from '@dokit/web-core'


new Dokit({
  plugins: [{
    name: '日志',
    component: Console
  }, {
    name: '应用信息',
    component: AppInfo
  }, {
    name: '你好',
    component: HelloWorld
  }]
});