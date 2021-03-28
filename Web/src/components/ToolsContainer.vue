<template>
  <div class="tools-container">
    <div class="tools-tabs">
      <div
        v-for="tab in tabs"
        v-bind:key="tab.dispalyName"
        v-bind:class="['tab-button', { active: currentTab === tab.component }]"
        v-on:click="currentTab = tab.component"
      >
        {{ tab.dispalyName }}
      </div>
    </div>
    <keep-alive>
      <component v-bind:is="currentTool" class="tab"></component>
    </keep-alive>
  </div>
</template>
<script>
import ToolConsole from './ToolConsole'
import ToolAppInfo from './ToolAppInfo'
import ToolHelloWorld from './ToolHelloWorld'

export default {
  components: {
    ToolConsole,
    ToolHelloWorld,
    ToolAppInfo
  },
  data() {
    return {
      tabs: [{
        component: 'console',
        dispalyName: 'Console'
      },{
        component: 'app-info',
        dispalyName: 'AppInfo'
      },{
        component: 'hello-world',
        dispalyName: 'HelloWorld'
      }],
      currentTab: 'console'
    }
  },
  computed: {
    currentTool () {
      return `tool-${this.currentTab}`
    }
  },
}
</script>
<style lang="less">
.tools-container {
  height: 61.8%;
  width: 100%;
  overflow: hidden;
  position: fixed;
  bottom: 0;
  box-shadow:0px 0px 10px 5px #ddd;
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
  .tools-tabs{
    // position: absolute;
    // top: 0;
    width: 100%;
    white-space: nowrap;
    overflow-x: auto;
    text-align: left;
    border-bottom: 1px solid #ddd;
    padding: 0;
    .tab-button{
      font-size: 14px;
      display: inline-block;
      height: 30px;
      min-width: 50px;
      line-height: 30px;
      padding: 0 10px;
      background-color: white;
      text-align: center;
      border-left: 1px solid #eee;
    }
    .active{
      background-color: #337cc4;
      color: #fff;
      font-weight: bold;
    }
  }
}
</style>