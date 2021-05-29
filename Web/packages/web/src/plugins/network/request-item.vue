/*
 * @Author: yanghui 
 * @Date: 2021-05-28 21:31:53 
 * @Last Modified by: yanghui
 * @Last Modified time: 2021-05-29 21:04:41
 */
<template>
  <div class="request-item" @click="showDetail(request.id)">
    <span class="request-url">{{request.url}}</span>
    <span class="request-status">{{request.status}}</span>
    <!-- <span class="request-method">{{request.method}}</span>
    <span class="request-type">{{request.type}}</span>
    <span class="request-size">{{request.size}}</span> -->
    <span class="request-time">{{request.time}}ms</span>
  </div>
</template>
<script>
import { getDataStructureStr } from '../../assets/util'

export default {
  components: {
    
  },
  props: {
    requestItemData: {
      type: Object,
      default: function () {
        return {}
      }
    }
  },
  data () {
    return {
      request: this.requestItemData
    }
  },
  computed: {
    
  },
  methods: {
    showDetail(id){
      this.$parent.$parent.showDetail(id)
    }
  }
}
</script>
<style lang="less">
  .request-item{
    text-align: left;
    font-size: 13px;
    display: flex;
    width: 100%;
    cursor: pointer;
    border-bottom: 1px solid #ccc;
    height: 41px;
    color: #333;
    white-space: nowrap;
  }
  .request-item:first-child {
    border-top: none;
  }
   .request-url{
    flex: 1;
  }
  .request-status{
    width: 40px;
  }
  .request-method{
    width: 50px;
  }
  .request-type{
    width: 50px;
  }
  .request-size{
    width: 70px;
  }
  .request-time{
    width: 60px;
    padding-right: 10px;
  }
  .request-item span {
    display: block;
    line-height: 40px;
    height: 40px;
    padding: 0 5px;
    font-size: 12px;
    text-align: center;
    text-overflow: ellipsis;
    overflow: hidden;
  }
 

</style>