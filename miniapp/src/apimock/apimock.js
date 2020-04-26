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
        this.initMockStatus()
        this.initMockList()
    },
    request (options) {
        return new Promise((resolve, reject) => {
            app.globalData.originRequest({
                ...options,
                success: res => resolve(res),
                fail: err => reject(err)
            })
        })
    },
    initMockStatus () {
        const curStatus = wx.getStorageSync('dokit-mockStatus')
        if (typeof(curStatus) == 'boolean') {
            this.setData({
                isMock: curStatus
            })
        }
    },
    // 初始化mock列表
    initMockList () {
        const that = this
        const opt = {
            url: `${baseUrl}/api/app/interface`,
            method: 'GET',
            data: { projectId: '749a0600b5e48dd77cf8ee680be7b1b7',}
        }
        that.request(opt).then(res => {
            const { data } = res.data
            if (data && data.datalist && data.datalist.length) {
                that.updateMockList(data.datalist)
            }
        }).catch(err => console.log(err))
    },
    updateMockList (datalist) {
        const that = this
        let list = []
        const localList = wx.getStorageSync('dokit-mocklist')
        if (localList && localList.length) {
            // 更新本地缓存
            for (let i = 0,iLen = datalist.length; i < iLen; i++) {
                const dataItem = datalist[i]
                let isHas = false
                for (let j = 0, jLen = localList.length ; j < jLen; j++) {
                    const localItem = localList[j];
                    if (dataItem._id == localItem._id) {
                        dataItem.hidden = localItem.hidden
                        dataItem.checked = localItem.checked
                        isHas = true
                        break;
                    }
                }
                if (!isHas) {
                    dataItem.hidden = true
                    dataItem.checked = false    
                }
                list.push(dataItem)
            }
        } else {
            // 没有的话，添加数据并且缓存
            list = datalist.map(item => ({
                ...item,
                hidden: true,
                checked: false
            }))
        }
        that.setData({
            mockList: list
        }, () => {
            console.log('dokit-mocklist', that.data.mockList)
        })
    },
    // 卸载hooks
    toggleMockState() {
        this.setData({
            isMock: !this.data.isMock
        }, () => {
            this.data.isMock ? this.addRequestHooks() : this.removeRequestHooks()
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
    removeRequestHooks () {
        wx.request = getApp().globalData.originRequest
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
        console.log(Object.getOwnPropertyDescriptor(wx, 'request'))
    },
    matchUrlRequest (options) {
        let flag = false, curItem;
        console.log('matchUrlRequest success')
        // return true
        if (!this.data.mockList.length) { return false }
        for (let i = 0,len = this.mockList.length; i < len; i++) {
            curItem = this.mockList[i]
            if (this.urlIsMatch(options, curItem)) {
                flag = true
                break;
            }
        }
        return flag && curItem.checked;
    },
    // 匹配的方法
    urlIsMatch (requestOpt, mockItem) {
        return true
    },
    onHide: function () {
        console.log('影藏了~')
    },
    onUnload: function () {
        wx.setStorageSync('dokit-mocklist', this.data.mockList)
        wx.setStorageSync('dokit-mockStatus', this.data.isMock)
    }
});
