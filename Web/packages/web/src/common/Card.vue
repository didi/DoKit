<template>
  <div class="dokit-common-card">
    <div class="dokit-common-card-header">
      <div class="dokit-common-card-header__title">{{ title }}</div>
      <div class="dokit-common-card-header__extra-info">
        <slot name="extra"></slot>
      </div>
    </div>
    <div class="dokit-common-card-body">
      <slot name="body"></slot>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    title: {
      default: ""
    }
  }
};
</script>
<style lang="less">
.dokit-common-card {
  border-radius: 5px;
  overflow: hidden;
  border: 1px solid #d6e4ef;

}

.dokit-common-card-header {
  border-bottom: 1px solid #d6e4ef;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .dokit-common-card-header__title {
    padding: 8px;
    font-size: 18px;
    color:#2c405a;
  }
  
}

.dokit-common-card-body {
  padding: 8px;
}
</style>