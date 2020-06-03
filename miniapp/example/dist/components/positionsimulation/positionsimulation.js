const img = require('../../utils/imgbase64')
const app = getApp()
app.originGetLocation = wx.getLocation
Component({
    lifetimes: {
      created (){
      },
      attached () {
        this.getMyPosition()
      },
      detached (){
      }
    },
    data: {
        currentLatitude:0,
        currentLongitude:0,
        img
    },
    methods: {
        choosePosition (){
            wx.chooseLocation({
                success: res => {
                    this.setData({ currentLatitude: res.latitude });
                    this.setData({ currentLongitude: res.longitude })
                    Object.defineProperty(wx, 'getLocation', {
                        get(val) {
                            return function (obj) {
                                obj.success({latitude: res.latitude, longitude: res.longitude})
                            }
                        }
                    })
                }
            })
        },
        getMyPosition (){
            wx.getLocation({
                type: 'gcj02',
                success:res=> {
                    this.setData({currentLatitude:res.latitude}),
                    this.setData({currentLongitude:res.longitude})
                }
            })
        },
        openMyPosition (){
            wx.getLocation({
                type: 'gcj02',
                success (res) {
                  wx.openLocation({
                    latitude:res.latitude,
                    longitude:res.longitude,
                    scale: 18
                  })
                }
            })
        },
        resetPosition (){
            Object.defineProperty(wx, 'getLocation',
            {
                get(val) {
                    return app.originGetLocation
                }
            });
            wx.showToast({title:'还原成功！'})
            this.getMyPosition()
        },
        onGoBack () {
            this.triggerEvent('toggle', { componentType: 'dokit'})
        }
    }
  });
  