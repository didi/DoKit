const img = require('../../utils/imgbase64')
Component({
    data: {
        qrCodeUrl:'',
        historyUrlList:[],
        isShowWebView:false,
        img
    },
    lifetimes: {
      created () {
      },
      attached () {
        this.getHistoryUrlList()
      },
      detached () {
        console.log('detached')
      }
    },
    methods: {
        getHistoryUrlList (){
            let urlArr = []
            let result = wx.getStorageSync('h5door-url')
            if(result){
                urlArr = urlArr.concat(result.split(","))
            }else{
                urlArr = []
            }
            this.setData({
                historyUrlList:urlArr
            })
        },
        setQrCode(event){
            this.setData({
                qrCodeUrl:event.target.dataset.qrCode
            })
        },
        qrCodeArouse(){
            let qrCodeObj = {
                scanType:['qrCode'],
                success:res=>{
                    this.setData({qrCodeUrl:res.result})
                    this.goWebview()
                }
            }
            wx.scanCode(qrCodeObj)
        },
        textareaChange(event){
            this.setData({
                qrCodeUrl:event.detail.value
            })
        },
        clearAll (){
            // Todo: 清除 dokit
            wx.clearStorageSync()
            this.getHistoryUrlList()
        },
        addUrlToStorage(){
            let urlArr = this.data.historyUrlList
            urlArr.push(this.data.qrCodeUrl)
            this.setData({
                historyUrlList:urlArr
            })
            if(this.data.historyUrlList.length>0){
                let newArr = new Set(this.data.historyUrlList)
                this.setData({
                    historyUrlList:[...newArr]
                })
                wx.setStorageSync('h5door-url', this.data.historyUrlList.join(","))
            }
        },
        goWebview(event){
            if(!this.data.qrCodeUrl){
                wx.showToast({title:'请输入跳转链接'})
                return
            }
            this.addUrlToStorage()
            this.setData({isShowWebView:true})
    
        },
        onGoBack () {
            this.triggerEvent('toggle', { componentType: 'dokit'})
        }
    }
  });
  