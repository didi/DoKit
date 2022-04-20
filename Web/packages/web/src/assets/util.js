import {
  Request,
  EventEmitter
} from "@dokit/web-utils";
import { ConsoleLogMap } from "../plugins/console/js/console";


export const request = new Request()

export const getDataType = function (arg) {
  if (arg === null) {
    return 'Null'
  }
  if (arg === undefined) {
    return 'Undefined'
  }
  return arg.constructor && arg.constructor.name || 'Object'
}

const MAX_DISPLAY_PROPERTY_NUM = 5

export const getDataStructureStr = function (arg, isFirstLevel) {
  let dataType = getDataType(arg)
  let str = ''
  switch (dataType) {
    case 'Number':
    case 'String':
    case 'Boolean':
    case 'RegExp':
    case 'Symbol':
    case 'Function':
      str = arg.toString()
      break;

    case 'Null':
    case 'Undefined':
      str = arg + ''
      break;
    case 'Array':
      break;
    case 'Object':
      try {
        str += '{'
        if (isFirstLevel) {
          let propertyNames = Object.getOwnPropertyNames(arg)
          let propertyNameStrs = propertyNames.map(key =>
            str += `${key}: ${getDataStructureStr(arg[key], false)}`
          )
          propertyNameStrs.join(',')
          if (propertyNameStrs.length > MAX_DISPLAY_PROPERTY_NUM) {
            str += ',...'
          }
        } else {
          str += '...'
        }
        str += '}'
      } catch (error) {
        console.log(error)
      }
      break;
    default:
      break;
  }
  return str
}

export const guid = function () {
  function S4() {
    return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1)
  }
  return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4())
}

export const $bus = new EventEmitter()

export const uuid = () => {
  var temp_url = URL.createObjectURL(new Blob());
  var uuid = temp_url.toString(); // blob:https://xxx.com/b250d159-e1b6-4a87-9002-885d90033be3
  URL.revokeObjectURL(temp_url);
  return uuid.substr(uuid.lastIndexOf("/") + 1);
}

export const debounce = (fn, wait, time) => {
  var previous = null
  var timer = null
  return function (...args) {
    var now = +new Date()
    var that = this
    if (!previous) previous = now
    if (now - previous > time) {
      clearTimeout(timer)
      fn.apply(that, args)
      previous = now
    } else {
      clearTimeout(timer)
      timer = setTimeout(function () {
        fn.apply(that, args)
      }, wait)
    }
  }
}

export function throttle(fn, wait) {
  let prev = 0
  return (...args) => {
    let now = +new Date()
    if (now - prev > wait) {
      fn.apply(this, args)
      prev = now
    }
  }
}
export function getDeviceType() {
  let u = navigator.userAgent.toLowerCase();
  let isAndroid = u.indexOf('Android') > -1 || u.indexOf('Adr') > -1; //判断是否是 android终端
  let isIOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/); //判断是否是 iOS终端
  if (isAndroid) {
    return 'android';
  } else if (isIOS) {
    return 'ios';
  } else {
    return 'pc';
  }
}
export function getOsVersion() {
  let version, ua = navigator.userAgent.toLowerCase();
  if (getDeviceType() === 'android') {
    version = /Android(.+?);/.exec(ua)
    return version[1].trim()
  } else if (getDeviceType() === 'ios') {
    if (ua.indexOf("like mac os x") > 0) {
      let reg = /os [\d._]*/gi;
      let verinfo = ua.match(reg);
      version = (verinfo + "").replace(/[^0-9|_.]/ig, "").replace(/_/ig, ".");
      return version
    }
  }
}