<template>
  <div class="dokit-card dokit-version">
    <div>
      <span class="dokit-version-text">当前版本：V{{version}}</span>
    </div>
    <div class="dokit-version-image">
      <img class="dokit-icon" :src="dokitIcon"/>
    </div>
  </div>
</template>
<script>
import {dokitIcon} from '../js/icon'
export default {
  props: {
    version: String
  },
  data(){
    return {
      dokitIcon: dokitIcon
    }
  }
}
</script>
<style lang="less" scoped>
.dokit-card {
  padding: 10px;
  background-color: white;
}
.dokit-version{
  padding: 20px 0;
  text-align: center;
  .dokit-version-text{
    font-size: 16px;
    color: #999999;
  }
  .dokit-version-image{
    margin-top: 20px;
  }
  .dokit-icon{
    width: 150px;
  }
}
</style>