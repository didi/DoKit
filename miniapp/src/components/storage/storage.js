const img = require('../../utils/imgbase64')
Component({
    data: {
        storage:[],
        limitSize:0,
        currentSize:0,
        isShowManage:false,
        addPopupClass:'',
        isShowMask:false,
        isDeleteMode:false, //是否是删除模式
        checkedStorage:[],
        addInfo:{
            key:'',
            value:'',
            title:'添加',
            disabled:false
        },
        img
    },
    lifetimes: {
      created () { },
      attached () {
        this.componentInit()
      },
      detached () {
        console.log('detached')
      }
    },
    methods: {
        componentInit () {
            this.setData({
                addInfo:{
                    key:'',
                    value:'',
                    title:'添加',
                    disabled:false
                }
            })
            this.getStorageInfo()
        },
        openDeleteMode(){
            this.setData({
                isDeleteMode:true
            })
            this.closeAll()
        },
        cancelDelete() {
            this.setData({
                isDeleteMode:false
            })
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
        getStorageInfo (){
            let storageArr = []
            let storageInfo = wx.getStorageInfoSync()
            this.setData({ limitSize: storageInfo.limitSize})
            this.setData({ currentSize: storageInfo.currentSize})
            storageInfo.keys.forEach(key => {
                let result = wx.getStorageSync(key)
                let info = {
                    key,
                    value:result,
                    isModify:false,
                    ischecked:false
                }
                storageArr.push(info)
            });
            storageArr = storageArr.filter(item => ["dokit-mocklist", "dokit-tpllist"].indexOf(item.key) == -1)
            this.setData({storage:storageArr})
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
        openManageMeau(){
            this.setData({
                isShowManage:true,
                isShowMask:true
            })
        },
        clearStorage(event){
            if(!this.data.checkedStorage.length){
                return
            }
            wx.showModal({
                title: '提示',
                content: '确定删除选中内容？',
                success:res => {
                    if (res.confirm) {
                        this.data.checkedStorage.forEach((item)=>{
                            wx.removeStorageSync(item)
                        })
                        this.componentInit()
                    }
                }
            })
        },
        clearAll(){
            this.setData({
                isShowManage:false
            })
            wx.showModal({
                title: '提示',
                content: '确定要清除所有吗？',
                success:res => {
                    if (res.confirm) {
                        let storageInfo = wx.getStorageInfoSync()
                        storageInfo.keys.forEach(key => {
                            if (["dokit-mocklist", "dokit-tpllist"].indexOf(key) == -1) {
                                wx.removeStorageSync(key)
                            }
                        });
                        this.componentInit()
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
            this.setData({'addInfo.key':event.detail.value})
        },
        bingAddInfoValue(event){
            this.setData({'addInfo.value':event.detail.value})
        },
        addStorage(){
            if(this.data.addInfo.key&&this.data.addInfo.value){
                wx.setStorageSync(this.data.addInfo.key, this.data.addInfo.value)
                this.componentInit()
            }
            this.closeAll()
        },
        onGoBack () {
            this.triggerEvent('toggle', { componentType: 'dokit'})
        }
    }
});
  