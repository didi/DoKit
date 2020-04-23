//logs.js
const util = require('../utils/util.js');

Page({
    data: {
        logs: [],
        tools: []
    },
    onLoad: function () {
        this.setData({
            tools: this.getTools()
        });
    },
    getTools() {
        return [
            {
                "type": "common",
                "title": "常用工具",
                "tools": [
                    {
                        "title": "App信息",
                        "image": "../assets/img/appinfo-icon.png",
                        "path": "../appInformation/appInformation"
                    },
                    {
                        "title": "位置模拟",
                        "image": "../assets/img/gps-icon.png",
                        "path": "../positionSimulation/positionSimulation"
                    },
                    {
                        "title": "缓存管理",
                        "image": "../assets/img/save-icon.png",
                        "path": "../storage/storage"
                    },
                    {
                        "title": "H5任意门",
                        "image": "../assets/img/h5door-icon.png",
                        "path": "../h5door/h5door"
                    },
                    {
                        "title": "请求注射",
                        "image": "../assets/img/injector.png",
                        "path": "../httpInjector/httpInjector"
                    },
                    {
                        "title": "更新版本",
                        "image": "../assets/img/update-version-icon.png",
                        "type": "function",
                        "path": "onUpdate"
                    },
                    {
                        "title": "Api Mock",
                        "image": "../assets/img/api-mock.png",
                        "path": "../apimock/apimock"
                    }
                ]
            }
        ]
    },
    onUpdate () {
        const updateManager = wx.getUpdateManager();

        updateManager.onCheckForUpdate(function (res) {
            if(!res) {
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
    openSetting() {
        wx.openSetting({
            success: function(res) {
                console.log(res);
            }
        })
    },
    onRedirect: function (event) {
        const eventType = event.currentTarget.dataset.type;
        const path = event.currentTarget.dataset.path;
        if(eventType === 'function') {
            this[path]();
        }else {
            util.goToLink(path)
        }
    }
});
