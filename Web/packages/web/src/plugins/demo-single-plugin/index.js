import HelloWorld from './ToolHelloWorld.vue'
import {SinglePlugin} from '@dokit/web-core'

export default new SinglePlugin({
  nameZh: '独立 Single',
  name: 'test',
  icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
  component: HelloWorld
})

