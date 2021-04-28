<template>
  <div class="container" style="z-index: 999;">
    <div class="plugin-container">
      <div>
        I Am Single Container! I am the boss;
      </div>
    </div>
  </div>
</template>
<script>

export default {
  components: {},
  data(){
    return {}
  },
  computed:{
    singlePlugins(){
      return this.$store.state.singlePlugins
    }
  },
  created(){
    console.log(this.$router)
  }
}
</script>
<style lang="less" scoped>
.container{
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
}
.plugin-container{
  background-color: #ffffff;
  box-shadow: 1px 1px 2px #333333;
}
</style>