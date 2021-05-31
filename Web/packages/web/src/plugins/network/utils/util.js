import { getDataType, getDataStructureStr } from '../../../assets/util'

export function isStr(str){
    let dataType = getDataType(str)
    return dataType === 'String'
}

export function startWith(str, prefix) {
    return str.indexOf(prefix) === 0;
};

export function isEmpty(val) {
    if (val == null) return true;

    if (isArrLike(val) && (isArr(val) || isStr(val) || isArgs(val))) {
        return val.length === 0;
    }

    return keys(val).length === 0;
}

const regSpace = /^\s+|\s+$/g;
export function trim(str, chars) {
    if (chars == null) return str.replace(regSpace, '');

    return ltrim(rtrim(str, chars), chars);
}

export function now(){
    if (Date.now) {
        return Date.now;
    } else {
        return new Date().getTime();
    }
}

//遍历object
export function each(obj, iterator, ctx) {
    iterator = optimizeCb(iterator, ctx);

    let i, len;

    if (isArrLike(obj)) {
        for (i = 0, len = obj.length; i < len; i++) iterator(obj[i], i, obj);
    } else {
        const _keys = keys(obj);
        for (i = 0, len = _keys.length; i < len; i++) {
            iterator(obj[_keys[i]], _keys[i], obj);
        }
    }

    return obj;
};

export function toNum(val) {
    if (isNum(val)) return val;

    if (isObj(val)) {
        const temp = isFn(val.valueOf) ? val.valueOf() : val;
        val = isObj(temp) ? temp + '' : temp;
    }

    if (!isStr(val)) return val === 0 ? val : +val;

    return +val;
};

//令原函数fn只执行一次
export function once(fn) {
    var isFirst = true;
    return function () {
      if (isFirst) {
        isFirst = !isFirst;
        fn();
      }
    };
}  

//检查fn是否为原生函数
export function isNative(val) {
    if (!isObj(val)) return false;

    if (isFn(val)) return regIsNative.test(toSrc(val));

    // Detect host constructors (Safari > 4; really typed array specific)
    return regIsHostCtor.test(toSrc(val));
};

const hasOwnProperty = Object.prototype.hasOwnProperty;

const regIsNative = new RegExp(
    '^' +
        toSrc(hasOwnProperty)
            .replace(/[\\^$.*+?()[\]{}|]/g, '\\$&')
            .replace(
                /hasOwnProperty|(function).*?(?=\\\()| for .+?(?=\\\])/g,
                '$1.*?'
            ) +
        '$'
);

const regIsHostCtor = /^\[object .+?Constructor\]$/;