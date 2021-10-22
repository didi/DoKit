<template>
  <div class="operation">
    <div class="input-wrapper">
      <input class="input" placeholder="Command……" v-model="command" />
    </div>
    <div class="button-wrapper" @click="excuteCommand">
      <span>Excute</span>
    </div>
  </div>
</template>
<style lang="less" scoped>
@import "./css/var.less";
.operation {
  height: 50px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.input-wrapper {
  flex: 1;
  height: 100%;
  .input {
    height: 100%;
    width: 100%;
    outline: none;
    border: none;
    line-height: 100%;
    padding: 0 10px;
    font-size: 18px;
  }
}
.button-wrapper {
  height: 100%;
  line-height: 100%;
  margin-left: 10px;
  padding: 0 10px;
  border-left: 1px solid @border-color;
  display: flex;
  align-items: center;
  font-size: 18px;
}
</style>
<script>
import {excuteScript} from './js/console'
export default {
  data(){
    return {
      command: ""
    }
  },
  methods: {
    excuteCommand(){
      if(!this.command){
        return
      }
      try {
        let ret = excuteScript(this.command)
        console.log(ret)
      } catch (error) {
        console.error(error)
      }
      this.command = ''
    }
  }
};
</script>