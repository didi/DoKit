<template>
  <div class="dokit-container" style="z-index: 998;">
    <component :is="toRaw(plugin.component)" v-for="plugin in independPlugins" :key="plugin.name"/>
  </div>
</template>
<script>
// 所有独立插件共用一个容器
import { toRaw } from 'vue';

export default {
  components: {},
  data(){
    return {}
  },
  computed:{
    independPlugins(){
      return this.$store.state.independPlugins
    }
  },
  methods: {
    toRaw: toRaw
  }
}
</script>
<style lang="less" scoped>
.dokit-container{
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  top: 0;
}
</style>