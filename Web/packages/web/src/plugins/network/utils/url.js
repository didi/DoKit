// export default Class(
//     {
//         className: 'Url',
//         initialize(url) {
//             if (!url && isBrowser) url = window.location.href;
//             extend(this, exports.parse(url || ''));
//         },
//         setQuery(name, val) {
//             const query = this.query;

//             if (isObj(name)) {
//                 each(name, function(val, key) {
//                     query[key] = val;
//                 });
//             } else {
//                 query[name] = val;
//             }

//             return this;
//         },
//         rmQuery(name) {
//             const query = this.query;

//             if (!isArr(name)) name = toArr(name);
//             each(name, function(key) {
//                 delete query[key];
//             });

//             return this;
//         },
//         toString() {
//             return exports.stringify(this);
//         }
//     },
//     {
//         parse(url) {
//             const ret = {
//                 protocol: '',
//                 auth: '',
//                 hostname: '',
//                 hash: '',
//                 query: {},
//                 port: '',
//                 pathname: '',
//                 slashes: false
//             };
//             let rest = trim(url);
//             let slashes = false;

//             let proto = rest.match(regProto);
//             if (proto) {
//                 proto = proto[0];
//                 ret.protocol = proto.toLowerCase();
//                 rest = rest.substr(proto.length);
//             }

//             if (proto) {
//                 slashes = rest.substr(0, 2) === '//';
//                 if (slashes) {
//                     rest = rest.slice(2);
//                     ret.slashes = true;
//                 }
//             }

//             if (slashes) {
//                 let host = rest;
//                 let hostEnd = -1;
//                 for (let i = 0, len = hostEndingChars.length; i < len; i++) {
//                     const pos = rest.indexOf(hostEndingChars[i]);
//                     if (pos !== -1 && (hostEnd === -1 || pos < hostEnd))
//                         hostEnd = pos;
//                 }

//                 if (hostEnd > -1) {
//                     host = rest.slice(0, hostEnd);
//                     rest = rest.slice(hostEnd);
//                 }

//                 const atSign = host.lastIndexOf('@');

//                 if (atSign !== -1) {
//                     ret.auth = decodeURIComponent(host.slice(0, atSign));
//                     host = host.slice(atSign + 1);
//                 }

//                 ret.hostname = host;
//                 let port = host.match(regPort);
//                 if (port) {
//                     port = port[0];
//                     if (port !== ':') ret.port = port.substr(1);
//                     ret.hostname = host.substr(0, host.length - port.length);
//                 }
//             }

//             const hash = rest.indexOf('#');

//             if (hash !== -1) {
//                 ret.hash = rest.substr(hash);
//                 rest = rest.slice(0, hash);
//             }

//             const queryMark = rest.indexOf('?');

//             if (queryMark !== -1) {
//                 ret.query = query.parse(rest.substr(queryMark + 1));
//                 rest = rest.slice(0, queryMark);
//             }

//             ret.pathname = rest || '/';

//             return ret;
//         },
//         stringify(obj) {
//             let ret =
//                 obj.protocol +
//                 (obj.slashes ? '//' : '') +
//                 (obj.auth ? encodeURIComponent(obj.auth) + '@' : '') +
//                 obj.hostname +
//                 (obj.port ? ':' + obj.port : '') +
//                 obj.pathname;

//             if (!isEmpty(obj.query)) ret += '?' + query.stringify(obj.query);
//             if (obj.hash) ret += obj.hash;

//             return ret;
//         }
//     }
// );

// const regProto = /^([a-z0-9.+-]+:)/i;
// const regPort = /:[0-9]*$/;
// const hostEndingChars = ['/', '?', '#'];

//类似 decodeURIComponent 函数，只是输入不合法时不抛出错误并尽可能地对其进行解码。
export function decodeUriComponent(str) {
    try {
        return decodeURIComponent(str);
    } catch (e) {
        const matches = str.match(regMatcher);

        if (!matches) {
            return str;
        }

        each(matches, function(match) {
            str = str.replace(match, decode(match));
        });

        return str;
    }
};

function decode(str) {
    str = str.split('%').slice(1);

    const bytes = map(str, hexToInt);

    str = ucs2.encode(bytes);
    str = utf8.decode(str, true);

    return str;
}

function hexToInt(numStr) {
    return +('0x' + numStr);
}

const regMatcher = /(%[a-f0-9]{2})+/gi;
