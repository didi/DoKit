<template>
  <div class="hello-independ" v-dragable>
    <div style="font-weight: bold; font-size: 30px; font-style: italic">
      实时内存监测
    </div>
    <!-- jsHeapSizeLimit 上下文内可用堆的最大体积，以字节计算。
         totalJSHeapSize 已分配的堆体积，以字节计算。 
         usedJSHeapSize  当前 JS 堆活跃段（segment）的体积，以字节计算。-->
    <div>上下文内可用堆的最大体积：{{jsHeapSizeLimit}}字节</div>
    <div>已分配的堆体积：{{totalJSHeapSize}}字节</div>
    <div>当前JS堆活跃段的体积：{{usedJSHeapSize}}字节</div>
    <div @click="remove" id='removeMemory' style="background-color: red; color:white;margin-top:10px">
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
  data() {
    return {
      jsHeapSizeLimit: 0,
      totalJSHeapSize: 0,
      usedJSHeapSize: 0
    }
  },
  mounted() {
    console.log('mounted')
    this.showMemory()
  },
  methods: {
    remove() {
      removeIndependPlugin("test");
    },
    showMemory() {
      // 功能检验（向document中加入一万个div）
      function f() {
        var date = +new Date();
        var str = [];
        for (var i = 1; i <= 10000; i++) {
            str.push('<div></div>');
        }
        str = str.join('');
        document.body.innerHTML = str;
        var date1 = +new Date();
        // console.log(date1 - date);
      }
      // 内存监测
      this.jsHeapSizeLimit = window.performance.memory.jsHeapSizeLimit
      this.totalJSHeapSize = window.performance.memory.totalJSHeapSize
      this.usedJSHeapSize = window.performance.memory.usedJSHeapSize
      var t = setInterval(() => {
        this.jsHeapSizeLimit = window.performance.memory.jsHeapSizeLimit
        this.totalJSHeapSize = window.performance.memory.totalJSHeapSize
        this.usedJSHeapSize = window.performance.memory.usedJSHeapSize
        console.log(this.jsHeapSizeLimit)
        // f(); 
      }, 1000)
      // 清除监测
      var removeMemory = document.getElementById('removeMemory');
      removeMemory.onclick = function(){
        clearInterval(t);
      }
    },
  },
};
</script>
<style scoped>
.hello-independ {
  display: inline-block;
  width: 500px;
  /* padding: 10px; */
  text-align: center;
  background-color: white;
  border-radius: 20px;
  box-shadow: 0 8px 12px #ebedf0;
  overflow: hidden;
  border: 1px solid red;
}
</style>