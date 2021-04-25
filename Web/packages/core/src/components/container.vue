<template>
  <div class="container">
    <top-bar :title="title" :canBack="canBack">
    </top-bar>
    <div class="router-container">
      <router-view v-slot="{ Component }">
        <keep-alive>
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </div>
  </div>
</template>
<script>
import TopBar from "../common/components/top-bar";

export default {
  components: {
   TopBar 
  },
  data(){
    return {}
  },
  computed:{
    curRoute(){
      return this.$router.currentRoute.value
    },
    title(){
      return this.curRoute.meta.title || 'Dokit'
    },
    canBack(){
      return this.curRoute.name !== 'index'
    }
  },
  created(){
  }
}
</script>
<style lang="less" scoped>
.container{
  position: absolute;
  left: 0;
  right: 0;
  top:0;
  bottom: 0;
  background-color: #f5f6f7;
  display: flex;
  flex-direction: column;
}
.router-container{
  margin-top: 5px;
  background-color: white;
  flex: 1;
  overflow-y: scroll;
}
</style>