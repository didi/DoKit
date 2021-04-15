<template lang="">
  <div class="dokit-card">
    <div class="dokit-card__header">{{title}}</div>
    <div class="dokit-card__body">
      <slot/>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    title: String
  }
}
</script>
<style lang="less">
.dokit-card {
  border-radius: 10px;
  box-shadow: 0 8px 12px #ebedf0;
  overflow: hidden;
  .dokit-card__header{
    background-color: #337cc4;
    padding: 10px;
    color: #fff;
  }
  .dokit-card__body{
    padding: 10px;
  }
}
</style>