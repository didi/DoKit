import IndependPluginDemo from './IndependPluginDemo.vue'
import { IndependPlugin } from '@dokit/web-core'

export default new IndependPlugin({
    nameZh: '内存实时监测',
    name: 'test',
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
    component: IndependPluginDemo
})