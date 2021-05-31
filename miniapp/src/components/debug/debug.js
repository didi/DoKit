const img = require('../../utils/imgbase64')
Component({
    data: {
        logs: [],
        tools: []
    },
    lifetimes: {
        attached () {
            console.log('debug attached')
            this.setData({
                tools: this.getTools()
            });
        }
    },
    methods: {
        getTools() {
            return [
                {
                    "type": "common",
                    "title": "常用工具",
                    "tools": [
                        {
                            "title": "App信息",
                            "image": img.appinfoicon,
                            "type": "appinformation"
                        },
                        {
                            "title": "位置模拟",
                            "image": img.gpsicon,
                            "type": "positionsimulation"
                        },
                        {
                            "title": "缓存管理",
                            "image": img.saveicon,
                            "type": "storage"
                        },
                        {
                            "title": "H5任意门",
                            "image": img.h5dooricon,
                            "type": "h5door"
                        },
                        {
                            "title": "请求注射",
                            "image": img.injectoricon,
                            "type": "httpinjector"
                        },
                        {
                            "title": "更新版本",
                            "image": img.updateversionicon,
                            "type": "onUpdate",
                        },
                        {
                            "title": "数据模拟",
                            "image": img.apimockicon,
                            "type": "apimock"
                        },
                        {

                            "title": "查看日志",
                            "image": img.apimockicon,
                            "type": "looklogs"
                        },
                        {
                            "title": "page任意门",
                            "image": img.h5dooricon,
                            "type": "pagedoor"
                        }
                    ]
                }
            ]
        },
        onUpdate () {
            const updateManager = wx.getUpdateManager();
            updateManager.onCheckForUpdate(function (res) {
                if(!res.hasUpdate) {
                    // 请求完新版本信息的回调
                    wx.showModal({
                        title: '更新提示',
                        content: '当前已经是最新版本'
                    })
                }
            });
            updateManager.onUpdateReady(function () {
                wx.showModal({
                    title: '更新提示',
                    content: '新版本已经准备好，是否重启应用？',
                    success(res) {
                        if (res.confirm) {
                            // 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
                            updateManager.applyUpdate()
                        }
                    }
                })
            });
            updateManager.onUpdateFailed(function () {
                // 新版本下载失败
            })
        },
        onToggle (event) {
            const type = event.currentTarget.dataset.type;
            if(type === 'onUpdate') {
                this[type]();
            } else {
                this.triggerEvent('toggle', { componentType: type })
            }
        },
        openSetting() {
            wx.openSetting({
                success: function(res) {
                    console.log(res);
                }
            })
        },
        onGoBack () {
            this.triggerEvent('toggle', { componentType: 'dokit'})
        }
    }
})
