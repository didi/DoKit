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

class Request extends EventEmitter{
  constructor(){
    super()
    this.hookFetchConfig = {}
    this.hookXhrConfig = {}
    this.initialize()
  }
  initialize(){
    let Req = this
    let {send:originSend, open:originOpen, setRequestHeader: originSetRequestHeader} = window.XMLHttpRequest.prototype;
    // XMLHttp
    window.XMLHttpRequest.prototype.setRequestHeader = function(){

      originSetRequestHeader.apply(this, arguments);
    }
    window.XMLHttpRequest.prototype.open = function (...args) {
      args = Req.hookXhrConfig.onBeforeOpen && Req.hookXhrConfig.onBeforeOpen(args) || args
      const xhr = this;
      this.reqConf = {
        id: guid(),
        type: 'xhr',
        requestInfo: {
          method: args[0],
          url: args[1]
        }
      }

      xhr.addEventListener('readystatechange', function () {
        switch (xhr.readyState) {
          case 2:
            // this.headersReceived();
          case 4:
            Req.handleDone(xhr);
        }
      });
  
      originOpen.apply(this, args);
    };

    window.XMLHttpRequest.prototype.send = function(){
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
          method: args.length > 1 ? (args[1].method || 'get') : 'get',
          headers: args.length > 1 ? (args[1].headers || {}) : {},
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

    const update = () => {
      this.emit('REQUEST.DONE', {
        id: xhr.reqConf.id,
        responseInfo: {
          resRaw: resTxt
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

}

// 单例，保证有且只有一个
export default new Request()