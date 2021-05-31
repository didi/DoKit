/*
 * @Author: yanghui 
 * @Date: 2021-05-28 21:31:36 
 * @Last Modified by: yanghui
 * @Last Modified time: 2021-05-29 17:14:26
 */
<template>
  <div class="request-container">
    <request-item
      v-for="(request, index) in reqList"
      :key="index"
      :requestItemData="request"
    ></request-item>
  </div>
</template>
<style lang="less" scoped>
@import "./css/var.less";

.request-container{
    overflow-y: auto;
    -webkit-overflow-scrolling: touch;
    height: 100%;
    margin-bottom: 10px;
}
</style>
<script>
import RequestItem from './request-item'
export default {
  components: {
    RequestItem
  },
  props: {
    reqList: {
      type: Array,
      default: [],
    },
  },
  data() {
    return {};
  }
};
</script>