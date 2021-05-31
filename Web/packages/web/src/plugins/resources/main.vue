<template>
  <div class="all-resources-container">
    <resource-tap :tabs="resourceTabs" @changeTap="handleChangeTab"></resource-tap>
    <div class="resource-container">
      <resource-container :resourceList = "curResourceList"></resource-container>
    </div>
  </div>
</template>

<script>
import ResourceTap from './resource-tap';
import ResourceContainer from './resource-container';
import {ResourceTabs, ResourceEnum, getResourceEntries} from './js/resources';

export default {
  components: {
    ResourceTap,
    ResourceContainer,
  },
  data() {
    return {
      resourceTabs: ResourceTabs,
      curTab: ResourceEnum.CSS,
    }
  },
  computed:{
    resourceList(){
      let resourceList = this.$store.state.resourceList || [];
      
      return resourceList
    },
    curResourceList(){
      return this.resourceList.filter(resource => {
        return resource.type == this.curTab
      })
    },
  },
  created () {
    //console.log(window.performance.now())
  },
  mounted (){
    
  },
  activated(){
    this.$nextTick(()=>{
      let resourceList = [];
      getResourceEntries(({ type, initiatorType, entryName, base64}) => {
        resourceList.push({
          type: type,
          initiatorType: initiatorType,
          entryName: entryName,
          base64: base64
        });
      })
      
      this.$store.state.resourceList = resourceList
    })
  },
  methods: {
    handleChangeTab(type){
      this.curTab = type
      //console.log(this.$store.state.resourceList)
    }
  }
}
</script>

<style lang="less" scoped>
@import "./css/var.less";
.all-resources-container{
  display: flex;
  flex-direction: column;
  height: 100%;
}
.resource-container{
  flex: 1;
  display: flex;
  flex-direction: column;
  word-break: break-all;
  overflow-y: scroll;
  
    background-color: @background-color;
    border-bottom: 1px solid @border-color;
    
}
</style>