import ClearCache from './main.vue'
// import {overrideConsole,restoreConsole} from './js/console'
import {getGlobalData, RouterPlugin} from '@dokit/web-core'

export default new RouterPlugin({
  name: 'ClearCache',
  nameZh: '清除缓存',
  component: ClearCache,
  icon: "https://pt-starimg.didistatic.com/static/starimg/img/nU9RCY0E1e1655278941997.pic",
  onLoad(){},
  onUnload(){}
})