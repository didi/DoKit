<template>
  <div class="dokit-bar">
    <div class="dokit-bar-back" @click="handleBackRoute" v-show="canBack">
      <img class="dokit-bar-back-icon" :src="icon"/>
      <span class="dokit-bar-back-btn">返回</span>
    </div>
    <div class="dokit-bar-title">
      <span class="dokit-bar-title-text">{{title}}</span>
    </div>
    <!-- TODO 支持切换模式 -->
    <!-- <div class="bar-other">
      <span class="bar-other-text">更多</span>
    </div> -->
  </div>
</template>
<script>
import {IconBack} from '../js/icon';
export default {
  props: {
    title: {
      default: 'DoKit'
    },
    canBack:{
      default: true
    }
  },
  data(){
    return {
      icon: IconBack
    }
  },
  methods: {
    handleBackRoute(){
      if(this.$emit('back')){
        // 支持拦截 back
      }else {
        this.$router.back()
      }
    }
  }
}
</script>
<style lang="less" scoped>
  .dokit-bar{
    background-color: white;
    height: 50px;
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0 10px;
    box-sizing: border-box;
    position: relative;
    border-radius: 10px 10px 0 0;
  }
  .dokit-bar-back{
    position: absolute;
    left: 10px;
    display: flex;
    flex-direction: row;
    align-items: center;
  }
  .dokit-bar-back-icon{
    display: inline-block;
    height: 18px;
  }
  .dokit-bar-back-btn{
    color: #337CC4;
    font-size: 16px;
    margin-left: 5px;
  }
  .dokit-bar-title-text{
    color: #333333;
    font-size: 20px;
    font-weight: bold;
  }
  .dokit-bar-other-text{
    color:  #666666;
    font-size: 16px;
  }
</style>