import index from './app.vue'
import {RouterPlugin} from '@dokit/web-core'

export default new RouterPlugin({
  nameZh: '扫码',
  name: 'scanCode',
  icon: 'https://pt-starimg.didistatic.com/static/starimg/img/jvD7qcMXX51645432343946.png',
  component: index
})

