<template>
  <div class="dokit-container">
    <top-bar :title="title" :canBack="canBack"></top-bar>
    <div class="dokit-router-container">
      <keep-alive>
        <component :is="component" />
      </keep-alive>
    </div>
  </div>
</template>
<script>
import TopBar from "@common/components/top-bar";

export default {
  components: {
   TopBar 
  },
  data(){
    return {}
  },
  computed:{
    component(){
      
      return this.$route.component
    },
    title(){
      return this.$route.meta && this.$route.meta.title || 'DoKit'
    },
    canBack(){
      return this.$route.name !== 'home'
    }
  }
}
</script>
<style lang="less" scoped>
.dokit-container{
  position: fixed;
  left: 0;
  right: 0;
  top:100px;
  bottom: 0;
  background-color: #f5f6f7;
  display: flex;
  flex-direction: column;
  z-index: 999;
  border-radius: 10px 10px 0 0;
}
.dokit-router-container{
  margin-top: 5px;
  background-color: white;
  flex: 1;
  overflow-y: scroll;
}
</style>