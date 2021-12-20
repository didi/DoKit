<template>
  <div class="dokit-computed-style dokit-section">
    <div class="dokit-box-model">
      <div
        class="dokit-position"
        v-if="highlightnNode.boxStyle.position !== 'static'"
      >
        <div class="dokit-label">position</div>
        <div class="dokit-top">{{ highlightnNode.boxStyle.top }}</div>
        <br />
        <div class="dokit-left">{{ highlightnNode.boxStyle.left }}</div>
        <ElementBoxModel
          :highlightnNode="highlightnNode"
          :contentWidth="contentWidth"
          :contentHeight="contentHeight"
        />
        <div class="dokit-right">{{ highlightnNode.boxStyle.right }}</div>
        <br />
        <div class="dokit-bottom">
          {{ highlightnNode.boxStyle.bottom }}
        </div>
      </div>
      <ElementBoxModel
        v-else
        :highlightnNode="highlightnNode"
        :contentWidth="contentWidth"
        :contentHeight="contentHeight"
      />
    </div>
  </div>
</template>

<script>
import ElementBoxModel from "./ElementBoxModel.vue";
export default {
  components: {
    ElementBoxModel,
  },
  props: {
    highlightElement: Object,
    highlightnNode: Object,
    contentWidth: String | Number,
    contentHeight: String | Number,
  },
};
</script>

<style lang="less" scoped>
.dokit-computed-style {
  font-size: 15px;
  .dokit-box-model {
    overflow-x: auto;
    color: #222;
    font-size: 12px;
    padding: 10px;
    text-align: center;
    white-space: nowrap;
    &:deep(.dokit-label) {
      position: absolute;
      margin-left: 3px;
      padding: 0 2px;
    }
    &:deep(.dokit-top),
    &:deep(.dokit-left),
    &:deep(.dokit-right),
    &:deep(.dokit-bottom) {
      display: inline-block;
    }
    &:deep(.dokit-left),
    &:deep(.dokit-right) {
      vertical-align: middle;
    }
    &:deep(.dokit-border),
    &:deep(.dokit-padding),
    &:deep(.dokit-content),
    &:deep(.dokit-position),
    &:deep(.dokit-margin) {
      position: relative;
      background: #fff;
      display: inline-block;
      text-align: center;
      vertical-align: middle;
      padding: 3px;
      margin: 3px;
    }
    .dokit-position {
      border: 1px grey dotted;
    }
    &:deep(.dokit-margin) {
      border: 1px dashed;
      background: rgba(246, 178, 107, 0.66);
    }
    &:deep(.dokit-border) {
      border: 1px #000 solid;
      background: rgba(255, 229, 153, 0.66);
    }
    &:deep(.dokit-padding) {
      border: 1px grey dashed;
      background: rgba(147, 196, 125, 0.55);
    }
    &:deep(.dokit-content) {
      border: 1px grey solid;
      min-width: 100px;
      background: rgba(111, 168, 220, 0.66);
      &:deep(span) {
        margin: 0;
        padding: 0;
        border: 0;
        font: inherit;
        vertical-align: baseline;
      }
    }
  }
}
</style>