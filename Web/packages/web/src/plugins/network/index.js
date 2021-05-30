/*
 * @Author: yanghui 
 * @Date: 2021-05-28 20:53:35 
 * @Last Modified by: yanghui
 * @Last Modified time: 2021-05-30 12:01:52
 */

import Network from './network-container.vue'
import {mockData} from './js/network'
import {getGlobalData, RouterPlugin} from '@dokit/web-core'

export default new RouterPlugin({
  nameZh: '请求捕获',
  name: 'network',
  icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',
  component: Network,
  onLoad(){
    //获取拦截到的请求列表，存到state中
    mockData(({value}) => {
      let state = getGlobalData();
      state.reqList = state.reqList || [];
      value.id = state.reqList.length
      state.reqList.push(value);
      let value2 = {
        name: "collect",
        url: "https://www.tianqiapi.com/free/week?appid=68852321&appsecret=BgGLDVc7",
        status: '200',
        type: 'plain',
        subType: '',
        size: 2,
        data: {},
        method: 'GET',
        startTime: 0,
        time: 139,
        resTxt: '',
        done: false,
        reqHeaders: {},
        resHeaders: {},
        hasErr: false,
        response: {},
        headers: {},
        displayTime: 1,
      }
      value2.id = state.reqList.length
      state.reqList.push(value2);
    });
  },
  onUnload(){
    // restoreNetwork()
  }
})