// export const XHR_NETWORK_METHODS = ["open", "send", 'setRequestHeader']

// export const origNetwork = {}
// export const noop = () => {}
// export const overrideNetwork = function(callback) {
//   const winXhrProto = window.XMLHttpRequest.prototype;
//   XHR_NETWORK_METHODS.forEach((name) => {
//     let origin = (origNetwork[name] = noop)
//     if (winXhrProto[name]) {
//       origin = origNetwork[name] = winXhrProto[name].bind(winXhrProto)
//     }

//     winXhrProto[name] = (...args) => {
//       callback({
//         name: name,
//         type: ConsoleLogMap[name],
//         value: args
//       })
//       origin(...args)
//     }
//   })
// }



// export const restoreNetwork = function(){
//   const winXhrProto = window.XMLHttpRequest.prototype;
//   console.log(winXhrProto);
//   XHR_NETWORK_METHODS.forEach((name) => {
//     winXhrProto[name] = origNetwork[name]
//   })
// }

// const resTxtMap = new Map();

// export const overrideNetwork = once(function () {
//   const winXhrProto = window.XMLHttpRequest.prototype;

//   const origSend = winXhrProto.send;
//   const origOpen = winXhrProto.open;
//   const origSetRequestHeader = winXhrProto.setRequestHeader;

//   winXhrProto.open = function (method, url) {
//     const xhr = this;
//     xhr.chiiRequest = {
//         xhr: xhr,
//         method: method,
//         url: fullUrl(url),
//         id: createId(),
//         reqHeaders: {},
//     }
 
//     const req = xhr.chiiRequest = new XhrRequest(xhr, method, url);


//     req.on('send', (id, data) => {
//       const request = {
//         method: data.method,
//         url: data.url,
//         headers: data.reqHeaders,
//       };
//       if (data.data) {
//         request.postData = data.data;
//       }

//       connector.trigger('Network.requestWillBeSent', {
//         requestId: id,
//         type: 'XHR',
//         request,
//         timestamp: data.time / 1000,
//       });
//     });
//     req.on('headersReceived', (id, data) => {
//       connector.trigger('Network.responseReceivedExtraInfo', {
//         requestId: id,
//         blockedCookies: [],
//         headers: data.resHeaders,
//       });
//     });
//     req.on('done', (id, data) => {
//       connector.trigger('Network.responseReceived', {
//         requestId: id,
//         type: 'XHR',
//         response: {
//           status: data.status,
//         },
//         timestamp: data.time / 1000,
//       });
//       resTxtMap.set(id, data.resTxt);
//       connector.trigger('Network.loadingFinished', {
//         requestId: id,
//         encodedDataLength: data.size,
//         timestamp: data.time / 1000,
//       });
//     });

//     xhr.addEventListener('readystatechange', function () {
//       switch (xhr.readyState) {
//         case 2:
//           return req.handleHeadersReceived();
//         case 4:
//           return req.handleDone();
//       }
//     });

//     origOpen.apply(this, arguments);
//   };

//   winXhrProto.send = function (data) {
//     const req = (this as any).chiiRequest;
//     if (req) req.handleSend(data);

//     origSend.apply(this, arguments);
//   };

//   winXhrProto.setRequestHeader = function (key, val) {
//     const req = this.chiiRequest;
//     if (req) {
//       req.handleReqHeadersSet(key, val);
//     }

//     origSetRequestHeader.apply(this, arguments);
//   };

//   let isFetchSupported = false;
//   if (window.fetch) isFetchSupported = isNative(window.fetch);
//   if (!isFetchSupported) return;

//   const origFetch = window.fetch;

//   window.fetch = function (...args) {
//     const req = new FetchRequest(...args);
//     req.on('send', (id, data) => {
//       const request = {
//         method: data.method,
//         url: data.url,
//         headers: data.reqHeaders,
//       };

//       if (data.data) {
//         request.postData = data.data;
//       }

//       connector.trigger('Network.requestWillBeSent', {
//         requestId: id,
//         type: 'Fetch',
//         request,
//         timestamp: data.time / 1000,
//       });
//     });
//     req.on('done', (id, data) => {
//       connector.trigger('Network.responseReceived', {
//         requestId: id,
//         type: 'Fetch',
//         response: {
//           status: data.status,
//           headers: data.resHeaders,
//         },
//         timestamp: data.time / 1000,
//       });
//       resTxtMap.set(id, data.resTxt);
//       connector.trigger('Network.loadingFinished', {
//         requestId: id,
//         encodedDataLength: data.size,
//         timestamp: data.time / 1000,
//       });
//     });

//     const fetchResult = origFetch(...args);
//     req.send(fetchResult);

//     return fetchResult;
//   };
// });

// export function getResponseBody(params) {
//   return {
//     base64Encoded: false,
//     body: resTxtMap.get(params.requestId),
//   };
// }

// const link = document.createElement('a');

// export function fullUrl(href) {
//   link.href = href;

//   return (
//     link.protocol + '//' + link.host + link.pathname + link.search + link.hash
//   );
// }

export const origNetwork = {}
export const noop = () => {}
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
    value: request
  })
}