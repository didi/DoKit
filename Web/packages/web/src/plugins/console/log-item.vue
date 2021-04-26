<template>
  <div class="log-ltem">
    <div class="log-preview" v-html="logPreview" @click="toggleDetail"></div>
    <div v-if="showDetail && typeof value === 'object'">
      <div class="list-item" v-for="(key, index) in value" :key="index">
        <Detail :detailValue="key" :detailIndex="index"></Detail>
      </div>
    </div>
  </div>
</template>
<script>
import { getDataType, getDataStructureStr } from '../../assets/util'
import Detail from './log-detail'

const DATATYPE_NOT_DISPLAY = ['Number', 'String', 'Boolean']

export default {
  components: {
    Detail
  },
  props: {
    type: [Number],
    value: [String, Number, Object]
  },
  data () {
    return {
      showDetail: false
    }
  },
  computed: {
    logPreview () {
      let dataType = ''
      let html = `<div>`
      this.value.forEach(arg => {
        dataType = getDataType(arg)
        if (DATATYPE_NOT_DISPLAY.indexOf(dataType) === -1) {
          html += `<span class="data-type">${dataType}</span>`
        }
        html += `<span class="data-structure">${getDataStructureStr(arg, true)}</span>`
      });
      html += `</div>`
      return html
    }
  },
  methods: {
    toggleDetail () {
      this.showDetail = !this.showDetail
    }
  }
}
</script>
<style lang="less">
  .log-ltem{
    padding: 5px;
    border-top: 1px solid #eee;
    text-align: left;
    font-size: 12px;
  }
  .log-ltem:first-child {
    border: none;
  }
  .log-preview{
    .data-type{
      margin-left: 5px;
      margin-right: 5px;
      font-style: italic;
      font-weight: bold;
      color: #aaa;
    }
    .data-structure{
      font-style: italic;
    }
  }
</style>