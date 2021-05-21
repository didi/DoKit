import Console from './main.vue'
import {overrideConsole,restoreConsole} from './js/console'
import {getGlobalData, RouterPlugin} from '@dokit/web-core'

export default new RouterPlugin({
  name: 'console',
  nameZh: '日志',
  component: Console,
  icon: 'https://pt-starimg.didistatic.com/static/starimg/img/bZhn7wsssA1621588946807.png',
  onLoad(){
    console.log('Load')
    overrideConsole(({name, type, value}) => {
      let state = getGlobalData();
      state.logList = state.logList || [];
      state.logList.push({
        type: type,
        name: name,
        value: value
      });
    });
  },
  onUnload(){
    restoreConsole()
  }
})