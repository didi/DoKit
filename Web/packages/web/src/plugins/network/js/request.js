// import {EventEmitter} from '@dokit/web-core'
import { last } from '../utils/arr'
// import Url from '../utils/url'
import { isStr, isEmpty, trim, now, each, startWith, toNum } from '../utils/util'
import { createId } from '../utils/uniqId'

export class XhrRequest{
  constructor(xhr, method, url) {
    // super();

    this.xhr = xhr;
    this.reqHeaders = {};
    this.method = method;
    this.url = fullUrl(url);
    this.id = createId();
  }
  
  handleSend(data) {
    if (!isStr(data)) data = '';

    data = {
      name: getFileName(this.url),
      url: this.url,
      data,
      time: now(),
      reqHeaders: this.reqHeaders,
      method: this.method,
    };
    if (!isEmpty(this.reqHeaders)) {
      data.reqHeaders = this.reqHeaders;
    }
    this.emit('send', this.id, data);
  }

  handleReqHeadersSet(key, val) {
    if (key && val) {
      this.reqHeaders[key] = val;
    }
  }

  handleHeadersReceived() {
    const { xhr } = this;

    const type = getType(xhr.getResponseHeader('Content-Type') || '');
    this.emit('headersReceived', this.id, {
      type: type.type,
      subType: type.subType,
      size: getSize(xhr, true, this.url),
      time: now(),
      resHeaders: getHeaders(xhr),
    });
  }

  handleDone() {
    const xhr = this.xhr;
    const resType = xhr.responseType;
    let resTxt = '';

    const update = () => {
      this.emit('done', this.id, {
        status: xhr.status,
        size: getSize(xhr, false, this.url),
        time: now(),
        resTxt,
      });
    };

    const type = getType(xhr.getResponseHeader('Content-Type') || '');
    if (
      resType === 'blob' &&
      (type.type === 'text' ||
        type.subType === 'javascript' ||
        type.subType === 'json')
    ) {
      readBlobAsText(xhr.response, (err, result) => {
        if (result) resTxt = result;
        update();
      });
    } else {
      if (resType === '' || resType === 'text') resTxt = xhr.responseText;
      if (resType === 'json') resTxt = JSON.stringify(xhr.response);

      update();
    }
  }
}

function getHeaders(xhr) {
  const raw = xhr.getAllResponseHeaders();
  const lines = raw.split('\n');

  const ret = {};

  each(lines, line => {
    line = trim(line);

    if (line === '') return;

    const [key, val] = line.split(':', 2);

    ret[key] = trim(val);
  });

  return ret;
}

function getSize(xhr, headersOnly, url) {
  let size = 0;

  function getStrSize() {
    if (!headersOnly) {
      const resType = xhr.responseType;
      let resTxt = '';

      if (resType === '' || resType === 'text') resTxt = xhr.responseText;
      if (resTxt) size = lenToUtf8Bytes(resTxt);
    }
  }

  if (isCrossOrig(url)) {
    getStrSize();
  } else {
    try {
      size = toNum(xhr.getResponseHeader('Content-Length'));
    } catch (e) {
      getStrSize();
    }
  }

  if (size === 0) getStrSize();

  return size;
}

const link = document.createElement('a');

export function fullUrl(href) {
  link.href = href;

  return (
    link.protocol + '//' + link.host + link.pathname + link.search + link.hash
  );
}

function getFileName(url) {
  let ret = last(url.split('/'));

  if (ret.indexOf('?') > -1) ret = trim(ret.split('?')[0]);

  if (ret === '') {
    // const urlObj = new Url(url);
    // ret = urlObj.hostname;
    ret = "";
  }

  return ret;
}

function getType(contentType) {
  if (!contentType)
    return {
      type: 'unknown',
      subType: 'unknown',
    };

  const type = contentType.split(';')[0].split('/');

  return {
    type: type[0],
    subType: last(type),
  };
}

function readBlobAsText(blob, callback) {
  const reader = new FileReader();
  reader.onload = () => {
    callback(null, reader.result);
  };
  reader.onerror = err => {
    callback(err);
  };
  reader.readAsText(blob);
}

const origin = window.location.origin;

function isCrossOrig(url) {
  return !startWith(url, origin);
}

function lenToUtf8Bytes(str) {
  const m = encodeURIComponent(str).match(/%[89ABab]/g);

  return str.length + (m ? m.length : 0);
}



// 下面两句先不要，类不实现单例，在network.js，enable()中重写open()/send()用once()来实现只调用一次
// // 单例，保证有且只有一个
// export default new Request()
