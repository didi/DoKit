//logs.js
const {
    setSystemInfo,
    setUserInfo,
    setAuthInfo
} = require('./formatInfo.js');

Page({
    data: {
        systemInfo: [],
        accountInfo: [],
        userInfo: [],
        authInfo: []
    },
    onLoad: function () {
        this.onGetSystemInfo();
        this.onGetAccountInfo();
        this.onGetUserInfo();
        this.onGetAuthInfo();
    },
    onGetSystemInfo () {
        const that = this;
        wx.getSystemInfo({
            success (res) {
                const systemInfo = setSystemInfo(res);
                that.setData({systemInfo});
            }
        })
    },
    onGetAccountInfo () {
        const accountInfo = [
            {
                name: "小程序appid",
                value: wx.getAccountInfoSync().miniProgram.appId
            }
        ];
        this.setData({
            accountInfo
        })
    },
    onGetUserInfo () {
        const that = this;
        wx.getUserInfo({
            success: function(res) {
                const userInfo = setUserInfo(res.userInfo);
                that.setData({
                    userInfo
                });
            }
        });
    },
    onGetAuthInfo () {
        const that = this;
        wx.getSetting({
            success (res) {
                const authInfo = setAuthInfo(res.authSetting);
                that.setData({
                    authInfo
                });
            }
        })
    }
});
