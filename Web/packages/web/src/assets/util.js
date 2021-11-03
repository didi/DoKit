import { Request,EventEmitter } from "@dokit/web-utils";


export const request = new Request()

export const getDataType = function (arg) {
  if (arg === null ) {
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
      str += '{'
      if (isFirstLevel) {
        let propertyNames = Object.getOwnPropertyNames(arg)
        let propertyNameStrs =  propertyNames.map(key =>
          str += `${key}: ${getDataStructureStr(arg[key], false)}`
        )
        propertyNameStrs.join(',')
        if(propertyNameStrs.length > MAX_DISPLAY_PROPERTY_NUM) {
          str+= ',...'
        }
      } else {
        str += '...'
      }
      
      str += '}'
      
      break;
    default:
      break;
  }
  return str
}

export const guid = function () {
  function S4() { 
    return (((1+Math.random())*0x10000)|0).toString(16).substring(1)
  }
  return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4()+S4())
}

export const $bus = new EventEmitter()

export const debounce = (fn, wait, time) => {
  var previous = null
  var timer = null
  return function () {
    var now = +new Date()
    if (!previous) previous = now
    if (now - previous > time) {
      clearTimeout(timer)
      fn()
      previous = now
    } else {
      clearTimeout(timer)
      timer = setTimeout(function () {
        fn()
      }, wait)
    }
  }
}
