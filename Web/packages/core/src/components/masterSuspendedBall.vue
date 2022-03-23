<template>
  <div>
    <div class="masterSuspendedBall" @click="change" v-dragable="btnConfig">
      {{ isMaster ? "主机" : "从机" }}
    </div>
  </div>
</template>

<script>
import { dragable } from "@dokit/web-utils";
export default {
  directives: {
    dragable,
  },
  data() {
    return {
      isMaster: false,
      btnConfig: {
        name: "dokit_multi_control",
        opacity: 0.5,
        left: window.innerWidth - 50,
        top: window.innerHeight * 0.2,
        safeBottom: 50,
        eventPlayback: null,
      },
    };
  },
  computed: {
    isMaster() {
      return this.$store.state.isMaster;
    },
  },
  methods: {
    change() {
      this.$store.state.isMaster = !this.$store.state.isMaster;
    },
  },
};
</script>

<style lang="less" scoped>
.masterSuspendedBall {
  // position: fixed;
  z-index: 10000;
  // opacity: 0.5;
  // right: 50px;
  // top: 20%;
  font-size: 16px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ccc;
}
</style>