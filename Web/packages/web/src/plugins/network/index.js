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
    state.recorderRequestList = []
    request.on('REQUEST.SEND', info=> {
      console.log('REQUEST.SEND',info)
      state.requestList.push(info)
      state.startRecorder&&state.recorderRequestList.push(info)
    })

    request.on('REQUEST.DONE', info => {
      console.log('REQUEST.DONE',info)
      let index = state.requestList.findIndex(e => e.id === info.id)
      let recorderIndex = state.recorderRequestList.findIndex(e => e.id === info.id)
      state.requestList[index].responseInfo = info.responseInfo
      state.startRecorder&&recorderIndex!==-1&&(state.recorderRequestList[recorderIndex].responseInfo = info.responseInfo,console.log(state.recorderRequestList))
    })

    request.on('REQUEST.ERROR', info => {
      console.log('REQUEST.ERROR',info)
      let index = state.requestList.findIndex(e => e.id === info.id)
      let recorderIndex = state.recorderRequestList.findIndex(e => e.id === info.id)
      state.requestList[index].responseInfo = {...state.requestList[index].responseInfo, ...info.responseInfo}
      state.startRecorder&&(state.recorderRequestList[recorderIndex].responseInfo = {...state.recorderRequestList[recorderIndex].responseInfo, ...info.responseInfo},console.log(state.recorderRequestList))
    })
  },
  onUnload(){

  }
})