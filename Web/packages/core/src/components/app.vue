<template>
  <div class="dokit-app">
    <div class="dokit-entry-btn" v-dragable @click="toggleContainer"></div>
    <div class="mask" v-show="showContainer" @click="toggleContainer"></div>
    <router-container v-show="showContainer"></router-container>
  </div>
</template>

<script>
import dragable from "../common/directives/dragable";
import RouterContainer from './container';

export default {
  components: {
    RouterContainer
  },
  directives: {
    dragable,
  },
  data() {
    return {
      showContainer: false,
    };
  },
  methods: {
    toggleContainer() {
      this.showContainer = !this.showContainer;
    },
  },
};
</script>

<style lang="less" scoped>
.dokit-app {
  font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
}
.dokit-entry-btn {
  width: 50px;
  height: 50px;
  padding: 10px;
  box-sizing: border-box;
  background-image: url(//pt-starimg.didistatic.com/static/starimg/img/OzaetKDzHr1618905183992.png);
  background-size: 50px;
  background-position: center;
  background-repeat: no-repeat;
}
.mask{
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 3;
  background-color: #333333;
  opacity: 0.3;
}
</style>