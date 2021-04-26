<template>
  <div class="bar">
    <div class="bar-back" @click="handleBackRoute" v-show="canBack">
      <img class="bar-back-icon" :src="icon"/>
      <span class="bar-back-btn">返回</span>
    </div>
    <div class="bar-title">
      <span class="bar-title-text">{{title}}</span>
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
  .bar{
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
  .bar-back{
    position: absolute;
    left: 10px;
    display: flex;
    flex-direction: row;
    align-items: center;
  }
  .bar-back-icon{
    display: inline-block;
    height: 18px;
  }
  .bar-back-btn{
    color: #337CC4;
    font-size: 16px;
    margin-left: 5px;
  }
  .bar-title-text{
    color: #333333;
    font-size: 20px;
  }
  .bar-other-text{
    color:  #666666;
    font-size: 16px;
  }
</style>