import HelloWorld from './ToolHelloWorld.vue'
import {RouterPlugin} from '@dokit/web-core'

export default new RouterPlugin({
  nameZh: '测试',
  name: 'test',
  icon: 'https://pt-starimg.didistatic.com/static/starimg/img/6WONqJCVks1621926657356.png',
  component: HelloWorld
})

