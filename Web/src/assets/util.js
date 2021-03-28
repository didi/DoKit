export const getDataType = function (arg) {
  if (arg === null ) {
    return 'Null'
  }
  if (arg === undefined) {
    return 'Undefined'
  }
  return arg.constructor && arg.constructor.name || 'Object'
}

export const getDataStructureStr = function (arg) {
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
      Object.getOwnPropertyNames(arg)
      break;
    default:
      break;
  }
  return str
}