// Contributor： PKU - Li Bin
const img = require('../../utils/imgbase64')
const util = require('../../utils/util')
const app = getApp();
const types = ['debug','log','info','warn','error'];
if(!Object.prototype.hasOwnProperty.call(app,'originlog')){
  for(let type of types){
    app[`origin${type}`] = console[type];
  }
}
Component({
  data: {
    logs:{"debug":[],"log":[],"info":[],"warn":[],"error":[],"search":[]},
    lookLog : false,
    isShowMask:false,
    isShowManage:false,
    logKinds : [],
    currentKind : 'log',
    img
  },
  lifetimes:{
    attached:function(){
      //加载种类栏和全局存储的日志表
      const globalLogs = getApp().globalData['__DOKIT_LOOKLOG'];
      if(globalLogs !== undefined){
        this.data.logs = globalLogs.logs;
        this.data.lookLog = globalLogs.lookLog;
        this.data.currentKind = globalLogs.currentKind;
      }
      this.data.logKinds = this.getKinds();
      this.setData(this.data);
    },
    detached(){
      const { logs , lookLog , currentKind} = this.data;
      getApp().globalData['__DOKIT_LOOKLOG'] = { logs , lookLog ,currentKind};
    }
  },

  /**
   * 组件的方法列表
   */
  methods: {
    hookConsole(){
      for(let type of types){
        this.hookKindLog(type);
      }
      console.log("日志查看已开启");
    },
    resetConsole(){
      for(let type of types){
        console[types] = app[`origin${type}`];
      }
    },
    chooseList(event){
      const type = event.currentTarget.dataset.type;
      this.setData({currentKind:type});
    },
    searchLog(event){
      var text = event.detail.value;
      if(text !== ""){
        var searchList = [];
        for(let type of types){
          searchList = searchList.concat(this.traversal(type,text));
        }
        this.setData({
          'logs.search' : searchList,
          currentKind : 'search'
        })
      }
    },
    traversal(kind,text){
      var tempList = [];
      var logs = this.data.logs[kind];
      for(var i = 0; i < logs.length; i++){
        if((logs[i].para).search(text) !== -1){
          tempList.push(logs[i]);
        }
      }
      return tempList;
    },
    closeAll(){
      this.setData({
        isShowMask:false,
        isShowManage:false
      })
    },
    openManageMenu(){
      if(this.data.lookLog) return;
      this.setData({
        isShowMask:true,
        isShowManage:true
      })
    },
    clearAll (){
      this.setData({ isShowManage:false })
      wx.showModal({
          title: '提示',
          content: '确定要清除所有吗？',
          success:res => {
              if (res.confirm) {
                  // Todo 清除之前记录的日志信息
                  getApp().globalData['__DOKIT_LOOKLOG'].logs = {
                    "debug":[],"log":[],"info":[],"warn":[],"error":[],"search":[]};
                  getApp().globalData['__DOKIT_LOOKLOG'].lookLog = false;
                  this.setData(getApp().globalData['__DOKIT_LOOKLOG']);
              }
              this.closeAll()
          }
      })
    },    
    toggleLookLog(){
      this.setData({
        lookLog : !this.data.lookLog
      },()=>{
        if(this.data.lookLog)
          this.hookConsole();
        else this.resetConsole();
      })
    },
    onExpand(event){
      const index = event.currentTarget.dataset.index;
      const curState = this.data.logs[this.data.currentKind][index].hidden;
      const path = `logs.${this.data.currentKind}`;
      this.setData({
        [`${path}[${index}]hidden`]: !curState
      })
    },
    hookKindLog(kind){
      const that = this;
      Object.defineProperty(console,kind,{writable:true});
      console[kind] = (...vars)=>{
        app[`origin${kind}`](...vars);
        var param = "";
        for(var i = 0; i < vars.length; i++){
          if(typeof vars[i] === 'object') param += util.obj2str(vars[i]);
          else if(typeof vars[i] === 'symbol') param += String(vars[i])
          else param += vars[i];
        }
        const logTime = util.formatTime(new Date()) ;
        that.data.logs[kind].push({ time:logTime , para:param ,hidden:true});
        that.setData({
          logs:that.data.logs
        });
      }
    },
    onGoBack () {
      this.triggerEvent('toggle', { componentType: 'dokit'})
    },
    getKinds(){
      return [
        {
          "kind" : "Debug",
          "type" : "debug"
        },
        {
          "kind" : "Log",
          "type" : "log"
        },
        {
          "kind" : "Info",
          "type" : "info"
        },
        {
          "kind" : "Warn",
          "type" : "warn"
        },
        {
          "kind" : "Error",
          "type" : "error"
        }
      ]
    }
  }
})
