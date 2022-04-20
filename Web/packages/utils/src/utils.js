
import {EventEmitter} from './eventEmiter'
export const isObject = function (obj) {
  return Object.prototype.toString.call(obj) === '[object Object]'
}

export const isFunction = function (obj) {
  return typeof obj === 'function'
}

export const isMobile = function () {
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

export const $bus = new EventEmitter()

export const guid = function () {
  function S4() {
    return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
  }
  return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4())
}

export const HTTP_STATUS_CODES = {
  100: "Continue",
  101: "Switching Protocols",
  200: "OK",
  201: "Created",
  202: "Accepted",
  203: "Non-Authoritative Information",
  204: "No Content",
  205: "Reset Content",
  206: "Partial Content",
  300: "Multiple Choice",
  301: "Moved Permanently",
  302: "Found",
  303: "See Other",
  304: "Not Modified",
  305: "Use Proxy",
  307: "Temporary Redirect",
  400: "Bad Request",
  401: "Unauthorized",
  402: "Payment Required",
  403: "Forbidden",
  404: "Not Found",
  405: "Method Not Allowed",
  406: "Not Acceptable",
  407: "Proxy Authentication Required",
  408: "Request Timeout",
  409: "Conflict",
  410: "Gone",
  411: "Length Required",
  412: "Precondition Failed",
  413: "Request Entity Too Large",
  414: "Request-URI Too Long",
  415: "Unsupported Media Type",
  416: "Requested Range Not Satisfiable",
  417: "Expectation Failed",
  422: "Unprocessable Entity",
  500: "Internal Server Error",
  501: "Not Implemented",
  502: "Bad Gateway",
  503: "Service Unavailable",
  504: "Gateway Timeout",
  505: "HTTP Version Not Supported"
}

/**
 * 获取url参数
 * @param key 参数的key
 */
export const getQueryVariable = (key, url) => {
  if (url.indexOf('?') >= 0) {
    var query = url ? url.split('?')[1] : window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
      var pair = vars[i].split("=");
      if (pair[0] == key) {
        return pair[1];
      }
    }
  }
  return (false);
}

export const strMapToObj = (strMap) => {
  let obj = Object.create(null);
  for (let [k, v] of strMap) {
    obj[k] = v;
  }
  return obj;
}

export const completionUrlProtocol = (url) => {
  if (url.substr(0, 5).toLowerCase() == "http:" || url.substr(0, 6).toLowerCase() == "https:") {
    return url
  } else {
    return `${location.protocol}` + url;
  }
}