const img = require('../../utils/imgbase64')
const app = getApp()
if (!Object.prototype.hasOwnProperty.call(app, 'originRequest')) {
    app.originRequest = wx.request
}
Component({
    data: {
        interceptors:[],
        isInjected: false,
        isShowManage:false,
        addPopupClass:'',
        isShowMask:false,
        addInfo:{
            key:'',
            value:'',
            title:'添加',
            isRegex: false,
            disabled:false
        },
        img
    },
    lifetimes: {
      created () {

      },
      attached () {
        this.setData({
            addInfo:{
                key:'',
                value:'',
                title:'添加',
                isRegex: false,
                disabled:false
            }
        })
        this.setData(getApp().globalData['__HTTP_INJECTOR'])
      },
      detached () {
        const { interceptors, isInjected } = this.data
        getApp().globalData['__HTTP_INJECTOR'] = { interceptors,isInjected }
      }
    },
    methods: {
        hooksRequestSuccessCallback(res) {
            let { data } = res
            this.data.interceptors.forEach(interceptor => {
                const { isRegex, key, value } = interceptor
                const replaceKey = isRegex ? new RegExp(key, 'g') : key
                const isNotStringData = typeof data !== 'string'
                if (isNotStringData) {
                    data = JSON.stringify(data)
                }
                data = data.replace(replaceKey,value)
                if (isNotStringData) {
                    data = JSON.parse(data)
                }
            })
            res.data = data
            return res
        },
        hooksRequest() {
            Object.defineProperty(wx,  "request" , { writable:  true });
            const hooksRequestSuccessCallback = this.hooksRequestSuccessCallback
            wx.request = function(options){
                const originSuccessCallback = options.success
                options.success = res => {
                    originSuccessCallback(hooksRequestSuccessCallback(res))
                }
                app.originRequest(options)
            }
        },
        showAddPopup(){
            this.setData({
                isShowManage:false,
                addPopupClass:'add-dialog-active',
                addInfo:{
                    key: '',
                    value:'',
                    title:'添加',
                    disabled:false
                }
            })
        },
        closeAddPopup(){
            this.closeAll()
        },
        closeAll(){
            this.setData({
                isShowManage:false,
                isShowMask:false,
                addPopupClass:''
            })
        },
        openManageMenu(){
            if (this.data.isInjected)return;
            this.setData({
                isShowManage:true,
                isShowMask:true
            })
        },
        clearAll (){
            this.setData({ isShowManage:false })
            wx.showModal({
                title: '提示',
                content: '确定要清除所有吗？',
                success:res => {
                    if (res.confirm) {
                        // Todo 清楚所欲自定的key
                        wx.clearStorageSync()
                    }
                    this.closeAll()
                }
            })
        },
        checkboxChange(event){
            this.setData({checkedStorage:event.detail.value})
        },
        modifyItemValue(event) {
            this.setData({
                isShowMask:true,
                addPopupClass:'add-dialog-active',
                addInfo:{
                    key:event.currentTarget.dataset.key,
                    value:event.currentTarget.dataset.value,
                    title:'修改',
                    disabled:true
                }
            })
        },
        bingAddInfoKey(event){
            this.setData({'addInfo.key': event.detail.value})
        },
        bingAddInfoValue(event){
            this.setData({'addInfo.value': event.detail.value})
        },
        bingAddInfoIsRegex(event){
            this.setData({'addInfo.isRegex': event.detail.value})
        },
        addStorage(){
            const { key,value, isRegex } = this.data.addInfo
            if(key && value){
                this.setData({
                    interceptors: [...this.data.interceptors,{ key, value, isRegex }]
                })
            }
            this.closeAll()
        },
        toggleInjectionState() {
            this.setData({
                isInjected: !this.data.isInjected
            }, () => {
                this.data.isInjected && this.hooksRequest()
            })
        },
        onGoBack () {
            this.triggerEvent('toggle', { componentType: 'dokit'})
        }
    }
  });
  