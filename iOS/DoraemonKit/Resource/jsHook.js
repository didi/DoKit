; (function () {

    if (window.dokitJsi) { return; }

    var win = window;
    var messageHandlers = {};
    var responseCallbacks = {};
    var sendMessageQueue = [];
    var uniqueId = 0;

    function registerHandler(handlerName, handler) {
        messageHandlers[handlerName] = handler;
    }

    function invoke(command, msg, callback) {
        var message = { params: msg };

        message['command'] = command;

        if (callback) {
            var callbackId = 'callbackId_' + (uniqueId++) + '_' + new Date().getTime();
            responseCallbacks[callbackId] = callback;
            message['callbackId'] = callbackId;
        }

        sendMessageQueue.push(message);

        var messageQueueString = JSON.stringify(sendMessageQueue);
        sendMessageQueue = [];

        window.webkit.messageHandlers.handleJSMessage.postMessage(messageQueueString);
    }

    function receiveMessageFromNative(messageJSON) {
        setTimeout(function handleMessageFromNative() {
            var message = JSON.parse(messageJSON);
            var responseCallback;

            if (message.responseId) {
                responseCallback = responseCallbacks[message.responseId];

                if (!responseCallback) {
                    return;
                }

                responseCallback(message.params);
                delete responseCallbacks[message.responseId];
            }
            else {
                if (message.callbackId) {
                    var callbackResponseId = message.callbackId;
                    responseCallback = function (params) {
                        params['responseId'] = callbackResponseId;
                        invoke('', params, '');
                    };
                }

                var handler = messageHandlers[message.handlerName];

                if (!handler) {
                    console.log("JavascriptBridge WARNING: no handler for message from ObjC:", message);
                }
                else {
                    handler(message.params, responseCallback);
                }
            }
        });
    }

    win.dokitJsi = {
        registerHandler: registerHandler,
        invoke: invoke,
        receiveMessageFromNative: receiveMessageFromNative,
    };

    function generateRandom() {
        return Math.floor((1 + Math.random()) * 0x10000)
            .toString(16)
            .substring(1);
    }

    var requestID = null;
    //hook XMLHttpRequest open method
    XMLHttpRequest.prototype.reallyOpen = XMLHttpRequest.prototype.open;
    XMLHttpRequest.prototype.open = function (method, url, async, user, password) {
        //console.log(url)
        requestID = generateRandom();
        var hasQuery = url.split("?")[1];
        var urlWrap = null;
        if (hasQuery) {
            urlWrap = url + "&" + "dokit_flag=" + requestID
        } else {
            urlWrap = url + "?" + "dokit_flag=" + requestID
        }
        dokitJsi.invoke('open', {
            requestID: requestID,
            urlWrap: urlWrap,
            method: method,
            origin: window.location.origin,
        }, null)
        this.reallyOpen(method, urlWrap, async, user, password);
    };

    //hook XMLHttpRequest setRequestHeader method
    XMLHttpRequest.prototype.reallySetRequestHeader = XMLHttpRequest.prototype.setRequestHeader;
    XMLHttpRequest.prototype.setRequestHeader = function (header, value) {
        dokitJsi.invoke('setRequestHeader', {
            requestID: requestID,
            header: header,
            value: value,
        }, null)
        this.reallySetRequestHeader(header, value);
    };

    //hook XMLHttpRequest overrideMimeType method
    XMLHttpRequest.prototype.reallyOverrideMimeType = XMLHttpRequest.prototype.overrideMimeType;
    XMLHttpRequest.prototype.overrideMimeType = function (mimeType) {
        dokitJsi.invoke('overrideMimeType', {
            requestID: requestID,
            mimeType: mimeType,
        }, null)
        this.reallyOverrideMimeType(mimeType);
    };

    //hook XMLHttpRequest send method
    XMLHttpRequest.prototype.reallySend = XMLHttpRequest.prototype.send;
    XMLHttpRequest.prototype.send = function (body) {
        dokitJsi.invoke('send', {
            requestID: requestID,
            body: body,
        }, null)
        this.reallySend(body);
    };

    //hook Storage
    //保存一个值
    Storage.prototype.reallySetItem = Storage.prototype.setItem;
    Storage.prototype.setItem = function (key, value) {
        if (this === localStorage) {
            dokitJsi.invoke('localStorageSetItem', {
                key: key,
                value: value,
            }, null)
        } else if (this === sessionStorage) {
            dokitJsi.invoke('sessionStorageSetItem', {
                key: key,
                value: value,
            }, null)
        }
        this.reallySetItem(key, value);
    };

    //移除一个值
    Storage.prototype.reallyRemoveItem = Storage.prototype.removeItem;
    Storage.prototype.removeItem = function (key) {
        if (this === localStorage) {
            dokitJsi.invoke('localStorageRemoveItem', {
                key: key,
            }, null)
        } else if (this === sessionStorage) {
            dokitJsi.invoke('sessionStorageRemoveItem', {
                key: key,
            }, null)
        }

        this.reallyRemoveItem(key);
    };

    //清空
    Storage.prototype.reallyClear = Storage.prototype.clear;
    Storage.clear = function () {
        if (this === localStorage) {
            dokitJsi.invoke('localStorageClear', {}, null)
        } else if (this === sessionStorage) {
            dokitJsi.invoke('sessionStorageClear', {}, null)
        }

        this.reallyClear();
    };

    //遍历并输出localStorage里存储的名字和值
    var localStorageDic = new Map()
    for (var i = 0; i < localStorage.length; i++) {
        var key = localStorage.key(i)
        var value = localStorage.getItem(localStorage.key(i))
        localStorageDic[key] = value
    }
    dokitJsi.invoke('synchronizeLocalStorage', localStorageDic, null)

    //遍历并输出sessionStorage里存储的名字和值
    var sessionStorageDic = new Map()
    for (var i = 0; i < sessionStorage.length; i++) {
        var key = sessionStorage.key(i)
        var value = sessionStorage.getItem(sessionStorage.key(i))
        sessionStorageDic[key] = value
    }
    dokitJsi.invoke('synchronizeSessionStorage', sessionStorageDic, null)
})();
