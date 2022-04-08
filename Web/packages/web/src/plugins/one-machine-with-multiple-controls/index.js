import index from './app.vue'
import {
  RouterPlugin,
  getGlobalData
} from '@dokit/web-core'
import {
  hex_md5,
  guid,
  getQueryVariable,
  completionUrlProtocol,
  strMapToObj,
  HTTP_STATUS_CODES,
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
    request.multiControlhook({
      xhrHostRequest:function(xhr){
        let state = getGlobalData()
        const urlObject = new URL(xhr.reqConf.requestInfo.url)
        if (state.socketConnect) {
          if (state.isHost) {
            let data = {
              type: 'DATA',
              contentType: 'request',
              channelSerial: state.channelSerial,
              pid:xhr.reqConf.pid,
              data: JSON.stringify({
                did: xhr.reqConf.did, //数据唯一识别ID 32位随机数
                aid: state.aid, //行为唯一识别ID 32位随机数，非空
                url: urlObject?.href,
                scheme: urlObject?.protocol,
                host: urlObject?.host,
                port: urlObject?.port,
                path: `${urlObject?.pathname}${getQueryVariable('api',urlObject?.href)?`?api=${getQueryVariable('api',urlObject?.href)}`:''}`,
                searchKey:hex_md5(`${urlObject?.pathname}${getQueryVariable('api',urlObject?.href)?`?api=${getQueryVariable('api',urlObject?.href)}`:''}`),
                query: urlObject?.search?.split('?')[1] || "",
                fragment: urlObject?.hash,
                // requestTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"), //请求发生时间
                requestHeaders: JSON.stringify(xhr.reqConf.requestHeaders),
                requestContentType: xhr.reqConf.requestHeaders['Content-Type']||xhr.reqConf.requestHeaders['content-type'] || '',
                // requestBodyLength: 0,
                requestBody: xhr.reqConf?.requestInfo?.body || '', //接口参数
                method: xhr.reqConf?.requestInfo?.method || 'GET',
                clientProtocol: 'http',
                connectSerial: state.connectSerial
              })
            };
            if(state?.mySocket?.webSocketState) {
              let mcHostWaitRequestQueueLength = state.mcHostWaitRequestQueue.length
              if(mcHostWaitRequestQueueLength>0){
                while (mcHostWaitRequestQueueLength--) {
                  let item = state.mcHostWaitRequestQueue[mcHostWaitRequestQueueLength]
                  state.mySocket.send(item.data)
                  console.log('主机队列发送Request请求',item)
                  state.mcHostWaitRequestQueue.splice(mcHostWaitRequestQueueLength, 1)
                }
              }
              state.mySocket.send(data)
              console.log('主机发送Request请求')
            }else{
              state.mcHostWaitRequestQueue.push({data})
            }
          }
        }
      },
      xhrHostResponse:function(xhr){
        let state = getGlobalData()
        var headers = xhr.getAllResponseHeaders();
        var arr = headers.trim().split(/[\r\n]+/);
        var headerMap = {};
        arr.forEach(function (line) {
          var parts = line.split(': ');
          var header = parts.shift();
          var value = parts.join(': ');
          headerMap[header] = value;
        })
        xhr.reqConf.headerMap = headerMap
        if (state.socketConnect) {
          if (state.isHost) {
            let data = {
              type: 'DATA',
              contentType: 'response',
              channelSerial: state.channelSerial,
              pid:xhr.reqConf.pid,
              data: JSON.stringify({
                did:xhr.reqConf.did, //数据唯一识别ID 32位随机数
                // responseTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"),
                responseHeaders: JSON.stringify(xhr.reqConf.headerMap),
                protocol: "http",
                responseContentType: xhr.reqConf.headerMap['Content-Type']||xhr.reqConf.headerMap['content-type']||'',
                // responseBodyLength: '',
                responseBody: xhr.response, //响应消息体
                responseCode: xhr.status, //响应状态码
                image: (xhr.reqConf.headerMap['Content-Type']||xhr.reqConf.headerMap['content-type'])?.indexOf('image/') >= 0 ? true:false,
                source: '',
                connectSerial: state.connectSerial,
                headersString: headers,
                responseXML:xhr.responseXML,
                resRaw: xhr.reqConf.responseInfo.resRaw
              })
            };
            if(state?.mySocket?.webSocketState){
              let mcHostWaitResponseQueueLength = state.mcHostWaitResponseQueue.length
              if(mcHostWaitResponseQueueLength>0){
                while (mcHostWaitResponseQueueLength--) {
                  let item = state.mcHostWaitResponseQueue[mcHostWaitResponseQueueLength]
                  state.mySocket.send(item.data)
                  console.log('主机队列发送Response请求',item)
                  state.mcHostWaitResponseQueue.splice(mcHostWaitResponseQueueLength, 1)
                }
              }
              state.mySocket.send(data)
              console.log('主机发送Response请求')
            }else{
              state.mcHostWaitResponseQueue.push({data})
            }
          }
        }
      },
      xhrClientQuery:function(originSend,...arg){
        let state = getGlobalData();
        let did = guid();
        let pid = guid();
        this.reqConf.did = did
        this.reqConf.pid = pid
        const urlObject = new URL(this.reqConf.requestInfo.url)
        if (state.socketConnect) {
          if (!state.isHost) {
            let data = {
              type: 'DATA',
              contentType: 'query',
              channelSerial: state.channelSerial,
              pid,
              data: JSON.stringify({
                aid: state.aid, //行为唯一识别ID 32位随机数，非空
                url: urlObject?.href,
                scheme: urlObject?.protocol,
                host: urlObject?.host,
                port: urlObject?.port,
                path: `${urlObject?.pathname}${getQueryVariable('api',urlObject?.href)?`?api=${getQueryVariable('api',urlObject?.href)}`:''}`,
                searchKey:hex_md5(`${urlObject?.pathname}${getQueryVariable('api',urlObject?.href)?`?api=${getQueryVariable('api',urlObject?.href)}`:''}`),
                query: urlObject?.search?.split('?')[1] || "",
                fragment: urlObject?.hash,
                // requestTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"), //请求发生时间
                requestHeaders: JSON.stringify(this.reqConf?.requestHeaders), //请求头
                requestContentType:  this.reqConf.requestHeaders['Content-Type']||this.reqConf.requestHeaders['content-type'] || '',
                // requestBodyLength: 0,
                requestBody: this.reqConf?.requestInfo?.body || '', //接口参数
                method: this.reqConf?.requestInfo?.method || 'GET',
                clientProtocol: "http",
                connectSerial: state.connectSerial,
              })
            }
            if(state?.mySocket?.webSocketState){
              let mcClientWaitRequestQueueLength = state.mcClientWaitRequestQueue.length
              if(mcClientWaitRequestQueueLength>0){
                while (mcClientWaitRequestQueueLength--) {
                  let item = state.mcClientWaitRequestQueue[mcClientWaitRequestQueueLength]
                  console.log('从机队列发送请求',item)
                  let socketMessage = (e) => {
                    let msg = JSON.parse(e.data);
                    if (msg.type === "DATA") {
                      if (msg.pid === item.data.pid) {
                        if(msg.code!==404){
                          let definePropertyKey = ['readyState','response','responseText','status','statusText','responseURL','responseXML']
                          definePropertyKey.forEach((definePropertyItem)=>{
                            Object.defineProperty(item.that, definePropertyItem, {
                              writable:true,
                            });
                          })
                          let data = JSON.parse(msg.data)
                          let newData = data.responseBody
                          item.that.response = item.that.responseText = newData
                          item.that.status = data.responseCode || 200
                          item.that.statusText = HTTP_STATUS_CODES[item.that.status]
                          item.that.responseHeader = JSON.parse(data.responseHeaders)
                          item.that.headersString = data.headersString
                          item.that.responseURL = data.url
                          item.that.responseXML = data.responseXML
                          item.that.readyState = 2
                          item.that.dispatchEvent(new Event('readystatechange'))
                          item.that.readyState = 3
                          item.that.dispatchEvent(new Event('readystatechange'))
                          item.that.readyState = 4
                          item.that.dispatchEvent(new Event('readystatechange'))
                          item.that.dispatchEvent(new Event('load'));
                          item.that.dispatchEvent(new Event('loadend'));
                          console.log('从机队列接受中转消息',item.that)
                        }else {
                          item.that.socketCode = 404
                          originSend.apply(item.that, item.arg);
                        }
                        state.mySocket.socket.removeEventListener('message', socketMessage)
                      }
                    }
                  }
                  state.mySocket.socket.addEventListener('message', socketMessage)
                  state.mySocket.send(item.data)
                  console.log('从机队列发送请求成功',item)
                  state.mcClientWaitRequestQueue.splice(mcClientWaitRequestQueueLength, 1)
                }
              }
              console.log('从机发送请求',urlObject?.href)
              let socketMessage = (e) => {
                let msg = JSON.parse(e.data);
                if (msg.type === "DATA") {
                  if (msg.pid === pid) {
                    if(msg.code!==404){
                      let definePropertyKey = ['readyState','response','responseText','status','statusText','responseURL','responseXML']
                      definePropertyKey.forEach((item)=>{
                        Object.defineProperty(this, item, {
                          writable:true,
                        });
                      })
                      let data = JSON.parse(msg.data)
                      let newData = data.responseBody
                      this.response = this.responseText = newData
                      this.status = data.responseCode || 200
                      this.statusText = HTTP_STATUS_CODES[this.status]
                      this.responseHeader = JSON.parse(data.responseHeaders)
                      this.headersString = data.headersString
                      this.responseURL = data.url
                      this.responseXML = data.responseXML
                      this.readyState = 2
                      this.dispatchEvent(new Event('readystatechange'))
                      this.readyState = 3
                      this.dispatchEvent(new Event('readystatechange'))
                      this.readyState = 4
                      this.dispatchEvent(new Event('readystatechange'))
                      this.dispatchEvent(new Event('load'));
                      this.dispatchEvent(new Event('loadend'));
                    }else {
                      this.socketCode = 404
                      originSend.apply(this, arg);
                    }
                    state.mySocket.socket.removeEventListener('message', socketMessage)
                  }
                }
              }
              state.mySocket.socket.addEventListener('message', socketMessage)
              state.mySocket.send(data)
              console.log('从机发送请求成功',urlObject?.href)
            }else{
              state.mcClientWaitRequestQueue.push({that:this,data,arg:arg})
            }
          }
        }
      },
      getResponseHeader:function(getResponseHeader,name){
        let state = getGlobalData()
        if (!(state.socketConnect&&!state.isHost)||this.socketCode===404) {
          return getResponseHeader.call(this, name);
        }
        return this.responseHeader[name]
      },
      getAllResponseHeaders:function(getAllResponseHeaders,...arg){
        let state = getGlobalData()
        if (!(state.socketConnect&&!state.isHost)||this.socketCode===404) {
          return getAllResponseHeaders.apply(this, arg);
        }
        return this.headersString
      },
      isOriginSend:function(originSend,...arg){
        let state = getGlobalData()
        if (!(state.socketConnect&&!state.isHost)) {
          originSend.apply(this, arg);
        }   
      },
      fetchHostRequest:function(did,pid,...args){
        let state = getGlobalData()
        const urlObject = new URL(completionUrlProtocol(args[0]))
        if (state.socketConnect) {
          if (state.isHost) {
            console.log('主机发送请求')
            state.mySocket.send({
              type: 'DATA',
              contentType: 'request',
              channelSerial: state.channelSerial,
              pid,
              data: JSON.stringify({
                did, //数据唯一识别ID 32位随机数
                aid: state.aid, //行为唯一识别ID 32位随机数，非空
                url: urlObject?.href,
                scheme: urlObject?.protocol,
                host: urlObject?.host,
                port: urlObject?.port,
                query: urlObject?.search?.split('?')[1] || "",
                fragment: urlObject?.hash,
                // requestTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"), //请求发生时间
                requestHeaders: args.length > 1 ? JSON.stringify((args[1].headers || {})) : JSON.stringify({}), //请求头
                requestContentType: args.length > 1 && args[1].headers && args[1].headers['Content-Type'] || '',
                // requestBodyLength: 0,
                requestBody: args.length > 1 ? (args[1].body || '') : '', //接口参数
                method: args.length > 1 && args[1].method || 'GET',
                clientProtocol: 'http',
                path: `${urlObject?.pathname}${getQueryVariable('api',urlObject?.href)?`?api=${getQueryVariable('api',urlObject?.href)}`:''}`,
                searchKey:hex_md5(`${urlObject?.pathname}${getQueryVariable('api',urlObject?.href)?`?api=${getQueryVariable('api',urlObject?.href)}`:''}`),
                connectSerial: state.connectSerial
              })
            })
          }
        }
      },
      fetchHostResponse:function(did,res,r){
        let state = getGlobalData()
        if (state.socketConnect) {
          if (state.isHost) {
            const responseHeaders = strMapToObj(res.headers)
            state.mySocket.send({
              type: 'DATA',
              contentType: 'response',
              channelSerial: state.channelSerial,
              data: JSON.stringify({
                did, //数据唯一识别ID 32位随机数
                // responseTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"),
                responseHeaders: JSON.stringify(responseHeaders),
                protocol: "http",
                responseContentType: res.headers.get('Content-Type'),
                // responseBodyLength: '',
                responseBody: JSON.stringify(JSON.parse(r)), //响应消息体
                responseCode: res.status, //响应状态码
                image: res.headers.get('Content-Type').indexOf('image/') >= 0 ? true:false,
                source: '',
                connectSerial: state.connectSerial,
                resRaw: r
              })
            })
          }
        }
      },
      fetchClientQuery:function(pid,...args){
        const urlObject = new URL(completionUrlProtocol(args[0]))
        let state = getGlobalData()
        if (state.socketConnect) {
          if (!state.isHost) {
            state.mySocket.send({
              type: 'DATA',
              contentType: 'query',
              channelSerial: state.channelSerial,
              pid,
              data: JSON.stringify({
                aid: state.aid, //行为唯一识别ID 32位随机数，非空
                url: urlObject?.href,
                scheme: urlObject?.protocol,
                host: urlObject?.host,
                port: urlObject?.port,
                path: `${urlObject?.pathname}${getQueryVariable('api',urlObject?.href)?`?api=${getQueryVariable('api',urlObject?.href)}`:''}`,
                searchKey:hex_md5(`${urlObject?.pathname}${getQueryVariable('api',urlObject?.href)?`?api=${getQueryVariable('api',urlObject?.href)}`:''}`),
                query: args.length > 1 ? (args[1].query || '') : '',
                fragment: urlObject?.hash,
                // requestTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"), //请求发生时间
                requestHeaders: args.length > 1 ? JSON.stringify((args[1].headers || {})) : JSON.stringify({}), //请求头
                requestContentType: args.length > 1 && args[1].headers && args[1].headers['Content-Type'] || '',
                // requestBodyLength: 0,
                requestBody: args.length > 1 ? (args[1].body || '') : '',
                method: args.length > 1 && args[1].method || 'GET',
                clientProtocol: "http",
                connectSerial: state.connectSerial,
              })
            })
          }
        }
      },
      fetchResult:function(pid,reqId,origFetch,...args){
        let fetchResult = null;
        let state = getGlobalData()
        if (state.socketConnect) {
          if (!state.isHost) {
            fetchResult = new Promise((resolve, reject) => {
              //做一些异步操作
              let socketMessage = (e) => {
                let msg = JSON.parse(e.data);
                if (msg.type === "DATA") {
                  if (msg.pid === pid) {
                    if(msg.code!==404) {
                      let data = JSON.parse(msg.data)
                      this.emit('REQUEST.DONE', {
                        id: reqId,
                        responseInfo: {
                          contentType: data.contentType,
                          status: data.responseCode,
                          resRaw: data.resRaw
                        }
                      })
                      let newData = data.responseBody
                      let init = {
                        "status": data.responseCode,
                        "statusText": "OK"
                      };
                      let newResponse = new Response(newData, init);
                      resolve(newResponse);
                    }else{
                      fetchResult = origFetch(...args);
                      fetchResult.then(res => {
                        res.clone().text().then(r => {
                          resolve(res);
                          this.emit('REQUEST.DONE', {
                            id: reqId,
                            responseInfo: {
                              contentType: res.headers.get('Content-Type'),
                              status: res.status,
                              resRaw: r
                            }
                          })
                        })
                      })
                    }
                    state.mySocket.socket.removeEventListener('message', socketMessage)
                  }
                }
              }
              state.mySocket.socket.addEventListener('message', socketMessage)
            });
          }
        }
        return fetchResult
      }
    })
  },
})