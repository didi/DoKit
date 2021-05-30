<template>
  <div class="hello-independ" v-dragable>
    <div style="font-weight: bold; font-size: 30px; font-style: italic">
      Hello Dokit
    </div>
    <div>Demo Independ Plugin</div>
    <div @click="remove" style="background-color: red; color:white;margin-top:10px">
      点击移除当前独立插件
    </div>
  </div>
</template>
<script>
import { dragable } from "@dokit/web-utils";
import { removeIndependPlugin } from "@dokit/web-core";

export default {
  directives: {
    dragable,
  },
  methods: {
    remove() {
      removeIndependPlugin("test");
    },
  },
};
</script>
<style scoped>
.hello-independ {
  display: inline-block;
  width: 200px;
  /* padding: 10px; */
  text-align: center;
  background-color: white;
  border-radius: 20px;
  box-shadow: 0 8px 12px #ebedf0;
  overflow: hidden;
  border: 1px solid red;
}
</style>