<template>
  <div class="dokit-breadcrumb">
    <ul class="dokit-parents" v-if="parentTag.length > 0">
      <li v-for="(item, index) in parentTag" :key="index">
        <div class="dokit-parent" @click="selectElement(item)">
          <span class="nodeName">{{ item.nodeName.toLowerCase() }}</span>
          <span v-if="item.id !== ''" class="nodeaId">{{ `#${item.id}` }}</span>
          <span v-if="item.className !== ''" class="nodeaClass">{{
            `.${item.className}`
          }}</span>
        </div>
        <span class="dokit-icon-arrow-right">></span>
      </li>
    </ul>
    <div class="dokit-element" v-if="highlightElement">
      <span class="nodeName">{{
        highlightElement.nodeName.toLowerCase()
      }}</span>
      <span class="nodeaId" v-if="highlightElement.id !== ''">{{
        `#${highlightElement.id}`
      }}</span>
      <span class="nodeaClass" v-if="highlightElement.className !== ''">{{
        `.${highlightElement.className}`
      }}</span>
    </div>
  </div>
</template>

<script>
import { toggleElement } from "@dokit/web-core";
export default {
  props: {
    parentTag: Array,
    highlightElement: Object,
  },
  methods: {
    selectElement(item) {
      toggleElement(item);
    },
  },
};
</script>

<style lang="less" scoped>
.dokit-breadcrumb {
  .dokit-parents {
    overflow-x: auto;
    background: #f3f3f3;
    color: #333;
    padding: 10px;
    white-space: nowrap;
    border-bottom: 1px solid #ccc;
    font-size: 15px;
    margin: 0;
    li {
      display: inline-block;
    }
    .dokit-parent {
      display: inline-block;
    }
    .dokit-icon-arrow-right {
      font-size: 15px;
      margin: 0 5px;
    }
  }
  .dokit-element {
    background: #f3f3f3;
    color: #333;
    user-select: text;
    word-break: break-all;
    padding: 10px;
    font-size: 16px;
    border-bottom: 1px solid #ccc;
  }
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
</style>