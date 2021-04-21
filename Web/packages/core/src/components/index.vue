<template>
  <div class="container">
    <top-bar></top-bar>
    <card
      v-for="(item, index) in features"
      :key="index"
      :title="item.title"
      :list="item.list"
    ></card>
    <version-card :version="version"></version-card>
  </div>
</template>
<script>
import TopBar from "../common/components/top-bar";
import Card from "../common/components/card";
import VersionCard from "../common/components/version";
export default {
  components: {
    TopBar,
    Card,
    VersionCard
  },
  data(){
    return {
      version: '1.0.0'
    }
  },
  mounted(){
  },
  computed: {
    features(){
      return this.$store.state.features
    }
  }
};
</script>
<style lang="less" scoped>
.container {
  background-color: #f5f6f7;
}
</style>