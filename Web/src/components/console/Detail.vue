<template>
  <div class="detail-container" :class="[canFold ? 'can-unfold':'', unfold ? 'unfolded' : '']" >
    <div @click="unfoldDetail" v-html="displayDetailValue"></div>
    <template v-if="canFold">
      <div v-show="unfold" v-for="(key, index) in detailValue" :key="index">
        <Detail :detailValue="key" :detailIndex="index"></Detail>
      </div>
    </template>
  </div>
</template>
<script>
import Detail from './Detail'
import { getDataType } from '../../assets/util'

const TYPE_CAN_FOLD = ['Object', 'Array']
export default {
  components: {
    Detail
  },
  props: {
    detailValue: [String, Number, Object],
    detailIndex: [String, Number]
  },
  data () {
    return {
      unfold: false
    }
  },
  computed: {
    dataType () {
     return getDataType(this.detailValue)
    },
    canFold () {
      if (TYPE_CAN_FOLD.indexOf(this.dataType) > -1) {
        return true
      }
      return false
    },
    displayDetailValue () {
      let value = ''
      if (this.canFold) {
        if (this.dataType === 'Object') {
          value = 'Object'
        }
        if (this.dataType === 'Array') {
          value = `Array(${this.detailValue.length})`
        }
      } else {
        value = `<span style="color:#1802C7;">${this.detailValue}</span>`
      }
      return `<span style="color:#7D208C;">${this.detailIndex}</span>: ${value}`
    }
  },
  methods: {
    unfoldDetail() {
      this.unfold = !this.unfold
    }
  }
}
</script>
<style lang="less" scoped>
.detail-container{
  font-size: 12px;
  margin-left: 24px;
  position: relative;
  
}
.can-unfold {
  &::before{
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

.unfolded {
  &::before{
    border: 4px solid transparent;
    border-top-color: #333;
    top: 6px;
  }
}

</style>















