<template>
  <div class="dokit-position-infobox-container">
    位置: 左 {{ position.left }}, 右 {{ position.right }}, 上 {{ position.top }}, 下 {{ 
      position.bottom
    }}
    <span class="dokit-close-button" @click="$emit('remove')"></span>
  </div>
</template>

<script>
export default {
  props: {
    position: {
      top: 0,
      right: 0,
      bottom: 0,
      left: 0,
    },
  },
};
</script>

<style scoped>
.dokit-position-infobox-container {
  position: fixed;
  left: 0;
  right: 0;
  margin: auto;
  bottom: 20px;

  width: 250px;
  height: 50px;
  padding: 0px 20px;
  line-height: 50px;
  font-size: 12px;

  border-radius: 5px;
  box-shadow: 0px 0px 2px #cccccc;
}

.position-info {
  font-size: 10px;
}

.dokit-close-button {
  background: #324456;
  color: #fff;

  border-radius: 10px;
  width: 20px;
  height: 20px;

  line-height: 18px;
  font-size: 15px;
  text-align: center;

  top: 15px;
  right: 10px;
  position: absolute;
}

.close-button::before {
  content: "×";
}
</style>