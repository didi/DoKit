<template>
  <div class="container">
    <top-bar :title="title"></top-bar>
    <router-view v-slot="{ Component }">
      <keep-alive>
        <component :is="Component" />
      </keep-alive>
    </router-view>
  </div>
</template>
<script>
import TopBar from "../common/components/top-bar";

export default {
  components: {
   TopBar 
  },
  data(){

  },
  computed:{
    curRoute(){
      return this.$router.currentRoute.value
    },
    title(){
      return this.curRoute.meta.title || 'Dokit'
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
}
</style>