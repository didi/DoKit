const isObject = function(obj){
    return Object.prototype.toString.call(obj) === '[object Object]'
};

const isFunction  = function(obj){
    return typeof obj === 'function'
};

const isPC = function(){
  
};

export { isFunction, isObject, isPC };
