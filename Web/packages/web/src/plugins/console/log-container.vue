<template>
  <div class="log-container">
    <log-item
      v-for="(log, index) in logList"
      :key="index"
      :value="log.value"
      :type="log.type"
      :logType="log.name"
    ></log-item>
  </div>
</template>
<style lang="less" scoped>
@import "./css/var.less";
.tab-container {
  .tab-list {
    display: flex;
    height: @tap-height;
    justify-content: space-between;
    align-items: center;
    border: 1px solid @border-color;
  }
  .tab-item {
    flex: 1;
    height: @tap-height;
    line-height: @tap-height;
    text-align: center;
  }
  .tab-item-text {
    font-size: @tap-font-size;
    color: @tap-font-color;
  }
  .tab-active {
    border-bottom: 1px solid #1485ee;
  }
  .tab-default {
    border: none;
  }
}
</style>
<script>
import LogItem from './log-item'
export default {
  components: {
    LogItem
  },
  props: {
    logList: {
      type: Array,
      default: [],
    },
  },
  data() {
    return {};
  }
};
</script>