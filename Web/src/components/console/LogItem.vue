<template lang="">
  <div class="log-ltem">
    <div class="log-preview" v-html="logPreview"></div>
    <div class="list-item" v-if="typeof value === 'object'" v-for="key in value">
        <div class="item-name">
            <span>{{key}}</span>
        </div>
    </div>
  </div>
</template>
<script>
import { getDataType, getDataStructureStr } from './../../assets/util'

const DATATYPE_NOT_DISPLAY = ['Number', 'String', 'Boolean']

export default {
  components: {
  },
  props: {
    value: [String, Number, Object]
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
        html += `${getDataStructureStr(arg)}` 
      });
      html += `</div>`
      return html
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
      font-style: italic;
      font-weight: bold;
      color: #aaa;
    }
  }
</style>