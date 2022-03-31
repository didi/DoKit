import index from './app.vue'
import {
  RouterPlugin,
  getGlobalData
} from '@dokit/web-core'
import {
  hex_md5
} from '@dokit/web-utils'

export default new RouterPlugin({
  nameZh: '一机多控',
  name: 'OneMachineWithMultipleControls',
  icon: 'https://pt-starimg.didistatic.com/static/starimg/img/nktYHc1alL1635323338193.png',
  component: index,
  onLoad() {
    let state = getGlobalData()
    state.channelSerial = `web-${hex_md5(location.pathname)}`
  }
})