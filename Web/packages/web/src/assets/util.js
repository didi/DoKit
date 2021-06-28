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

