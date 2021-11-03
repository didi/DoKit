<template>
  <div class="dokit-app">
    <div
      class="dokit-entry-btn"
      style="z-index: 10000"
      v-dragable="btnConfig"
      @click="toggleShowContainer"
    ></div>
    <div class="mask" v-show="showContainer" @click="toggleContainer"></div>
    <router-container v-show="showContainer"></router-container>
    <independ-container v-show="independPlugins.length"></independ-container>
    <elements-highlight
      v-if="showHighlightElement && highlightElement"
      :element="highlightElement"
    ></elements-highlight>
  </div>
</template>

<script>
import { dragable } from "@dokit/web-utils";
import RouterContainer from "./router-container";
import IndependContainer from "./independ-container";
import ElementsHighlight from "./elements-highlight.vue";
import { toggleContainer } from "@store/index";

export default {
  components: {
    RouterContainer,
    IndependContainer,
    ElementsHighlight,
  },
  directives: {
    dragable,
  },
  data() {
    return {
      btnConfig: {
        name: "dokit_entry",
        opacity: 0.5,
        left: window.innerWidth - 50,
        top: window.innerHeight - 100,
        safeBottom: 50,
      },
    };
  },
  computed: {
    highlightElement() {
      return this.state.highlightElement;
    },
    showHighlightElement() {
      return this.state.showHighlightElement;
    },
    state() {
      return this.$store.state;
    },
    showContainer() {
      return this.state.showContainer;
    },
    independPlugins() {
      return this.$store.state.independPlugins;
    },
  },
  methods: {
    toggleShowContainer() {
      toggleContainer();
    },
  },
};
</script>

<style lang="less" scoped>
.dokit-app {
  font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
  pointer-events: none;
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: 100000;
  & > * {
    pointer-events: all;
  }
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
.mask {
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