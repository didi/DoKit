import {
  EventEmitter
} from './eventEmiter'
import {
  guid,getQueryVariable
} from './utils'
import {
  getGlobalData
} from '@dokit/web-core'
import { HTTP_STATUS_CODES } from './utils'
import { hex_md5 } from './md5'
// import moment from 'moment'
const getAllResponseHeadersMap = function (xhr) {
  let headers = xhr.getAllResponseHeaders();
  let arr = headers.trim().split(/[\r\n]+/);
  let headerMap = {};

  arr.forEach(line => {
    let parts = line.split(': ');
    let header = parts.shift();
    let value = parts.join(': ');
    headerMap[header] = value;
  });
}

export class Request extends EventEmitter {
  constructor() {
    super()
    this.hookFetchConfig = {}
    this.hookXhrConfig = {}
    this.initialize()
  }
  initialize() {
    let Req = this
    // const {send:originSend, open:originOpen, setRequestHeader: originSetRequestHeader} = window.XMLHttpRequest.prototype;

    const winXhrProto = window.XMLHttpRequest.prototype;

    const originSend = winXhrProto.send;
    const originOpen = winXhrProto.open;
    const originSetRequestHeader = winXhrProto.setRequestHeader;
    const getResponseHeader = winXhrProto.getResponseHeader;
    const getAllResponseHeaders = winXhrProto.getAllResponseHeaders;
    // XMLHttp
    window.XMLHttpRequest.prototype.setRequestHeader = function (...args) {
      this.reqConf.requestHeaders||(this.reqConf.requestHeaders = {})
      this.reqConf.requestHeaders[args[0]] = args[1]
      if (Req.hookXhrConfig.onBeforeSetRequestHeader) {
        args = Req.hookXhrConfig.onBeforeSetRequestHeader(args, this.reqConf);
        // 返回false则取消设置请求头 （api-mock拦截接口，会将post改为get 此时设置请求头Content-Type会报跨域错误）
        args && originSetRequestHeader.apply(this, args);
      } else {
        originSetRequestHeader.apply(this, args);
      }
    }
    window.addEventListener('xhrSendStart', function (e) {
      console.log('xhrSendStart', e);
    });
    window.XMLHttpRequest.prototype.open = function (...args) {
      Req.state = getGlobalData();
      console.log('open', args);
      let originArgs = {
        ...args
      }
      args = Req.hookXhrConfig.onBeforeOpen && Req.hookXhrConfig.onBeforeOpen(args) || args
      const xhr = this;
      this.reqConf = {
        id: guid(),
        type: 'xhr',
        requestInfo: {
          method: args[0].toUpperCase(),
          url: args[1]
        },
        originRequestInfo: {
          method: originArgs[0].toUpperCase(),
          url: originArgs[1]
        }
      }

      xhr.addEventListener('readystatechange', function (e) {
        switch (xhr.readyState) {
          case 2:
            // this.headersReceived();
            const urlObject = new URL(xhr.reqConf.requestInfo.url)
            if (Req.state.socketConnect) {
              if (Req.state.isMaster) {
                console.log('主机发送请求')
                Req.state.mySocket.send({
                  type: 'DATA',
                  contentType: 'request',
                  channelSerial: Req.state.channelSerial,
                  pid:xhr.reqConf.pid,
                  data: JSON.stringify({
                    did: xhr.reqConf.did, //数据唯一识别ID 32位随机数
                    aid: Req.state.aid, //行为唯一识别ID 32位随机数，非空
                    url: urlObject?.href,
                    scheme: urlObject?.protocol,
                    host: urlObject?.host,
                    port: urlObject?.port,
                    path: `${urlObject?.pathname}?${getQueryVariable('api',urlObject?.href)&&`api=${getQueryVariable('api',urlObject?.href)}`}`,
                    searchKey:hex_md5(`${urlObject?.pathname}?${getQueryVariable('api',urlObject?.href)&&`api=${getQueryVariable('api',urlObject?.href)}`}`),
                    query: urlObject?.search?.split('?')[1] || "",
                    fragment: urlObject?.hash,
                    // requestTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"), //请求发生时间
                    requestHeaders: JSON.stringify(xhr.reqConf.requestHeaders),
                    requestContentType: xhr.reqConf.requestHeaders['content-type'] || '',
                    // requestBodyLength: 0,
                    requestBody: xhr.reqConf?.requestInfo?.body || '', //接口参数
                    method: xhr.reqConf?.requestInfo?.method || 'GET',
                    clientProtocol: 'http',
                    connectSerial: Req.state.connectSerial
                  })
                })
              }
            }
            break;
          case 4:
            Req.handleDone(xhr);
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
            if (Req.state.socketConnect) {
              if (Req.state.isMaster) {
                Req.state.mySocket.send({
                  type: 'DATA',
                  contentType: 'response',
                  channelSerial: Req.state.channelSerial,
                  pid:xhr.reqConf.pid,
                  data: JSON.stringify({
                    did:xhr.reqConf.did, //数据唯一识别ID 32位随机数
                    // responseTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"),
                    responseHeaders: JSON.stringify(xhr.reqConf.headerMap),
                    protocol: "http",
                    responseContentType: xhr.reqConf.headerMap['content-type']||'',
                    // responseBodyLength: '',
                    responseBody: xhr.response, //响应消息体
                    responseCode: xhr.status, //响应状态码
                    image: xhr.reqConf.headerMap['content-type']?.indexOf('image/') >= 0 ? true:false,
                    source: '',
                    connectSerial: Req.state.connectSerial,
                    headersString: headers,
                    responseXML:xhr.responseXML,
                    resRaw: xhr.reqConf.responseInfo.resRaw
                  })
                })
              }
            }
            break;
        }
      });

      xhr.addEventListener('error', function (e) {
        Req.emit('REQUEST.ERROR', {
          id: this.reqConf.id,
          responseInfo: {
            type: 'error'
          }
        })
      });
      xhr.getResponseHeader = (name) => {
        if (!(Req.state.socketConnect&&!Req.state.isMaster)||this.socketCode===404) {
          return getResponseHeader.call(this, name);
        }
        return this.responseHeader[name]
      }
      xhr.getAllResponseHeaders = ()=> {
        if (!(Req.state.socketConnect&&!Req.state.isMaster)||this.socketCode===404) {
          return getAllResponseHeaders.apply(this, args);
        }
        return this.headersString
      }

      originOpen.apply(this, args);
    };

    window.XMLHttpRequest.prototype.send = function () {
      let did = guid();
      let pid = guid();
      const urlObject = new URL(this.reqConf.requestInfo.url)
      if (arguments.length) {
        this.reqConf.requestInfo.body = arguments[0]
      }
      console.log('open', arguments, this);
      this.reqConf.did = did
      this.reqConf.pid = pid
      if (Req.state.socketConnect) {
        if (!Req.state.isMaster) {
          console.log('从机发送请求')
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
                  originSend.apply(this, arguments);
                }
                Req.state.mySocket.socket.removeEventListener('message', socketMessage)
              }
            }
          }
          Req.state.mySocket.socket.addEventListener('message', socketMessage)
          Req.state.mySocket.send({
            type: 'DATA',
            contentType: 'query',
            channelSerial: Req.state.channelSerial,
            pid,
            data: JSON.stringify({
              aid: Req.state.aid, //行为唯一识别ID 32位随机数，非空
              url: urlObject?.href,
              scheme: urlObject?.protocol,
              host: urlObject?.host,
              port: urlObject?.port,
              path: `${urlObject?.pathname}?${getQueryVariable('api',urlObject?.href)&&`api=${getQueryVariable('api',urlObject?.href)}`}`,
              searchKey:hex_md5(`${urlObject?.pathname}?${getQueryVariable('api',urlObject?.href)&&`api=${getQueryVariable('api',urlObject?.href)}`}`),
              query: urlObject?.search?.split('?')[1] || "",
              fragment: urlObject?.hash,
              // requestTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"), //请求发生时间
              requestHeaders: JSON.stringify(this.reqConf.requestHeaders), //请求头
              requestContentType: this.reqConf.requestHeaders['content-type'] || '',
              // requestBodyLength: 0,
              requestBody: this.reqConf?.requestInfo?.body || '', //接口参数
              method: this.reqConf?.requestInfo?.method || 'GET',
              clientProtocol: "http",
              connectSerial: Req.state.connectSerial,
            })
          })
        }
      }
      Req.emit('REQUEST.SEND', this.reqConf)
      if (!(Req.state.socketConnect&&!Req.state.isMaster)) {
        originSend.apply(this, arguments);
      }
    }

    // fetch
    const origFetch = window.fetch;
    window.fetch = async function (...args) {
      Req.state = getGlobalData();
      let did = guid();
      let pid = guid();
      let fetchResult = null;
      const urlObject = new URL(args[0])
      if (Req.state.socketConnect) {
        if (Req.state.isMaster) {
          console.log('主机发送请求')
          Req.state.mySocket.send({
            type: 'DATA',
            contentType: 'request',
            channelSerial: Req.state.channelSerial,
            pid,
            data: JSON.stringify({
              did, //数据唯一识别ID 32位随机数
              aid: Req.state.aid, //行为唯一识别ID 32位随机数，非空
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
              path: `${urlObject?.pathname}?${getQueryVariable('api',urlObject?.href)&&`api=${getQueryVariable('api',urlObject?.href)}`}`,
              searchKey:hex_md5(`${urlObject?.pathname}?${getQueryVariable('api',urlObject?.href)&&`api=${getQueryVariable('api',urlObject?.href)}`}`),
              connectSerial: Req.state.connectSerial
            })
          })
        } else {
          console.log('从机发送请求')
          fetchResult = new Promise((resolve, reject) => {
            //做一些异步操作
            let socketMessage = (e) => {
              let msg = JSON.parse(e.data);
              if (msg.type === "DATA") {
                if (msg.pid === pid) {
                  if(msg.code!==404) {
                    let data = JSON.parse(msg.data)
                    Req.emit('REQUEST.DONE', {
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
                        Req.emit('REQUEST.DONE', {
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
                  Req.state.mySocket.socket.removeEventListener('message', socketMessage)
                }
              }
            }
            Req.state.mySocket.socket.addEventListener('message', socketMessage)
          });
          Req.state.mySocket.send({
            type: 'DATA',
            contentType: 'query',
            channelSerial: Req.state.channelSerial,
            pid,
            data: JSON.stringify({
              aid: Req.state.aid, //行为唯一识别ID 32位随机数，非空
              url: urlObject?.href,
              scheme: urlObject?.protocol,
              host: urlObject?.host,
              port: urlObject?.port,
              path: `${urlObject?.pathname}?${getQueryVariable('api',urlObject?.href)&&`api=${getQueryVariable('api',urlObject?.href)}`}`,
              searchKey:hex_md5(`${urlObject?.pathname}?${getQueryVariable('api',urlObject?.href)&&`api=${getQueryVariable('api',urlObject?.href)}`}`),
              query: args.length > 1 ? (args[1].query || '') : '',
              fragment: urlObject?.hash,
              // requestTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"), //请求发生时间
              requestHeaders: args.length > 1 ? JSON.stringify((args[1].headers || {})) : JSON.stringify({}), //请求头
              requestContentType: args.length > 1 && args[1].headers && args[1].headers['Content-Type'] || '',
              // requestBodyLength: 0,
              requestBody: args.length > 1 ? (args[1].body || '') : '',
              method: args.length > 1 && args[1].method || 'GET',
              clientProtocol: "http",
              connectSerial: Req.state.connectSerial,
            })
          })
        }
      }
      let reqId = guid()
      args = Req.hookFetchConfig.onBeforeFetch && Req.hookFetchConfig.onBeforeFetch(args) || args

      Req.emit('REQUEST.SEND', {
        id: reqId,
        type: 'fetch',
        requestInfo: {
          url: args[0],
          method: (args.length > 1 ? (args[1].method || 'get') : 'get').toUpperCase(),
          headers: args.length > 1 ? (args[1].headers || {}) : {},
          body: args.length > 1 ? (args[1].body || '') : ''
        }
      })
      if (!(Req.state.socketConnect && !Req.state.isMaster)) {
        fetchResult = origFetch(...args);
          try {
          const res = await fetchResult
          const r = await res.clone().text()
          if (Req.state.socketConnect) {
            if (Req.state.isMaster) {
              Req.state.mySocket.send({
                type: 'DATA',
                contentType: 'response',
                channelSerial: Req.state.channelSerial,
                data: JSON.stringify({
                  did, //数据唯一识别ID 32位随机数
                  // responseTime: moment(new Date()).format("YYYY-MM-DD HH:mm:ss"),
                  responseHeaders: JSON.stringify(res.headers),
                  protocol: "http",
                  responseContentType: res.headers.get('Content-Type'),
                  // responseBodyLength: '',
                  responseBody: JSON.stringify(JSON.parse(r)), //响应消息体
                  responseCode: JSON.stringify(JSON.parse(r).code), //响应状态码
                  image: res.headers.get('Content-Type').indexOf('image/') >= 0 ? true:false,
                  source: '',
                  connectSerial: Req.state.connectSerial,
                  resRaw: r
                })
              })
            }
          }
          Req.emit('REQUEST.DONE', {
            id: reqId,
            responseInfo: {
              contentType: res.headers.get('Content-Type'),
              status: res.status,
              resRaw: r
            }
          })     
        } catch (error) {
            console.error(error)
        }
      }
      return fetchResult;
    };
  }

  hookXhr(hookXhrConfig) {
    this.hookXhrConfig = hookXhrConfig
  }

  hookFetch(hookFetchConfig) {
    this.hookFetchConfig = hookFetchConfig
  }

  getType(contentType) {
    if (!contentType)
      return {
        type: 'unknown',
        subType: 'unknown',
      };

    const type = contentType.split(';')[0].split('/');

    return {
      type: type[0],
      subType: type[type.length - 1],
    };
  }

  readBlobAsText(blob, callback) {
    const reader = new FileReader();
    reader.onload = () => {
      callback(null, reader.result);
    };
    reader.onerror = err => {
      callback(err);
    };
    reader.readAsText(blob);
  }

  handleDone(xhr) {
    const resType = xhr.responseType;
    let resTxt = '';
    const update = () => {
      this.emit('REQUEST.DONE', {
        id: xhr.reqConf.id,
        responseInfo: {
          resRaw: resTxt,
          contentType: xhr.getResponseHeader('Content-Type'),
          status: xhr.status,
        }
      })
    };

    const type = this.getType(xhr.getResponseHeader('Content-Type') || '');
    if (
      resType === 'blob' &&
      (type.type === 'text' ||
        type.subType === 'javascript' ||
        type.subType === 'json')
    ) {
      this.readBlobAsText(xhr.response, (err, result) => {
        if (result) resTxt = result;
        update();
      });
    } else {
      if (resType === '' || resType === 'text') resTxt = xhr.responseText;
      if (resType === 'json') resTxt = JSON.stringify(xhr.response);

      update();
    }
  }
  multiControlTransfer() {

  }
}