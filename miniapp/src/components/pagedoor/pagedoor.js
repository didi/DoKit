// author：zhou-xingxing
Component({
  /**
   * 组件的属性列表
   */
  properties: {
  },
  /**
   * 组件的初始数据
   */
  data: {
    parmList: [
      {
        key:'',
        value:''
      }
    ],
    typeList: [
      {value: 'navigateTo', name: '打开新页面',checked:true},
      {value: 'redirectTo', name: '页面重定向'},
      {value: 'reLaunch', name: '重启动'},
      {value: 'switchTab', name: 'Tab切换'},
    ],
    pageList:[],
    pageUrl:'',
    goType:'navigateTo',
  },
  lifetimes: {
    attached () {
        this.setData({
          pageList:__wxConfig.pages
        })
        // console.log(this.data.pageList)
    }
},
  /**
   * 组件的方法列表
   */
  methods: {
    onGoBack () {
      this.triggerEvent('toggle', { componentType: 'dokit'})
    },
    // page输入框
    textareaChange(e){
      this.setData({
        pageUrl:e.detail.value
      })
    },
    // page选择器
    bindPickerChange(e) {
      let url='/'+this.data.pageList[e.detail.value]
      this.setData({
        pageUrl:url
      })
    },
    // 修改跳转方式
    radioChange(e) {
      this.setData({
        goType:e.detail.value
      })
    },
    // 增加一组参数
    addParm(e){
      let list=this.data.parmList
      list.push({
        key:'',
        value:''
      })
      this.setData({
        parmList:list
      })
    },
    // 输入参数key
    changeParmkey(e){
      let idx=e.currentTarget.dataset.index
      let key = e.detail.value
      let list=this.data.parmList
      list[idx].key=key
      this.setData({
        parmList:list
      })
    },
    // 输入参数value
    changeParmValue(e){
      let idx=e.currentTarget.dataset.index
      let val = e.detail.value
      let list=this.data.parmList
      list[idx].value=val
      this.setData({
        parmList:list
      })
    },
    // 拼接路径和参数
    makeUrl(){
      let url=this.data.pageUrl
      let list=this.data.parmList
      if(!url){
        wx.showToast({
          title:'请输入页面路径',
          icon:"error"
        })
        return ""
      }
      url=this.data.pageUrl+'?'
      // 检验参数是否完整
      for(let i=0;i<list.length;i++){
        if(list[i].key!=""&&list[i].value!=""){
          url=url+list[i].key+'='+list[i].value+'&'
        }
        else if(list[i].key==""&&list[i].value==""){
          continue
        }
        else{
          wx.showToast({
            title:'参数不完整',
            icon:"error"
          })
          return ""
        }
      }
      return url
    },
    // 页面跳转
    goPage(){
      let url=this.makeUrl()
      let type=this.data.goType
      if(url==""){
        return
      }
      // switchTab不支持queryString
      if(type=='switchTab'){
          let pos=0
          for(;pos<url.length;pos++){
            if(url[pos]=='?'){
              break;
            }
          }
          url=url.substring(0,pos)
      }
      console.log("页面路径：",url)
      // 跳转实现
      wx[type]({
        url: url,
        fail:(e)=>{
          wx.showModal({
            title:'跳转失败',
            content: '错误信息：'+e.errMsg,
          })
        }
      })
    },
  }
})
