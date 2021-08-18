import Network from './main.vue'
import {getGlobalData, RouterPlugin} from '@dokit/web-core'
import { request } from "./../../assets/util";


export default new RouterPlugin({
  name: 'Network',
  nameZh: '网络请求',
  component: Network,
  icon: 'https://pt-starimg.didistatic.com/static/starimg/img/bZhn7wsssA1621588946807.png',
  onLoad(){
    let state = getGlobalData()
    state.requestList = []
    request.on('REQUEST.SEND', info=> {
      state.requestList.push(info)
    })

    request.on('REQUEST.DONE', info => {
      let index = state.requestList.findIndex(e => e.id === info.id)
      state.requestList[index].responseInfo = info.responseInfo
    })

    request.on('REQUEST.ERROR', info => {
      let index = state.requestList.findIndex(e => e.id === info.id)
      state.requestList[index].responseInfo = {...state.requestList[index].responseInfo, ...info.responseInfo}
    })
  },
  onUnload(){

  }
})