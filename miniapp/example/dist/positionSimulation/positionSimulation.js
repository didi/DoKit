const app = getApp()
Page({
    data: {
        currentLatitude:0,
        currentLongitude:0
    },
    onLoad () {
        this.getMyPosition()
    },
    choosePosition() {
        wx.chooseLocation({
            success: res => {
                this.setData({currentLatitude: res.latitude});
                this.setData({currentLongitude: res.longitude})
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
    getMyPosition(){
        wx.getLocation({
            type: 'gcj02',
            success:res=> {
                this.setData({currentLatitude:res.latitude}),
                this.setData({currentLongitude:res.longitude})
            }
        })
    },
    openMyPosition(){
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
    resetPosition() {
        Object.defineProperty(wx, 'getLocation',
        {
            get(val) {
                return app.globalData.getLocation
            }
        });
        wx.showToast({title:'还原成功！'})
        this.onLoad();
    }
});
