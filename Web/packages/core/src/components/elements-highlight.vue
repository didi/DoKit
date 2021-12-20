<template>
  <div
    class="dokit-elements-highlight"
    :style="`
          top: ${element.getBoundingClientRect().top}px;
          left: ${element.getBoundingClientRect().left}px;
        `"
  >
    <div class="dokit-elements-indicator">
      <div
        class="dokit-margin"
        :style="`
          left: -${highlightnNode.boxStyle.marginLeft}px;
          top: -${highlightnNode.boxStyle.marginTop}px;
          width: ${element.offsetWidth}px;
          height: ${element.offsetHeight}px;
          border-width: ${highlightnNode.boxStyle.marginTop}px ${highlightnNode.boxStyle.marginRight}px ${highlightnNode.boxStyle.marginBottom}px ${highlightnNode.boxStyle.marginLeft}px;
          border-style: solid;
          border-color: rgba(246, 178, 107, 0.66);
        `"
      ></div>
      <div
        class="dokit-border"
        :style="`
          left: 0px;
          top:0px;
          width: ${element.offsetWidth}px;
          height: ${element.offsetHeight}px;
          border-width: ${highlightnNode.boxStyle.borderTopWidth}px ${highlightnNode.boxStyle.borderRightWidth}px ${highlightnNode.boxStyle.borderBottomWidth}px ${highlightnNode.boxStyle.borderLeftWidth}px;
          border-style: solid;
          border-color: rgba(255, 229, 153, 0.66);
        `"
      ></div>
      <div
        class="dokit-padding"
        :style="`
          left: ${highlightnNode.boxStyle.borderLeftWidth}px;
          top: ${highlightnNode.boxStyle.borderTopWidth}px;
          width: ${highlightnNode.boxStyle.contentWidth}px;
          height: ${highlightnNode.boxStyle.contentHeight}px;
          border-width: ${highlightnNode.boxStyle.paddingTop}px ${highlightnNode.boxStyle.paddingRight}px ${highlightnNode.boxStyle.paddingBottom}px ${highlightnNode.boxStyle.paddingLeft}px;
          border-style: solid;
          border-color: rgba(147, 196, 125, 0.55);
        `"
      ></div>
      <div
        class="dokit-content"
        :style="`
          left: ${contentLeft};
          top: ${contentTop};
          width: ${highlightnNode.boxStyle.contentWidth}px;
          height: ${highlightnNode.boxStyle.contentHeight}px;
          background: rgba(111, 168, 220, 0.66);
        `"
      ></div>
    </div>
    <div class="dokit-elements-size" :style="isOverfllow">
      <span class="nodeName">{{ element.nodeName.toLowerCase() }}</span>
      <span v-if="element.id !== ''" class="nodeaId">{{
        `#${element.id}`
      }}</span>
      <span v-if="element.className !== ''" class="nodeaClass">{{
        `.${element.className}`
      }}</span>
      <span> | {{ element.offsetWidth }} × {{ element.offsetHeight }}</span>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    element: Object,
  },
  data() {
    return {};
  },
  computed: {
    highlightnNode() {
      return this.element?.__dokitForWeb_node;
    },
    isOverfllow() {
      let offsetTop =
        this.element.getBoundingClientRect().top +
        this.highlightnNode.boxStyle.marginTop;
      if (offsetTop < 25) {
        return `
          top: 0px;
          left: 0px;
        `;
      } else {
        return `
          top: ${-25 - this.highlightnNode.boxStyle.marginTop}px;
          left: ${-this.highlightnNode.boxStyle.marginLeft}px;
        `;
      }
    },
    contentLeft() {
      let elPaddingLeft = parseInt(this.highlightnNode.boxStyle.paddingLeft);
      let elBorderLeftWidth = parseInt(this.highlightnNode.boxStyle.borderLeftWidth);
      return `${elPaddingLeft + elBorderLeftWidth}px`;
    },
    contentTop() {
      let elPaddingTop = parseInt(this.highlightnNode.boxStyle.paddingTop);
      let elBorderTopWidth = parseInt(this.highlightnNode.boxStyle.borderTopWidth);
      return `${elPaddingTop + elBorderTopWidth}px`;
    },
  },
};
</script>

<style lang="less" scoped>
.dokit-elements-highlight {
  position: absolute;
  z-index: -100;
  pointer-events: none !important;
  .dokit-elements-indicator {
    position: absolute;
    left: 0;
    right: 0;
    width: 100%;
    height: 100%;
    & > * {
      pointer-events: none !important;
    }
    .dokit-margin,
    .dokit-border,
    .dokit-padding,
    .dokit-content {
      position: absolute;
    }
    .dokit-border {
      box-sizing: border-box;
    }
  }
  .dokit-elements-size {
    position: absolute;
    background: #fff;
    color: #222;
    font-size: 12px;
    height: 25px;
    line-height: 25px;
    text-align: center;
    padding: 0 5px;
    white-space: nowrap;
    overflow-x: hidden;
    box-shadow: 0 2px 2px 0 rgb(0 0 0 / 5%), 0 1px 4px 0 rgb(0 0 0 / 8%),
      0 3px 1px -2px rgb(0 0 0 / 20%);
    .nodeName {
      color: #881280;
    }
    .nodeaId {
      color: #1a1aa8;
    }
    .nodeaClass {
      color: rgb(143, 73, 25);
    }
  }
}
</style>
