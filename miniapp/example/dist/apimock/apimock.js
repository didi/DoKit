const util = require('../utils/util.js');
const app = getApp()
// console.log(Object.getOwnPropertyDescriptor(wx, 'request'))
app.globalData.originRequest = wx.request
const mockBaseUrl = 'https://mock.dokit.cn'
const projectId = '749a0600b5e48dd77cf8ee680be7b1b7'

Page({
    data: {
        mockList: [],
        tplList: [],
        curScene: '',
        curNav: 'mock',
        templateData: '',
        urlId: '',
        isShow: false
    },
    onLoad: function () {
        // 初始化
        console.log(Object.getOwnPropertyDescriptor(wx, 'request'))
        console.log(Object.getOwnPropertyDescriptor(getApp().globalData, 'originRequest'))
        this.pageInit()
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
    pageInit () {
        this.initList()
        this.addRequestHooks()
    },
    // 初始化mock列表
    initList () {
        const that = this
        const opt = {
            url: `${mockBaseUrl}/api/app/interface`,
            method: 'GET',
            data: { projectId: projectId, isfull: 1 }
        }
        that.request(opt).then(res => {
            const { data } = res.data
            if (data && data.datalist && data.datalist.length) {
                that.updateMockList(data.datalist)
                that.updateTplList(data.datalist)
            }
        }).catch(err => console.log(err))
    },
    updateMockList (datalist) {
        let list = [], that = this
        const localMockList = wx.getStorageSync('dokit-mocklist')
        if (localMockList && localMockList.length) {
            list = that.mergeMockList(datalist, localMockList)
        } else {
            list = datalist.map(item => that.processMockItem(item))
        }
        that.setData({
            mockList: list
        }, () => {
            console.log('dokit-mocklist', that.data.mockList)
        })
    },
    updateTplList (datalist) {
        let list = [], that = this
        const localTplList = wx.getStorageSync('dokit-tpllist')
        if (localTplList && localTplList.length) {
            list = that.mergeTplList(datalist, localTplList)
        } else {
            list = datalist.map(item => that.processTplItem(item))
        }
        that.setData({
            tplList: list
        }, () => {
            console.log('dokit-tpllist', that.data.tplList)
        })
    },
    mergeMockList (datalist, localMockList) {
        let list = [], that = this
        for (let i = 0,iLen = datalist.length; i < iLen; i++) {
            let dataItem = datalist[i]
            let isHas = false
            for (let j = 0, jLen = localMockList.length ; j < jLen; j++) {
                const mockItem = localMockList[j];
                if (dataItem._id == mockItem._id) {
                    dataItem.hidden = mockItem.hidden
                    dataItem.checked = mockItem.checked
                    // Todo：将 sceneList 的记录
                    if (util.isArray(dataItem.sceneList) && dataItem.sceneList.length) {
                        for (let k = 0,kLen = dataItem.sceneList.length; k < kLen; k++) {
                            const nowSceneItem = dataItem.sceneList[k]
                            for (let h = 0, hLen = mockItem.sceneList.length; h < hLen; h++) {
                               const preSceneItem = mockItem.sceneList[h]
                               if (nowSceneItem._id == preSceneItem._id) {
                                    nowSceneItem.checked = preSceneItem.checked
                                    break;
                               }
                            }
                        }
                    } else {
                        dataItem.sceneList = []
                    }
                    isHas = true
                    break;
                }
            }
            if (!isHas) {
                dataItem = that.processMockItem(dataItem)
            }
            list.push(dataItem)
        }
        return list
    },
    mergeTplList (datalist, localTplList) {
        let list = [],that = this
        for (let i = 0,iLen=datalist.length; i < iLen; i++) {
            let dataItem = datalist[i]
            let isHas = false
            for (let j = 0, jLen = localTplList.length ; j < jLen; j++) {
                const tplItem = localTplList[j];
                if (dataItem._id == tplItem._id) {
                    dataItem.hidden = tplItem.hidden
                    dataItem.checked = tplItem.checked
                    dataItem.templateData = tplItem.templateData ? tplItem.templateData : ''
                    isHas = true
                    break;
                }
            }
            if (!isHas) {
                dataItem = that.processTplItem(dataItem)
            }
            list.push(dataItem)
        }
        return list
    },
    processMockItem (item) {
        return {
            ...item,
            hidden: true,
            checked: false,
            query: item.query ? JSON.stringify(item.query) : '{}',
            body: item.body ? JSON.stringify(item.body) : '{}',
            sceneList: util.isArray(item.sceneList) ? item.sceneList.map((sceneItem, sceneIndex) => ({
                ...sceneItem,
                checked: sceneIndex === 0 ? true:false
            })) : []
        }
    },
    processTplItem (item) {
        return {
            ...item,
            hidden: true,
            checked: false,
            query: item.query ? JSON.stringify(item.query) : '{}',
            body: item.body ? JSON.stringify(item.body) : '{}'
        }
    },
    addRequestHooks () {
        // Todo 添加一个拦截标识
        if (getApp().globalData.requsetIsChanged) {
            return
        }
        Object.defineProperty(wx,  "request" , { writable:  true });
        getApp().globalData.requsetIsChanged = true
        console.log('addRequestHooks success')
        const matchUrlRequest = this.matchUrlRequest
        const matchUrlTpl = this.matchUrlTpl
        wx.request = function (options) {
            const originSuccessFn = options.success
            const sceneId = matchUrlRequest(options)
            if (sceneId) {
                options.url = `${mockBaseUrl}/api/app/scene/${sceneId}`
                console.log('request options', options)
                console.warn('被拦截了~')
            }
            options.success = function (res) {
                originSuccessFn(res)
                matchUrlTpl(options, res)
            }
            app.globalData.originRequest(options)
        }
    },
    onTapbar (event) {
        const type = event.target.dataset.type
    },
    onNavChange (event) {
        const type = event.target.dataset.type
        this.setData({
            curNav: type
        })
    },
    onExpand (evnet) {
        const { index,type } = evnet.currentTarget.dataset
        const curHidden = `${type}List[${index}].hidden`
        this.setData({
            [curHidden]: !this.data[`${type}List`][index].hidden
        }, () => {
            console.log(this.data[`${type}List`][index].hidden)
        })
    },
    onToggleChecked (evnet) {
        const { index,type } = evnet.currentTarget.dataset
        const curChecked = `${type}List[${index}].checked`
        this.setData({
            [curChecked]: !this.data[`${type}List`][index].checked
        }, () => {
            console.log(this.data[`${type}List`][index].checked)
        })
    },
    onRadioChange (event) {
        const { index, idx } = event.currentTarget.dataset
        this.data.mockList[index].sceneList.map((sceneItem, sceneIndex) => {
            if (sceneIndex == idx) {
                this.data.mockList[index].sceneList[sceneIndex].checked = true
            } else {
                this.data.mockList[index].sceneList[sceneIndex].checked = false
            }
        })
    },
    onRadioGroupChange (e) {
        const val = e.detail.value
        console.log('val', val)
    },
    matchUrlTpl (options, res) {
        let curTplItem,that = this
        if (!that.data.tplList.length) { return false }
        for (let i=0,len=that.data.tplList.length;i<len;i++) {
            curTplItem = that.data.tplList[i]
            if (that.requestIsmatch(options, curTplItem) && curTplItem.checked && res.statusCode == 200) {
                that.data.tplList[i].templateData = res.data
            }
        }
        wx.setStorageSync('dokit-tpllist', that.data.tplList)
    },
    uploadTplData () {

    },
    matchUrlRequest (options) {
        let flag = false, curMockItem, sceneId;
        if (!this.data.mockList.length) { return false }
        for (let i = 0,len = this.data.mockList.length; i < len; i++) {
            curMockItem = this.data.mockList[i]
            if (this.requestIsmatch(options, curMockItem)) {
                flag = true
                break;
            }
        }
        if (curMockItem.sceneList && curMockItem.sceneList.length) {
            for (let j=0,jLen=curMockItem.sceneList.length; j<jLen; j++) {
                const curSceneItem = curMockItem.sceneList[j]
                if (curSceneItem.checked) {
                    sceneId = curSceneItem._id
                    break;
                }
            }
        } else {
            sceneId = false
        }
        return flag && curMockItem.checked && sceneId
    },
    // judge url is match
    requestIsmatch (options, mockItem) {
        const path = util.getPartUrlByParam(options.url, 'path')
        const query = util.getPartUrlByParam(options.url, 'query')
        return this.urlMethodIsEqual(path, options.method, mockItem.path, mockItem.method) && this.requestParamsIsEqual(query, options.data, mockItem.query, mockItem.body)
    },
    // path && menthod is equal
    urlMethodIsEqual (reqPath, reqMethod, mockPath, mockMethod) {
        reqPath = reqPath ? `/${reqPath}` : ''
        reqMethod = reqMethod || 'GET'
        return (reqPath == mockPath) && (reqMethod.toUpperCase() == mockMethod.toUpperCase())
    },
    // 判断请求参数是否相同 
    requestParamsIsEqual (reqQuery, reqBody, mockQuery, mockBody) {
        reqQuery = util.search2Json(reqQuery)
        reqBody = reqBody || {}
        try {
            return (JSON.stringify(reqQuery) == mockQuery) && (JSON.stringify(reqBody) == mockBody)
        } catch (e) {
            return false
        }
    },
    onPreview (event) {
        const that = this
        const index = event.currentTarget.dataset.index
        const { templateData, _id } = that.data.tplList[index]
        if (templateData) {
            let tplData
            try {
                tplData = JSON.stringify(templateData, null, 4)
            } catch (error) {}
            that.setData({
                urlId:  _id,
                templateData: tplData,
                isShow: !that.data.isShow
            })
        } else {
            // Todo: 提示
        }
    },
    onCancel () {
        this.setData({
            isShow: !this.data.isShow
        })
    },
    onUpload (event) {
        const that = this
        const index = event.currentTarget.dataset.index
        let data = {}
        if (index != undefined) {
            const { _id, templateData } = that.data.tplList[index]
            data = { 
                id: _id, 
                tempData: templateData,
                projectId: projectId
            }
        } else {
            data = { 
                id: that.data.urlId, 
                tempData: that.data.templateData,
                projectId: projectId
            }
        }
        const opt = {
            url: `${mockBaseUrl}/api/app/interface`,
            method: 'POST',
            data
        }
        that.request(opt).then(res => {
            const { data } = res.data
            console.log(data)
        }).catch(err => console.log(err))
    },
    onHide: function () {
        console.log('影藏了~')
    },
    onUnload: function () {
        wx.setStorageSync('dokit-mocklist', this.data.mockList)
    }
});
