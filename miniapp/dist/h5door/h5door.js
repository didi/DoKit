"use strict";

function _toConsumableArray(t) {
    return _arrayWithoutHoles(t) || _iterableToArray(t) || _nonIterableSpread()
}

function _nonIterableSpread() {
    throw new TypeError("Invalid attempt to spread non-iterable instance")
}

function _iterableToArray(t) {
    if (Symbol.iterator in Object(t) || "[object Arguments]" === Object.prototype.toString.call(t)) return Array.from(t)
}

function _arrayWithoutHoles(t) {
    if (Array.isArray(t)) {
        for (var r = 0, e = new Array(t.length); r < t.length; r++) e[r] = t[r];
        return e
    }
}

Page({
    data: {qrCodeUrl: "", historyUrlList: [], isShowWebView: !1}, onLoad: function () {
        this.getHistoryUrlList()
    }, getHistoryUrlList: function () {
        var t = [], r = wx.getStorageSync("h5door-url");
        t = r ? t.concat(r.split(",")) : [], this.setData({historyUrlList: t})
    }, setQrCode: function (t) {
        this.setData({qrCodeUrl: t.target.dataset.qrCode})
    }, qrCodeArouse: function () {
        var r = this, t = {
            scanType: ["qrCode"], success: function (t) {
                r.setData({qrCodeUrl: t.result}), r.goWebview()
            }
        };
        wx.scanCode(t)
    }, textareaChange: function (t) {
        this.setData({qrCodeUrl: t.detail.value})
    }, clearAll: function () {
        wx.clearStorageSync(), this.onLoad()
    }, addUrlToStorage: function () {
        var t = this.data.historyUrlList;
        if (t.push(this.data.qrCodeUrl), this.setData({historyUrlList: t}), 0 < this.data.historyUrlList.length) {
            var r = new Set(this.data.historyUrlList);
            this.setData({historyUrlList: _toConsumableArray(r)}), wx.setStorageSync("h5door-url", this.data.historyUrlList.join(","))
        }
    }, goWebview: function (t) {
        this.data.qrCodeUrl ? (this.addUrlToStorage(), this.setData({isShowWebView: !0})) : wx.showToast({title: "请输入跳转链接"})
    }
});
