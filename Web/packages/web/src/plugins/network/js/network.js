import connector from './connector'
import { XhrRequest } from './request'
import { trim, each, once, isNative } from '../utils/util'
import { decodeUriComponent } from '../utils/url'
import { rmCookie } from '../utils/cookie'

const resTxtMap = new Map();

//重写xhr原生的open/send/setRequestHeader方法，并在请求对象的绑定事件中触发一些自己写好的逻辑
//如在xhr对象的send事件上，触发一个方法名为requestWillBeSent，参数为{requestId: id, ...}的方法
//将这些方法的具体实现与方法名绑定起来，详见bindEvents()
//而这些触发的方法的具体实现在events.js中
function sendMessage (method, params) {
    const id = uuid();

    this.sendRawMessage(
      JSON.stringify({
        id,
        method,
        params,
      })
    );

    return new Promise(resolve => {
      this.resolves.set(id, resolve);
    });
}


async function sendRawMessage(message) {
    const parsedMessage = JSON.parse(message);

    const { method, params, id } = parsedMessage;

    const resultMsg = {
      id,
    };

    try {
      resultMsg.result = await this.callMethod(method, params);
    } catch (e) {
      resultMsg.error = {
        message: e.message,
      };
    }

    connector.emit('message', JSON.stringify(resultMsg));
}


export const enable = once(function(){
    const domain = this;
    domain.on('requestWillBeSent', reqWillBeSent)
    domain.on('responseReceivedExtraInfo', resReceivedExtraInfo)
    domain.on('responseReceived', resReceived)
    domain.on('loadingFinished', loadingFinished)

    let {send:originSend, open:originOpen, setRequestHeader:origSetRequestHeader} = window.XMLHttpRequest.prototype;
    // TODO 增加请求拦截器，增加单测
    window.XMLHttpRequest.prototype.open = function(method, url){
      const xhr = this;

      const req = xhr.chiiRequest = new XhrRequest(xhr, method, url);

      //send是window.XMLHttpRequest.prototype自己的事件，on(event,listener)将监听器绑定到事件上
      // 这里绑定的就是一旦send，就触发Network.requestWillBeSent，将{requestId: id, type: 'XHR', ...}emit出去
      req.on('send', (id, data) => {
        const request = {
          method: data.method,
          url: data.url,
          headers: data.reqHeaders,
        };
        if (data.data) {
          request.postData = data.data;
        }

        domain.emit('requestWillBeSent', {
            requestId: id,
            type: 'XHR',
            request,
            timestamp: data.time / 1000,
        });

      });

      req.on('headersReceived', (id, data) => {
        domain.emit('responseReceivedExtraInfo', {
          requestId: id,
          blockedCookies: [],
          headers: data.resHeaders,
        });
      });

      req.on('done', (id, data) => {
        domain.emit('responseReceived', {
          requestId: id,
          type: 'XHR',
          response: {
            status: data.status,
          },
          timestamp: data.time / 1000,
        });
        resTxtMap.set(id, data.resTxt);
        domain.emit('loadingFinished', {
          requestId: id,
          encodedDataLength: data.size,
          timestamp: data.time / 1000,
        });
      });

      xhr.addEventListener('readystatechange', function () {
        switch (xhr.readyState) {
          case 2:
            return req.handleHeadersReceived();
          case 4:
            return req.handleDone();
        }
      });

      originOpen.apply(this, arguments);
    }

    window.XMLHttpRequest.prototype.send = function(data){
      const req = this.chiiRequest;
      if (req) req.handleSend(data);
      originSend.apply(this, arguments);
    }

    window.XMLHttpRequest.prototype.setRequestHeader = function (key, val) {
      const req = this.chiiRequest;
      if (req) {
        req.handleReqHeadersSet(key, val);
      }
    
      origSetRequestHeader.apply(this, arguments);
    };
});

export function deleteCookies(params) {
  rmCookie(params.name);
}
  
export function getCookies() {
  const cookies = [];
  
  const cookie = document.cookie;
  if (trim(cookie) !== '') {
    each(cookie.split(';'), function (value) {
      value = value.split('=');
      const name = trim(value.shift());
      value = decodeUriComponent(value.join('='));
      cookies.push({
        name,
        value,
      });
    });
  }
  
  return { cookies };
}  

export const restoreConsole = function(){
    const winConsole = window.console
    CONSOLE_METHODS.forEach((name) => {
      winConsole[name] = origConsole[name]
    })
}
  

export const mockData = function(callback) {
    const request = {
      name: "qqq",
      url: "https://www.modeben.com/free/week?appid=68852321&appsecret=BgGLDVc7",
      status: '200',
      type: 'plain',
      subType: '',
      size: 2,
      data: {},
      method: 'GET',
      startTime: 0,
      time: 139,
      resTxt: {
          "cityid": "101010100",
          "city": "\u5317\u4eac",
          "update_time": "2021-05-28 20:29:16",
          "data": [{
              "date": "2021-05-28",
              "wea": "\u9634\u8f6c\u591a\u4e91",
              "wea_img": "yun",
              "tem_day": "28",
              "tem_night": "16",
              "win": "\u5317\u98ce",
              "win_speed": "3-4\u7ea7"
          }, {
              "date": "2021-05-29",
              "wea": "\u591a\u4e91",
              "wea_img": "yun",
              "tem_day": "27",
              "tem_night": "15",
              "win": "\u4e1c\u5317\u98ce",
              "win_speed": "3-4\u7ea7\u8f6c<3\u7ea7"
          }, {
              "date": "2021-05-30",
              "wea": "\u591a\u4e91",
              "wea_img": "yun",
              "tem_day": "27",
              "tem_night": "14",
              "win": "\u4e1c\u5357\u98ce",
              "win_speed": "<3\u7ea7"
          }, {
              "date": "2021-05-31",
              "wea": "\u591a\u4e91",
              "wea_img": "yun",
              "tem_day": "26",
              "tem_night": "16",
              "win": "\u4e1c\u5357\u98ce",
              "win_speed": "<3\u7ea7"
          }, {
              "date": "2021-06-01",
              "wea": "\u9634\u8f6c\u96f7\u9635\u96e8",
              "wea_img": "yu",
              "tem_day": "29",
              "tem_night": "17",
              "win": "\u4e1c\u5357\u98ce",
              "win_speed": "3-4\u7ea7"
          }, {
              "date": "2021-06-02",
              "wea": "\u6674\u8f6c\u591a\u4e91",
              "wea_img": "yun",
              "tem_day": "28",
              "tem_night": "18",
              "win": "\u897f\u5357\u98ce",
              "win_speed": "3-4\u7ea7"
          }, {
              "date": "2021-06-03",
              "wea": "\u591a\u4e91\u8f6c\u6674",
              "wea_img": "yun",
              "tem_day": "29",
              "tem_night": "19",
              "win": "\u5317\u98ce",
              "win_speed": "4-5\u7ea7\u8f6c3-4\u7ea7"
          }]
      },
      done: false,
      reqHeaders: {
          
      },
      resHeaders: {
          "Content-Type": "application/json",
          "Content-Type1": "application/json",
          "Content-Type2": "application/json",
      },
      hasErr: false,
      response: {},
      headers: {},
      displayTime: 1,
    };
    callback({
      value: request,
    })
}

export const reqWillBeSent = function (params){
    let state = getGlobalData();
    state.reqList = state.reqList || [];
    state.detailData = state.detailData || [];

    state.reqList[params.requestId] = {
        name: getFileName(params.request.url),
        url: params.request.url,
        status: 'pending',
        type: 'unknown',
        subType: 'unknown',
        size: 0,
        data: params.request.postData,
        method: params.request.method,
        startTime: params.timestamp * 1000,
        time: 0,
        resTxt: '',
        done: false,
        reqHeaders: params.request.headers || {},
        resHeaders: {},
    }
}
  
export const resReceivedExtraInfo = function (params){
    let state = getGlobalData();
    state.reqList = state.reqList || [];
    state.detailData = state.detailData || [];
        
    const target = state.reqList[params.requestId]
        
    if (!target) {
        return
    }
        
    target.resHeaders = params.headers
        
    this._updateType(target)        
    // this._render()
}
  
export function updateType(target) {
    const contentType = target.resHeaders['content-type'] || ''
    const { type, subType } = getType(contentType)
    target.type = type
    target.subType = subType
}

export function getType(contentType) {
    if (!contentType) return 'unknown'
  
    const type = contentType.split(';')[0].split('/')
  
    return {
      type: type[0],
      subType: last(type),
    }
}
  
export const resReceived = function(params){
    let state = getGlobalData();
    state.reqList = state.reqList || [];
    state.detailData = state.detailData || [];

    const target = state.reqList[params.requestId]
    if (!target) {
      return
    }
  
    const { response } = params
    const { status, headers } = response
    target.status = status
    if (status < 200 || status >= 300) {
      target.hasErr = true
    }
    if (headers) {
      target.resHeaders = headers
      this.updateType(target)
    }
  
    // this._render()
}
  
export const loadingFinished = function(params){
    let state = getGlobalData();
    state.reqList = state.reqList || [];
    state.detailData = state.detailData || [];

    const target = state.reqList[params.requestId]
    if (!target) {
      return
    }
  
    const time = params.timestamp * 1000
    target.time = time - target.startTime
    target.displayTime = ms(target.time)
  
    target.size = params.encodedDataLength
    target.done = true
    target.resTxt = getResponseBody({
        requestId: params.requestId,
    }).body
  
    // this._render()
}

export function getResponseBody(params) {
    return {
      base64Encoded: false,
      body: resTxtMap.get(params.requestId),
    };
}