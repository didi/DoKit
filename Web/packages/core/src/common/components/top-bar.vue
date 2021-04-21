<template>
  <div class="bar">
    <div class="bar-back" @click="handleBackRoute">
      <img class="bar-back-icon" :src="icon"/>
      <span class="bar-back-btn">返回</span>
    </div>
    <div class="bar-title">
      <span class="bar-title-text">{{title}}</span>
    </div>
    <div class="bar-other">
      <span class="bar-other-text">更多</span>
    </div>
  </div>
</template>
<script>
import {IconBack} from '../js/icon';
export default {
  props: {
    title: {
      default: 'DoKit'
    }
  },
  data(){
    return {
      icon: IconBack
    }
  },
  methods: {
    handleBackRoute(){
      this.$router.back()
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
    justify-content: space-between;
    align-items: center;
    padding: 0 10px;
    box-sizing: border-box;
  }
  .bar-back{
    display: flex;
    flex-direction: row;
    align-items: center;
  }
  .bar-back-icon{
    display: inline-block;
    height: 20px;
  }
  .bar-back-btn{
    color: #337CC4;
    font-size: 19px;
    margin-left: 5px;
  }
  .bar-title-text{
    color: #333333;
    font-size: 22px;
  }
  .bar-other-text{
    color:  #666666;
    font-size: 15px;
  }
</style>