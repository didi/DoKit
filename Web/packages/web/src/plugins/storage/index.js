import Storage from './main.vue'
// import {overrideConsole,restoreConsole} from './js/console'
import {getGlobalData, RouterPlugin} from '@dokit/web-core'

export default new RouterPlugin({
  name: 'Storage',
  nameZh: '存储',
  component: Storage,
  icon: 'https://pt-starimg.didistatic.com/static/starimg/img/LM74BpA9bS1621926286444.png',
  onLoad(){},
  onUnload(){}
})