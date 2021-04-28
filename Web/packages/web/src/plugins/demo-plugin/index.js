import HelloWorld from './ToolHelloWorld.vue'
import {RouterPlugin} from '@dokit/web-core'

export default new RouterPlugin({
  nameZh: '测试',
  name: 'test',
  icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
  component: HelloWorld
})

