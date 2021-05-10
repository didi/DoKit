<template>
  <div class="index-container">
    <card
      v-for="(item, index) in features"
      :key="index"
      :title="item.title"
      :list="item.list"
      @handleClickItem="handleClickItem"
    ></card>
    <version-card :version="version"></version-card>
  </div>
</template>
<script>
import TopBar from "@common/components/top-bar";
import Card from "@common/components/card";
import VersionCard from "@common/components/version";
import {pushContainer} from '@store/index';
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
  },
  methods: {
    handleClickItem(item){
      switch(item.type){
        case "RouterPlugin":
          this.$router.push({
            name: item.name
          })
          break;
        case "SinglePlugin":
          pushContainer(item)
          break;
        default:
          break;
      }
    }
  }
};
</script>
<style lang="less" scoped>
.index-container {
  background-color: #f5f6f7;
}
</style>