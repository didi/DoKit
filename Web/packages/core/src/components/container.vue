<template>
  <div class="container">
    <router-view v-slot="{Component}">
      <keep-alive>
        <component :is="{Component}"/>
      </keep-alive>
    </router-view>
  </div>
</template>
<script>
export default {
  
}
</script>
<style lang="less" scoped>
.container{
  position: absolute;
  left: 0;
  right: 0;
  top:0;
  bottom: 0;
  background-color: #ffffff;
}
</style>