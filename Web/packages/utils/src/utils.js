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