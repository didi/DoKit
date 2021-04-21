<template>
  <div class="console-container">
    <div class="log-container">
      <LogItem v-for="(log, index) in logList" :key="index" :value="log" ></LogItem>
    </div>
    <!--
      TODO: console input
    -->
  </div>
  
</template>
<script>
import LogItem from './console/LogItem'

export default {
  components: {
    LogItem
  },
  data() {
    return {
      logList: []
    }
  },
  created () {
    let self = this
    let originConsole = window.console;
    ['log'].forEach(type => {
      let origin =  originConsole[type].bind(originConsole)
      originConsole[type] = function (...args) {
        self.logList.push(args)
        origin(...args)
      }
    })
  }
}
</script>
<style lang="less">
.log-container{
  height: 100%;
  overflow-y: scroll;
}
</style>