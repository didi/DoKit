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
  },
};
</script>