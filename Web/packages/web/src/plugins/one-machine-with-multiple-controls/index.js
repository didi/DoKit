import index from './app.vue'
import {
  RouterPlugin,
  getGlobalData
} from '@dokit/web-core'
import {
  hex_md5,
} from '@dokit/web-utils'
import { request } from './../../assets/util'

export default new RouterPlugin({
  nameZh: '一机多控',
  name: 'OneMachineWithMultipleControls',
  icon: 'https://pt-starimg.didistatic.com/static/starimg/img/nktYHc1alL1635323338193.png',
  component: index,
  onLoad() {
    let state = getGlobalData()
    state.channelSerial = `web-${hex_md5(location.pathname)}`
  },
  // onProductReady(){
  //   request.multiControlhook({
  //     xhrMasterRequest:(xhr)=>{
  //       let state = getGlobalData()
  //       const urlObject = new URL(xhr.reqConf.requestInfo.url)
  //       if (state.socketConnect) {
  //         if (state.isMaster) {
  //           let data = {
  //             type: 'DATA',
  //             contentType: 'request',
  //             channelSerial: state.channelSerial,
  //             pid:xhr.reqConf.pid,
  //             data: JSON.stringify({
  //               did: xhr.reqConf.did, //数据唯一识别ID 32位随机数
  //               aid: state.aid, //行为唯一识别ID 32位随机数，非空
  //               url: urlObject?.href,
  //               scheme: urlObject?.protocol,
  //               host: urlObject?.host,
  //               port: urlObject?.port,
  //               path: `${urlObject?.pathname}${getQueryVariable('api',urlObject?.href)?`?api=${getQueryVariable('api',urlObject?.href)}`:''}`,
  //               searchKey:hex_md5(`${urlObject?.pathname}${getQueryVariable('api',urlObject?.href)?`?api=${getQueryVariable('api',urlObject?.href)}`:''}`),
  //               query: urlObject?.search?.split('?')[1] || "",
  //               fragment: urlObject?.hash,
  //               // requestTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"), //请求发生时间
  //               requestHeaders: JSON.stringify(xhr.reqConf.requestHeaders),
  //               requestContentType: xhr.reqConf.requestHeaders['Content-Type']||xhr.reqConf.requestHeaders['content-type'] || '',
  //               // requestBodyLength: 0,
  //               requestBody: xhr.reqConf?.requestInfo?.body || '', //接口参数
  //               method: xhr.reqConf?.requestInfo?.method || 'GET',
  //               clientProtocol: 'http',
  //               connectSerial: state.connectSerial
  //             })
  //           };
  //           if(state?.mySocket?.webSocketState) {
  //             let mcMasterWaitRequestQueueLength = state.mcMasterWaitRequestQueue.length
  //             if(mcMasterWaitRequestQueueLength>0){
  //               while (mcMasterWaitRequestQueueLength--) {
  //                 let item = state.mcMasterWaitRequestQueue[mcMasterWaitRequestQueueLength]
  //                 state.mySocket.send(item.data)
  //                 console.log('主机队列发送Request请求',item)
  //                 state.mcMasterWaitRequestQueue.splice(mcMasterWaitRequestQueueLength, 1)
  //               }
  //             }
  //             state.mySocket.send(data)
  //             console.log('主机发送Request请求')
  //           }else{
  //             state.mcMasterWaitRequestQueue.push({data})
  //           }
  //         }
  //       }
  //     }
  //   })
  // }
})