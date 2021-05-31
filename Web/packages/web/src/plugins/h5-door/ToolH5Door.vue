
<template>
  <div class="h5-door">
    <div class="h5-title">
      <input class="url-ipt" type="search" v-model="url" 
      placeholder="输入网址，点击右侧跳转" @keyup.enter="jumpHtml(url)">
      <button class="jump-btn" @click="jumpHtml(url)"> Search</button>
    </div>

    <div class="hislist-title">
      <p>历史记录</p>
    </div>
    
    <div class="his-part">
      <ul class="his-table">  
        <li class="his-item"
          v-for="(his,id) in view_his" :key="id" >
          <span class="text-in-li">{{ his.content }} </span>
          <div>
            <button class="his-item-btn" @click="jumpHtml(his.content)">载入</button> 
            <button class="his-item-btn" @click="delHis(id)">清除</button>
          </div>
        </li>
      </ul>
    </div>
    <div class="btm">
      <p class="clear-tip" v-if="clear">暂无历史记录</p>
      <button class="clear-btn" v-if="!clear" @click="clearHistory" >清空搜索历史</button>
    </div>
  </div>
</template>


<script>
let storage = window.localStorage

export default {
  data(){
    return{
      url:null,
      view_his:this.fetchData(),
      clear:this.fetchData().length == 0
    }
  },
  watch: {
    view_his: {
    handler: function(val, oldVal) {
      this.storeData(val);
      if (this.view_his.length == 0){
        this.clear = true
      }
      else{
        this.clear = false
      }
    },
    deep: true
  }
 },
  methods:{
    jumpHtml(url){
      url = url.replace('https','http')
      if (!url.startsWith('http://')) {
        url = 'http://' + url
      }

      let flag = true
      let len = this.view_his.length
      for (let i=0; i < len; i++){
        let his = this.view_his[i]
        if (his.content == url) {
          flag = false
          break
        }
      }
      if (flag){
        this.addHis(url)
      }
      window.open(url)
    },
    addHis(url){
      this.view_his.push({content:url})
    },
    delHis(idx){
      this.view_his.splice(idx, 1)

    },
    clearHistory(){
      this.view_his = []
    },
    storeData(val){
      storage.setItem('viewHis', window.JSON.stringify(val))
      console.log(storage['viewHis'])
    },
    fetchData(){
      return window.JSON.parse(storage.getItem('viewHis') || '[]')
    }
  },
}
</script>

<style lang="less" scoped>
.h5-title{
  text-align: center;
  display: flex;
  justify-content: center;
  height: inherit;
}
.url-ipt{
  width: 300px;
  min-width: 100px;
  height: 30px;
  background-color:white;
  border: solid 2px skyblue;
  border-top-left-radius: 6px;
  border-bottom-left-radius: 6px;
  vertical-align: top;
  outline: none;

}
.jump-btn{
  background-color:skyblue;
  height: 30px;
  border: 2px solid skyblue;
  border-top-right-radius: 6px;
  border-bottom-right-radius: 6px;
  color: white;
  vertical-align: top;
  
}
.clear-tip{
  font-size: 10px;
  color: lightgray;
}
.hislist-title{
  text-align: center;
  margin-top: 30px;
  font-size: 10px;
  font-weight: bold;
}
.his-part{
  text-align: center;
  display: flex;
  justify-content: center;
}
.his-table{
  margin-bottom: 10px;
  padding: 10px;
}
.his-item{
  display: flex;
  justify-content: space-between;
  align-items: center;
  list-style: none;
  text-align: left;
  background-color: white;
  border: 1px solid lightgray;
  border-radius: 5px;
  margin-top: 5px;
  padding: 3px;
  width: 350px;  
  height: 20px;
  font-size: 15px;  
  padding: 5px;
}
.text-in-li{
  flex: 0 1 68%;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
}
.btn-in-li{
  text-align: center;
}
.his-item-btn{
  min-width: 30px;
  height: inherit;
  float: right;
  margin-left: 10px;
  background-color:skyblue;
  border: 2px solid skyblue;
  border-radius: 5px;
  color: white;
}
.btm{
  text-align: center;
}
.clear-btn{
  background-color:skyblue;
  height: 30px;
  border: 2px solid skyblue;
  color: white;
  vertical-align: top;
}
</style>