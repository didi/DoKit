export const isObject = function (obj) {
  return Object.prototype.toString.call(obj) === '[object Object]'
}

export const isFunction = function (obj) {
  return typeof obj === 'function'
}

export const isMobile = function() {
  if (window.navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)) {
    return true
  }
  return false
}

export const getPartUrlByParam = (url, param) => {
  const reg = /^(?:([A-Za-z]+):)?(\/{0,3})([0-9.\-A-Za-z]+)(?::(\d+))?(?:\/([^?#]*))?(?:\?([^#]*))?(?:#(.*))?$/;
  const res = reg.exec(url)
  const fields = ['url', 'scheme', 'slash', 'host', 'port', 'path', 'query', 'hash'];
  return res[fields.indexOf(param)]
}

export const getQueryMap = (queryStr) => {
  if (!queryStr) {
    return null
  }
  let queryMap = {}
  let queryList = queryStr.split('&')
  queryList.forEach(query => {
    if (query) {
      queryMap[query.split('=')[0]] = query.split('=')[1]
    }
  });
  return queryMap
}

export const guid = function () {
  function S4() { 
    return (((1+Math.random())*0x10000)|0).toString(16).substring(1)
  }
  return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4())
}