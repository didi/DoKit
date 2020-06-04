//index.js
//获取应用实例
const app = getApp()

Page({
  data: {
    motto: 'Hello World',
    userInfo: {},
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },
  //事件处理函数
  bindViewTap: function() {
    wx.navigateTo({
      url: '../logs/logs'
    })
  },
  sendRequest: function() {
    wx.request({
      url: 'http://jsonplaceholder.typicode.com/users/2?app=name',
      success: (res) => {
        this.setData({
          motto: JSON.stringify(res.data)
        })
      }
    })
  },
  sendDokitRequest: function () {
    // Tip: 小程序测试
    wx.request({
      url: 'https://mock.dokit.cn/api/app/interface?name=zzy',
      method: 'GET',
      success: (res) => {
        console.log('用户自定义',res.data)
      }
    })
    // Tip: 小程序测试4-post加参数
    // wx.request({
    //   url: 'https://mock.dokit.cn/users/jtsky_copy_copy_copy',
    //   data: '{name: aa}',
    //   method: 'POST',
    //   success: (res) => {
    //     console.log('用户自定义',res.data)
    //   }
    // })
  },
  onLoad: function () {
    if (app.globalData.userInfo) {
      this.setData({
        userInfo: app.globalData.userInfo,
        hasUserInfo: true
      })
    } else if (this.data.canIUse){
      // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
      // 所以此处加入 callback 以防止这种情况
      app.userInfoReadyCallback = res => {
        this.setData({
          userInfo: res.userInfo,
          hasUserInfo: true
        })
      }
    } else {
      // 在没有 open-type=getUserInfo 版本的兼容处理
      wx.getUserInfo({
        success: res => {
          app.globalData.userInfo = res.userInfo
          this.setData({
            userInfo: res.userInfo,
            hasUserInfo: true
          })
        }
      })
    }
  },
  getUserInfo: function(e) {
    console.log(e)
    app.globalData.userInfo = e.detail.userInfo
    this.setData({
      userInfo: e.detail.userInfo,
      hasUserInfo: true
    })
  }
})
