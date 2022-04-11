<template>
  <div class="dokit-log-ltem" :class="logType">
    <div class="dokit-log-preview" v-html="logPreview" @click="toggleDetail"></div>
    <div v-if="canShowDetail">
      <div class="dokit-list-item" v-for="(key, index) in value" :key="index">
        <Detail :detailValue="key" :detailIndex="index"></Detail>
      </div>
    </div>
  </div>
</template>
<script>
import { getDataType, getDataStructureStr } from '../../assets/util'
import Detail from './log-detail'

const DATATYPE_NOT_DISPLAY = ['Number', 'String', 'Boolean', 'Undefined', 'Null']
export default {
  components: {
    Detail
  },
  props: {
    type: [Number],
    value: [String, Number, Object],
    logType: [String]
  },
  data () {
    return {
      showDetail: false
    }
  },
  computed: {
    logPreview () {
      let dataType = ''
      let func = null
      let html = `<div>`
      if (this.logType === 'log' || this.logType === 'info') {
        func = arg => {
          dataType = getDataType(arg)
          if (DATATYPE_NOT_DISPLAY.indexOf(dataType) === -1) {
            html += `<span class="data-type">${dataType}</span>`
          }
          html += `<span class="data-structure">${getDataStructureStr(arg, true)}</span>`
        }
        // this.value.forEach(arg => {
        //   dataType = getDataType(arg)
        //   if (DATATYPE_NOT_DISPLAY.indexOf(dataType) === -1) {
        //     html += `<span class="data-type">${dataType}</span>`
        //   }
        //   html += `<span class="data-structure">${getDataStructureStr(arg, true)}</span>`
        // });
      } else if (this.logType === 'error' || this.logType === 'warn') {
        func = arg => {
          if (arg.stack) {
            html += `<span style="white-space: pre-wrap;">${arg.stack}</span>`
          } else {
            dataType = getDataType(arg)
            if (DATATYPE_NOT_DISPLAY.indexOf(dataType) === -1) {
              html += `<span class="data-type">${dataType}</span>`
            }
            html += `<span class="data-structure">${getDataStructureStr(arg, true)}</span>`
          }
        }
      } else {
        
      }
      
      this.value.forEach(func);

      html += `</div>`
      return html
    },
    canShowDetail () {
      return this.showDetail 
        && typeof this.value === 'object'
        && !this.value.stack
    }
  },
  methods: {
    toggleDetail () {
      this.showDetail = !this.showDetail
    }
  }
}
</script>
<style lang="less" scoped>
  .dokit-log-ltem{
    padding: 5px;
    padding-left: 20px;
    border-top: 1px solid #eee;
    text-align: left;
    font-size: 12px;
  }
  .log{
    
  }
  .info{
    background-color: #ECF1F7;
    position: relative;
    &::before{
      content:"";
      background:url("https://pt-starimg.didistatic.com/static/starimg/img/M3nz7HYPH21621412737959.png") no-repeat;
      background-size: 10px 10px;
      width: 10px;
      height: 10px;
      position: absolute;
      top: 7px;
      left: 8px;
    }
  }
  .warn{
    background-color: #FFFBE4;
    color: #5C3C01;
    position: relative;
    &::before{
      content:"";
      background:url("https://pt-starimg.didistatic.com/static/starimg/img/39hzJzObhZ1621411397522.png") no-repeat;
      background-size: 10px 10px;
      width: 10px;
      height: 10px;
      position: absolute;
      top: 7px;
      left: 8px;
    }
  }
  .error{
    background-color: #FEF0F0;
    color: #FF161A;
    position: relative;
    &::before{
      content:"";
      background:url("https://pt-starimg.didistatic.com/static/starimg/img/z6EndYs29d1621411397532.png") no-repeat;
      background-size: 10px 10px;
      width: 10px;
      height: 10px;
      position: absolute;
      top: 7px;
      left: 8px;
    }
  }
  .log-ltem:first-child {
    border: none;
  }
  .dokit-log-preview{
    & >>> .data-type{
      margin-left: 5px;
      margin-right: 5px;
      font-style: italic;
      font-weight: bold;
      color: #aaa;
    }
    & >>> .data-structure{
      display: inline-block;
      max-width: 100%;
      font-style: italic;
      white-space: pre-wrap;
      word-wrap: break-word;
      overflow: hidden;
    }
  }
</style>