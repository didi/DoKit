const isObject = function(obj){
    return Object.prototype.toString.call(obj) === '[object Object]'
};

const isFunction  = function(obj){
    return typeof obj === 'function'
};

const inBrowser = typeof window !== 'undefined';

export { inBrowser, isFunction, isObject };
