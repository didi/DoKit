export const overrideLocalStorage = function(callback) {
  const originSetItem = localStorage.setItem.bind(localStorage);
  localStorage.setItem = function (key, value) {
    // if (!isStr(key) || !isStr(value)) return;
    originSetItem(key, value);
    callback({type: 'setItem'})
  };

  const originRemoveItem = localStorage.removeItem.bind(localStorage);
  localStorage.removeItem = function (key) {
    originRemoveItem(key);
    callback({type: 'removeItem'})
  };

  const originClear = localStorage.clear.bind(localStorage);
  localStorage.clear = function (key) {
    originClear();
    callback({type: 'clear'})
  };
}

export const overrideSessionStorage = function(callback) {
  const originSetItem = sessionStorage.setItem.bind(sessionStorage);
  sessionStorage.setItem = function (key, value) {
    // if (!isStr(key) || !isStr(value)) return;
    originSetItem(key, value);
    callback({type: 'setItem'})
  };

  const originRemoveItem = sessionStorage.removeItem.bind(sessionStorage);
  sessionStorage.removeItem = function (key) {
    originRemoveItem(key);
    callback({type: 'removeItem'})
  };

  const originClear = sessionStorage.clear.bind(sessionStorage);
  sessionStorage.clear = function (key) {
    originClear();
    callback({type: 'clear'})
  };
}

export const clearCookie = function () {
  let cookieMap = getCookieMap()
  for (const key in cookieMap) {
    if (cookieMap.hasOwnProperty.call(cookieMap, key)) {
      removeCookieItem(key)
    }
  }
}

export const removeCookieItem = function (key) {
  document.cookie = encodeURIComponent(key) + "=; expires=Thu, 01 Jan 1970 00:00:00 GMT";
}

export const getCookieMap = function (params) {
  const cookieMap = Object.create({})
  const cookie = document.cookie;
  if (cookie.trim() !== '') {
    cookie.split(';').forEach(ele => {
      ele = ele.split('=');
      const key = ele.shift().trim();
      ele = decodeURIComponent(ele.join('='));
      cookieMap[key] = ele
    })
  }

  return cookieMap;
}

