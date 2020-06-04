const {
    setSystemInfo,
    setUserInfo,
    setAuthInfo
} = require('./formatInfo.js');

Component({
    data: {
        systemInfo: [],
        accountInfo: [],
        userInfo: [],
        authInfo: []
    },
    lifetimes: {
        created () {
          console.log('debug created')
        },
        attached () {
            this.onGetSystemInfo();
            this.onGetAccountInfo();
            this.onGetUserInfo();
            this.onGetAuthInfo();
        },
        detached () {
          console.log('debug detached')
        }
    },
    methods: {
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
        },
        onGoBack () {
            this.triggerEvent('toggle', { componentType: 'dokit'})
        }
    }
})