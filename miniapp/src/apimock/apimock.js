//logs.js
// const util = require('../utils/util.js');
const app = getApp()
app.globalData.originRequest = wx.request
const baseUrl = 'https://mock.dokit.cn'

Page({
    data: {
        isMock: false,
        mockList: []
    },
    onLoad: function () {
        // 初始化获取
        this.initMockList()
    },
    initMockList () {
        const that = this
        app.globalData.originRequest({
            url: `${baseUrl}/api/app/interface`,
            method: 'GET',
            data: {
                projectId: '749a0600b5e48dd77cf8ee680be7b1b7',
            },
            success: function (res) {
                console.log(res)
                const { data } = res.data
                if (data && data.datalist && data.datalist.length) {
                    const mockList = data.datalist.map(item => ({
                        ...item,
                        hidden: true,
                        checked: false
                    }))
                    that.setData({
                        mockList: mockList
                    }, () => {
                        console.log('data.mockList', that.data.mockList)
                    })
                }
            }
        })
    },
    toggleMockState() {
        this.setData({
            isMock: !this.data.isMock
        }, () => {
            this.data.isMock && this.addRequestHooks()
        })
    },
    onTapbar (event) {
        const type = event.target.dataset.type
    },
    onExpand (evnet) {
        const index = evnet.currentTarget.dataset.index;
        const curHidden = `mockList[${index}].hidden`
        this.setData({
            [curHidden]: !this.data.mockList[index].hidden
        })
    },
    onToggleChecked (evnet) {
        const index = evnet.target.dataset.index;
        const curChecked = `mockList[${index}].checked`
        this.setData({
            [curChecked]: !this.data.mockList[index].checked
        }, () => {
            console.log(this.data.mockList[index].checked)
        })
    },
    addRequestHooks () {
        Object.defineProperty(wx,  "request" , { writable:  true });
        console.log('addRequestHooks success')
        const matchUrlRequest = this.matchUrlRequest
        wx.request = function (options) {
            console.log('request options', options)
            // options.url
            if (matchUrlRequest(options)) {
                // Todo 改写url
                app.globalData.originRequest(options)
            } else {
                app.globalData.originRequest(options)
            }
        }
    },
    matchUrlRequest (options) {
        let flag = false, curItem;
        if (!this.data.mockList.length) { return false }
        for (let i = 0,len = this.mockList.length; i < len; i++) {
            curItem = this.mockList[i]
            // Todo
            if (options.url = curItem.url) {
                flag = true
                break;
            }
        }
        return flag && curItem.checked;
    }
});
