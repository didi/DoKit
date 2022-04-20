import {
  EventEmitter
} from './eventEmiter'
import {
  guid,completionUrlProtocol
} from './utils'
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
    this.multiControlhookConfig = {}
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
          url: completionUrlProtocol(args[1])
        },
        originRequestInfo: {
          method: originArgs[0].toUpperCase(),
          url: completionUrlProtocol(originArgs[1])
        }
      }

      xhr.addEventListener('readystatechange', function (e) {
        try {
          switch (xhr.readyState) {
            case 2:
              // this.headersReceived();
              Req.multiControlhookConfig?.xhrHostRequest?.call(Req,xhr);
              break;
            case 4:
              Req.handleDone(xhr);
              Req.multiControlhookConfig?.xhrHostResponse?.call(Req,xhr);
              break;
          }         
        } catch (error) {
            console.log(error)
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
        if(Req.multiControlhookConfig?.getResponseHeader){
          return Req.multiControlhookConfig?.getResponseHeader.call(this, getResponseHeader,name);
        }else{
          return getResponseHeader.call(this, name);
        }
      }
      xhr.getAllResponseHeaders = ()=> {
        if(Req.multiControlhookConfig?.getAllResponseHeaders){
          return Req.multiControlhookConfig?.getAllResponseHeaders.apply(this, [getAllResponseHeaders,...arguments]);
        }else{
          return getAllResponseHeaders.call(this, args);
        }
      }
      originOpen.apply(this, args);
    };

    window.XMLHttpRequest.prototype.send = function () {
      try {
        if (arguments.length) {
          this.reqConf.requestInfo.body = arguments[0]
        }
        Req.multiControlhookConfig?.xhrClientQuery?.apply(this, [originSend,...arguments]);
        Req.emit('REQUEST.SEND', this.reqConf)
        if(Req.multiControlhookConfig?.isOriginSend){
          Req.multiControlhookConfig?.isOriginSend.apply(this, [originSend,...arguments]);
        }else{
          originSend.apply(this, arguments);
        }
      } catch (error) {
        console.log(error)
      }
    }

    // fetch
    const origFetch = window.fetch;
    window.fetch = async function (...args) {
      try {
        let did = guid();
        let pid = guid();
        let reqId = guid()
        let fetchResult = null;
        console.log('fetchHostRequest:',Req.multiControlhookConfig?.fetchHostRequest)
        Req.multiControlhookConfig?.fetchHostRequest?.apply(Req, [did,pid,...arguments]);
        fetchResult = Req.multiControlhookConfig?.fetchResult?.apply(Req, [pid,reqId,origFetch,...arguments]);
        Req.multiControlhookConfig?.fetchClientQuery?.apply(Req, [pid,...arguments]);
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
        if (!fetchResult) {
          fetchResult = origFetch(...args);
          const res = await fetchResult
          const r = await res.clone().text()
          Req.multiControlhookConfig?.fetchHostResponse?.apply(Req, [did,res,r]);
          Req.emit('REQUEST.DONE', {
            id: reqId,
            responseInfo: {
              contentType: res.headers.get('Content-Type'),
              status: res.status,
              resRaw: r
            }
          })     
        }
        return fetchResult;
      } catch (error) {
        console.error(error)
      }
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
  multiControlhook(multiControlhookConfig) {
    this.multiControlhookConfig = multiControlhookConfig
  }
}