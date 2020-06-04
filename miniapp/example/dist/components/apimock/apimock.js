const util = require('../../utils/util.js');
const img = require('../../utils/imgbase64')
const app = getApp()
if (!Object.prototype.hasOwnProperty.call(app, 'originRequest') && Object.prototype.toString.apply(getApp().originRequest) !== "[object Function]") {
    app.originRequest = wx.request
}
const mockBaseUrl = 'https://mock.dokit.cn'
Component({
    properties: {
        projectId: {
          type: String,
          value: '',
        }
      },
    data: {
        mockList: [],
        tplList: [],
        curScene: '',
        curNav: 'mock',
        templateData: '',
        urlId: '',
        isShow: false,
        img
    },
    lifetimes: {
      created () {
        console.log('app', app)
      },
      attached () {
        this.pageInit()
      },
      detached () {
        wx.setStorageSync('dokit-mocklist', this.data.mockList)
        wx.setStorageSync('dokit-tpllist', this.data.tplList)
      }
    },
    methods: {
        onGoBack () {
            this.triggerEvent('toggle', { componentType: 'dokit'})
        },
        request (options) {
            return new Promise((resolve, reject) => {
                app.originRequest({
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
        getProjectId () {
            if (!this.data.projectId) {
                console.warn("您还没有设置 projectId，去快平台端体验吧：https://www.dokit.cn")
                return
            } else {
                return this.data.projectId
            }
        },
        // 初始化mock列表
        initList () {
            const that = this
            const opt = {
                url: `${mockBaseUrl}/api/app/interface`,
                method: 'GET',
                data: { projectId: this.getProjectId(), isfull: 1 }
            }
            that.request(opt).then(res => {
                const { data } = res.data
                if (data && data.datalist && data.datalist.length) {
                    that.updateMockList(data.datalist)
                    that.updateTplList(data.datalist)
                }
            }).catch()
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
                        dataItem.query = dataItem.query ? JSON.stringify(dataItem.query) : '{}'
                        dataItem.body = dataItem.body ? JSON.stringify(dataItem.body) : '{}'
                        if (util.isArray(dataItem.sceneList) && dataItem.sceneList.length) {
                            let isScene = false
                            for (let k = 0,kLen = dataItem.sceneList.length; k < kLen; k++) {
                                const nowSceneItem = dataItem.sceneList[k]
                                for (let h = 0, hLen = mockItem.sceneList.length; h < hLen; h++) {
                                   const preSceneItem = mockItem.sceneList[h]
                                   if (nowSceneItem._id == preSceneItem._id && preSceneItem.checked) {
                                        nowSceneItem.checked = preSceneItem.checked
                                        isScene = true
                                        break;
                                   }
                                }
                                if (isScene) {
                                    break;
                                }
                            }
                            if (!isScene && dataItem.sceneList[0]) {
                                dataItem.sceneList[0].checked = true
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
            Object.defineProperty(wx,  "request" , { writable:  true });
            console.group('addRequestHooks success')
            const matchUrlRequest = this.matchUrlRequest.bind(this)
            const matchUrlTpl = this.matchUrlTpl.bind(this)
            wx.request = function (options) {
                const opt = util.deepClone(options)
                const originSuccessFn = options.success
                const sceneId = matchUrlRequest(options)
                if (sceneId) {
                    options.url = `${mockBaseUrl}/api/app/scene/${sceneId}`
                    console.group('request options', options)
                    console.warn('被拦截了~')
                }
                options.success = function (res) {
                    originSuccessFn(matchUrlTpl(opt, res))
                }
                app.originRequest(options)
            }
        },
        onTabbar (event) {
            const type = event.currentTarget.dataset.type
        },
        onNavChange (event) {
            const type = event.currentTarget.dataset.type
            this.setData({ curNav: type })
        },
        onExpand (evnet) {
            const { index,type } = evnet.currentTarget.dataset
            const curHidden = `${type}List[${index}].hidden`
            this.setData({
                [curHidden]: !this.data[`${type}List`][index].hidden
            })
        },
        onToggleChecked (evnet) {
            const { index,type } = evnet.currentTarget.dataset
            const curChecked = `${type}List[${index}].checked`
            this.setData({
                [curChecked]: !this.data[`${type}List`][index].checked
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
        matchUrlTpl (options, res) {
            let curTplItem,that = this
            if (!that.data.tplList.length) { return res }
            for (let i=0,len=that.data.tplList.length;i<len;i++) {
                curTplItem = that.data.tplList[i]
                if (that.requestIsmatch(options, curTplItem) && curTplItem.checked && res.statusCode == 200) {
                    that.data.tplList[i].templateData = res.data
                }
            }
            wx.setStorageSync('dokit-tpllist', that.data.tplList)
            return res
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
                wx.showToast({
                    title: '没有模板数据哦~',
                    image: '../assets/img/error.png',
                    duration: 1000
                })
            }
        },
        onCancel () {
            this.setData({
                isShow: false
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
                    projectId: that.getProjectId()
                }
            } else {
                data = { 
                    id: that.data.urlId, 
                    tempData: that.data.templateData,
                    projectId: that.getProjectId()
                }
            }
            const opt = {
                url: `${mockBaseUrl}/api/app/interface`,
                method: 'POST',
                data
            }
            that.request(opt).then(res => {
                wx.showToast({
                    title: '上传成功!',
                    icon: 'success',
                    duration: 1000
                })
                that.data.isShow && that.onCancel()
            }).catch()
        }
    }
  });
  