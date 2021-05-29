/*
 * @Author: yanghui 
 * @Date: 2021-05-28 21:31:27 
 * @Last Modified by: yanghui
 * @Last Modified time: 2021-05-29 20:42:20
 */
<template>
  <div class="network-container">
    <div class="network-title">
      请求捕获
      <!-- <div {{{class 'btn clear-request'}}}>
        <span {{{class 'icon-clear'}}}></span>
      </div> -->
      <!-- <img class="item-icon-image" :src="defaultIcon"/> -->
    </div>

    <div class="network-info"> 
      <div v-if="!detailShowed">
        <request-container :reqList="reqList"></request-container>
      </div>
      <div v-if="detailShowed">
        <request-detail :reqDetail="curReq"></request-detail>
      </div>
    </div>
  </div>
</template>
<style lang="less" scoped>
@import "./css/var.less";

.network-container{
  display: flex;
  flex-direction: column;
  height: 100%;
}
.network-title{
  font-size: 14px;
  position: relative;
  width: 100%;
  left: 0;
  top: 0;    
  background: #f3f3f3;
  padding: 10px;
  color: #333;
  box-sizing: border-box;
  pointer-events: all;
  -webkit-user-select: none;    
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  display: block;
  -webkit-tap-highlight-color: transparent;
  -webkit-text-size-adjust: none;  
  
  border-bottom: 1px solid #ccc;
}
.network-info{
  display: flex;
  flex-direction: column;
  overflow: hidden;
  flex: 1;
  background-color: #ffffff;
  overflow-y: scroll; 
}
</style>
<script>
import RequestDetail from './request-detail.vue';
import RequestContainer from './request-container'
export default {
  components: {
    RequestContainer,
    RequestDetail
  },
  props: {
    
  },
  computed: {
   
  },
  data() {
    return {
      detailShowed: false,
      reqList: this.$store.state.reqList || [],
      curReq: {}
      // reqMap: this.$store.state.reqMap,
      // curReq: this.$store.state.reqMap.get(this.$store.state.reqList[0].id)
    };
  },
  methods: {
    showDetail(id){
      this.curReq = this.reqList[id]
      this.detailShowed = true
      console.log("现在的reqid: "+id)
    },
    hideDetail(){
      this.detailShowed = false
    }
  }
};
</script>