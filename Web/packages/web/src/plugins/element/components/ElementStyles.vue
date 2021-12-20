<template>
  <div class="dokit-styles dokit-section">
    <h2>Styles</h2>
    <div class="dokit-style-wrapper">
      <div class="dokit-style-rules" v-for="(item, idx) in styles" :key="idx">
        <div>{{ item.selectorText }} {</div>
        <div
          class="dokit-rule"
          v-for="(styleValue, styleKey, index) in item.style"
          :key="index"
        >
          <span>{{ styleKey }}</span
          >:
          <span v-html="`${styleValue};`"></span>
        </div>
        <div>}</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    styles: Array,
  },
};
</script>

<style lang="less" scoped>
.dokit-styles {
  font-size: 15px;
  .dokit-style-wrapper {
    padding: 10px;
    .dokit-style-rules {
      border: 1px solid #ccc;
      padding: 10px;
      margin-bottom: 10px;
      &:last-child {
        margin-bottom: 0;
      }
      .dokit-rule {
        padding-left: 2em;
        word-break: break-all;
       &:deep(.dokit-style-color) {
          position: relative;
          top: 1px;
          width: 10px;
          height: 10px;
          border-radius: 50%;
          margin-right: 2px;
          border: 1px solid #ccc;
          display: inline-block;
        }
        span {
          color: #c80000;
        }
      }
    }
  }
}
</style>