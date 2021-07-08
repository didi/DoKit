import { EventEmitter } from './eventEmiter'
import { guid } from './utils'

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

export class Request extends EventEmitter{
  constructor(){
    super()
    this.hookFetchConfig = {}
    this.hookXhrConfig = {}
    this.initialize()
  }
  initialize(){
    let Req = this
    // const {send:originSend, open:originOpen, setRequestHeader: originSetRequestHeader} = window.XMLHttpRequest.prototype;

    const winXhrProto = window.XMLHttpRequest.prototype;

    const originSend = winXhrProto.send;
    const originOpen = winXhrProto.open;
    const originSetRequestHeader = winXhrProto.setRequestHeader;
    // XMLHttp
    window.XMLHttpRequest.prototype.setRequestHeader = function(){

      originSetRequestHeader.apply(this, arguments);
    }
    window.XMLHttpRequest.prototype.open = function (...args) {
      console.
      args = Req.hookXhrConfig.onBeforeOpen && Req.hookXhrConfig.onBeforeOpen(args) || args
      const xhr = this;
      this.reqConf = {
        id: guid(),
        type: 'xhr',
        requestInfo: {
          method: args[0].toUpperCase(),
          url: args[1]
        }
      }

      xhr.addEventListener('readystatechange', function (e) {
        switch (xhr.readyState) {
          case 2:
            // this.headersReceived();
          break;
          case 4:
            Req.handleDone(xhr);
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
  
      originOpen.apply(this, args);
    };

    window.XMLHttpRequest.prototype.send = function(){
      console.log('sendsendsendsendsend')
      if (arguments.length) {
        this.reqConf.requestInfo.body = arguments[0]
      }
      Req.emit('REQUEST.SEND', this.reqConf)
      originSend.apply(this, arguments);
    }

    // fetch
    const origFetch = window.fetch;
    window.fetch = function (...args) {
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
      const fetchResult = origFetch(...args);

      fetchResult.then(res => {
        res.clone().text().then(r=> {
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
    console.log('handledone')
    const update = () => {
      console.log('update', {
        id: xhr.reqConf.id,
        responseInfo: {
          resRaw: resTxt,
          contentType: xhr.getResponseHeader('Content-Type'),
          status: xhr.status,
        }
      })
      this.emit('REQUEST.DONE', {
        id: xhr.reqConf.id,
        responseInfo: {
          resRaw: resTxt,
          contentType: xhr.getResponseHeader('Content-Type'),
          status: xhr.status,
        }
      })
      console.log('after update')
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

}