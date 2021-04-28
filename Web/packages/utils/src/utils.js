export const isObject = function(obj){
    return Object.prototype.toString.call(obj) === '[object Object]'
}

export const isFunction  = function(obj){
    return typeof obj === 'function'
}