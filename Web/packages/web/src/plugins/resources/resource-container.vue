<template>
  <div class="resource-container">
    <resource-item
      v-for="(item, index) in resourceList"
      :key="item.entryName"
      :index="index"
      :type="item.type"
      :initiatorType="item.initiatorType"
      :entryName="item.entryName"
      :base64="item.base64"
    ></resource-item>
  </div>
</template>
<style lang="less" scoped>
@import "./css/var.less";
.resource-container{
  padding: 0 5px;
}
</style>
<script>
import ResourceItem from "./resource-item"
export default{
  components:{
    ResourceItem
  },
  props: {
    resourceList: {
      type: Array,
      default: [],
    },
  },
  data() {
    return {};
  },
  methods:{
    

  }
}
</script>