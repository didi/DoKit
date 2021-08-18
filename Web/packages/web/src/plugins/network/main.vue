<template>
  <div class="network-plugin">
    <div class="network-header">
      <do-row>
        <do-col :span="12">url</do-col>
        <do-col :span="3">type</do-col>
        <do-col :span="4">method</do-col>
        <do-col :span="3">status</do-col>
      </do-row>
    </div>
    <requestItem
      v-for="(requestItem, index) in requestList"
      :key="requestItem.id"
      :index="index"
      :requestItem="requestItem"
    ></requestItem>
  </div>
</template>
<script>
import requestItem from "./request-item";

export default {
  components: {
    requestItem
  },
  data() {
    return {
      requestList: this.$store.state.requestList || []
    }
  }
};
</script>
<style lang="less">
.network-plugin {
  padding: 5px;
  .network-header{
    text-align: center;
    font-weight: bold;
    font-size: 14px;
  }
}
</style>