/*
 * @Author: yanghui 
 * @Date: 2021-05-28 21:31:43 
 * @Last Modified by: yanghui
 * @Last Modified time: 2021-05-29 20:44:05
 */
<template>
  <div class="request-detail">
    <div>
      <div class="request-url">{{request.url}}</div>
      <pre class="request-data">{{request.data}}</pre>
      <div class="request-headers-section">
        <h2>Request Headers</h2>
        <table class="request-headers" v-html="displayRequestHeader">
        </table>
        <h2>Response Headers</h2>
        <table class="request-headers" v-html="displayResponseHeader">
        </table>
      
      </div>
      <pre class="request-data">{{request.resTxt}}</pre>
    </div>

    <div class="detail-back" @click="hideDetail">返回列表</div>

  </div>
</template>
<script>
import { getDataType } from '../../assets/util'

export default {
  components: {

  },
  props: {
    reqDetail: {
      type: Object,
      default: function () {
        return {}
      }
    }
  },
  data () {
    return {
      request: this.reqDetail,
    }
  },
  computed: {
    displayRequestHeader () {
      let reqHeaders = this.reqDetail.reqHeaders
      let value = '<tbody>'
      if(!reqHeaders){
        value = '<tbody><tr><td>Empty</td></tr></tbody>'
        return value
      }
      for(var key in reqHeaders){
        value += '<tr>'
        value += '<td class="header-key">'+key+'</td>'
        value += '<td>'+reqHeaders[key]+'</td>'
        value += '</tr>'
      }
      value += '</tbody>'
      return value
    },
    displayResponseHeader () {
      let resHeaders = this.reqDetail.resHeaders
      let value = '<tbody>'
      for(var key in resHeaders){
        value += '<tr>'
        value += '<td class="header-key">'+key+'</td>'
        value += '<td>'+resHeaders[key]+'</td>'
        value += '</tr>'
      }
      value += '</tbody>'
      return value
    }
  },
  methods: {
    hideDetail(){
      this.$parent.hideDetail()
    }
  }
}
</script>
<style lang="less" scoped>
.request-detail{
  font-size: 12px;
  position: relative;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
  z-index: 10;
  padding-bottom: 40px;
  background: #fff;
}
.request-url{
  background: #f3f3f3;
  color: #333;
  -webkit-user-select: text;
  -moz-user-select: text;
  -ms-user-select: text;
  user-select: text;
  margin-bottom: 10px;
  word-break: break-all;
  padding: 10px;
  font-size: 16px;
  min-height: 40px;
  border-bottom: 1px solid #ccc;
}
.request-headers-section{
  border-top: 1px solid #ccc;
  border-bottom: 1px solid #ccc;
  margin-bottom: 10px;
}
.request-headers-section h2{
  margin: 2px 0;
  background: #f3f3f3;
  color: #333;
  padding: 10px;
  font-size: 14px;
}
pre.request-data{
  -webkit-user-select: text;
  -moz-user-select: text;
  -ms-user-select: text;    
  user-select: text;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  padding: 10px;
  font-size: 12px;
  margin-bottom: 10px;
  white-space: pre-wrap;
  border-top: 1px solid #ccc;
  color: #333;
  border-bottom: 1px solid #ccc;
} 
.detail-back{
  position: absolute;
  left: 0;
  bottom: 0;
  color: #333;
  width: 100%;
  border-top: 1px solid #ccc;
  background: #f3f3f3;
  display: block;
  height: 40px;
  line-height: 40px;
  text-decoration: none;
  text-align: center;
  margin-top: 10px;
  transition: background .3s;
  cursor: pointer;
}
table.request-headers {
    border-collapse: collapse;
    border-spacing: 0;
    color: #333;
}
table.request-headers>>> tbody, table.request-headers>>> tr{
  margin: 0;
    padding: 0;
    border: 0;
    font-size: 100%;
    font: inherit;
    vertical-align: baseline;
}
table.request-headers>>> td{
    font-size: 12px;
    padding: 5px 10px;
    word-break: break-all;
}
table.request-headers>>> td.header-key{
    white-space: nowrap;
    font-weight: 700;
    color: #1a73e8;
}

</style>














