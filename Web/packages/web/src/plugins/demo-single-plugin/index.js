import IndependPluginDemo from './IndependPluginDemo.vue'
import {IndependPlugin} from '@dokit/web-core'

export default new IndependPlugin({
  nameZh: '独立插件',
  name: 'test',
  icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
  component: IndependPluginDemo
})

