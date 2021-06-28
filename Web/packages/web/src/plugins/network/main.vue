<template>
  <div class="network-plugin">
    <requestItem
      v-for="requestItem in requestList"
      :key="requestItem.id"
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
<style>
.network-plugin {
  padding: 5px;
}
</style>