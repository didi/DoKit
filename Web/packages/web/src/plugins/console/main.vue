<template>
  <div class="console-container">
    <console-tap :tabs="logTabs" @changeTap="handleChangeTab"></console-tap>
    <div class="log-container">
      <div class="info-container">
        <log-container :logList="curLogList"></log-container>
      </div>
      <div class="operation-container">
        <operation-command></operation-command>
      </div>
    </div>
  </div>
</template>
<script>
import ConsoleTap from './console-tap';
import LogContainer from './log-container';
import OperationCommand from './op-command';
import {LogTabs, LogEnum} from './js/console'
export default {
  components: {
    ConsoleTap,
    LogContainer,
    OperationCommand
  },
  data() {
    return {
      logTabs: LogTabs,
      curTab: LogEnum.ALL
    }
  },
  computed:{
    logList(){
      return this.$store.state.logList || []
    },
    curLogList(){
      if(this.curTab == LogEnum.ALL){
        return this.logList
      }
      return this.logList.filter(log => {
        return log.type == this.curTab
      })
    }
  },
  created () {},
  methods: {
    handleChangeTab(type){
      this.curTab = type
    }
  }
}
</script>
<style lang="less" scoped>
@import "./css/var.less";

.console-container{
  display: flex;
  flex-direction: column;
  height: 100%;
}
.log-container{
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  .info-container{
    flex: 1;
    background-color: @background-color;
    border-bottom: 1px solid @border-color;
    overflow-y: scroll;
  } 
}

</style>