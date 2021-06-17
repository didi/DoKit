import { EventEmitter } from './eventEmiter'

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
    // TODO 增加请求拦截器，增加单测
    // XMLHttp
    window.XMLHttpRequest.prototype.setRequestHeader = function(){

      originSetRequestHeader.apply(this, arguments);
    }
    window.XMLHttpRequest.prototype.open = function (...args) {
      args = Req.hookXhrConfig.onBeforeOpen && Req.hookXhrConfig.onBeforeOpen(args) || args
      const xhr = this;
      xhr.addEventListener('readystatechange', function () {
        switch (xhr.readyState) {
          case 2:
            // this.headersReceived();
          case 4:
            // this.handleDone();
        }
      });
  
      originOpen.apply(this, args);
    };

    window.XMLHttpRequest.prototype.send = function(){
      originSend.apply(this, arguments);
    }

    // fetch
    const origFetch = window.fetch;
    window.fetch = function (...args) {
      args = Req.hookFetchConfig.onBeforeFetch && Req.hookFetchConfig.onBeforeFetch(args) || args
      const fetchResult = origFetch(...args);
      return fetchResult;
    };
    // 触发事件
    
  }

  hookXhr(hookXhrConfig) {
    this.hookXhrConfig = hookXhrConfig
  }

  hookFetch(hookFetchConfig) {
    this.hookFetchConfig = hookFetchConfig
  }

}

// 单例，保证有且只有一个
export default new Request()