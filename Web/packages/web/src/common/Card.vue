<template>
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
  background-color: #d9e1e8;
  border-radius: 5px;
  box-shadow: 0 8px 12px #ebedf0;
  overflow: hidden;
  .dokit-card__header{
    background-color: #2b90d9;
    padding: 5px;
    color: #fff;
    font-weight: bold;
    font-style: italic;
  }
  .dokit-card__body{
    padding: 5px;
  }
}
</style>