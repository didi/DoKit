<template>
  <div class="tab-container">
    <div class="tab-list">
      <div
        class="tab-item"
        :class="curIndex === index? 'tab-active': 'tab-default'"
        v-for="(item, index) in tabs"
        :key="index"
        @click="handleClickTab(item, index)"
      >
        <span class="tab-item-text">{{ item.name }}</span>
      </div>
      <div class="tab-item" @click="handleRefresh">
        <img style="width:16px;margin-top:12px;" src="data:image/png;base64,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" alt="" srcset="">
      </div>
    </div>
  </div>
</template>
<style lang="less" scoped>
@import "./css/var.less";
.tab-container{
  .tab-list{
    display: flex;
    height: @tap-height;
    justify-content: space-between;
    align-items: center;
    border: 1px solid @border-color;
  }
  .tab-item{
    flex: 1;
    height: @tap-height;
    line-height: @tap-height;
    text-align: center;
  }
  .tab-item-text{
    font-size: @tap-font-size;
    color: @tap-font-color;
  }
  .tab-active{
    border-bottom: 1px solid @border-active-color;
  }
  .tab-default{
    border: none;
  }
}
</style>
<script>
export default {
  props: {
    tabs: {
      type: Array
    },
  },
  data() {
    return {
      curIndex: 0
    };
  },
  methods: {
    handleClickTab(item, index) {
      let { type } = item;
      this.curIndex = index;
      this.$emit("changeTap", type);
    },
    handleRefresh() {
      this.$emit("refreshResource");
    }
  },
};
</script>