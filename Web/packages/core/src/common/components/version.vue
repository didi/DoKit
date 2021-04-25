<template>
  <div class="card version">
    <div>
      <span class="version-text">当前版本：V{{version}}</span>
    </div>
    <div class="version-image">
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
.card {
  padding: 10px;
  background-color: white;
}
.version{
  padding: 20px 0;
  text-align: center;
  .version-text{
    font-size: 16px;
    color: #999999;
  }
  .version-image{
    margin-top: 20px;
  }
  .dokit-icon{
    width: 300px;
  }
}
</style>