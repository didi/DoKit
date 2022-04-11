<template>
  <div
    class="dokit-detail-container"
    :class="[canFold ? 'dokit-can-unfold' : '', unfold ? 'dokit-unfolded' : '']"
  >
    <div @click="unfoldDetail" v-html="displayDetailValue"></div>
    <template v-if="canFold">
      <div v-show="unfold" v-for="(key, index) in newDetailValue" :key="index">
        <Detail :detailValue="key" :detailIndex="index"></Detail>
      </div>
    </template>
  </div>
</template>
<script>
import Detail from "./log-detail";
import { clone } from '@dokit/web-utils'
import { getDataType } from '../../assets/util'
export default {
  name: "Detail",
  components: {
    Detail,
  },
  props: {
    detailValue: [String, Number, Object],
    detailIndex: [String, Number],
  },
  data() {
    return {
      unfold: false,
      newDetailValue:null,
    };
  },
  computed: {
    dataType () {
     return getDataType(this.newDetailValue)
    },
    canFold() {
      if ((this.isObject(this.newDetailValue)) || this.isArray(this.newDetailValue)) {
        return true;
      }
      return false;
    },
    displayDetailValue() {
      let value = "";
      if (this.canFold) {
        if (this.isObject(this.newDetailValue)) {
          if(this.dataType !== 'Function'){
            value = "Object";
          } else{
            value = "Function";
          }
        }
        if (this.isArray(this.newDetailValue)) {
          value = `Array(${this.newDetailValue.length})`;
        }
      } else {
        value = `<span style="color:#1802C7;">${this.newDetailValue}</span>`;
      }
      return `<span style="color:#7D208C;">${this.detailIndex}</span>: ${value}`;
    },
  },
  watch: {
    detailValue: {
      immediate: true,
      handler(newVal, oldVal) {
        this.newDetailValue = clone(newVal)
      },
    },
  },
  methods: {
    unfoldDetail() {
      this.unfold = !this.unfold;
    },
    isObject(val) {
      return Object.prototype.isPrototypeOf(val);
    },
    isArray(val) {
      return Array.prototype.isPrototypeOf(val);
    },
  },
};
</script>
<style lang="less" scoped>
.dokit-detail-container {
  font-size: 12px;
  margin-left: 24px;
  position: relative;
  white-space: pre-wrap;
  word-wrap: break-word;
  max-width: 100%;
}
.dokit-can-unfold {
  &::before {
    content: "";
    width: 0;
    height: 0;
    border: 4px solid transparent;
    position: absolute;
    border-left-color: #333;
    left: -12px;
    top: 3px;
  }
}

.dokit-unfolded {
  &::before {
    border: 4px solid transparent;
    border-top-color: #333;
    top: 6px;
  }
}
</style>















