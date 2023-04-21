<template>
  <div class="hello-independ" v-dragable>
    <div style="font-weight: bold; font-size: 30px; font-style: italic">
      实时帧率展示
    </div>
    <div>平均FPS: {{fps}}</div>
    <div>最小FPS: {{minFps}}</div>
    <div>最大FPS: {{maxFps}}</div>
    <div
      @click="remove"
      style="background-color: red; color: white; margin-top: 10px"
    >
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
      fps: 0,
      minFps: 60,
      maxFps: 60
    }
  },
  mounted() {
    console.log('moounted')
    this.showFPS()
  },
  methods: {
    remove() {
      removeIndependPlugin("test");
    },
    showFPS() {
      this.fps = 60
      var rAF = (function () {
        return (
          window.requestAnimationFrame ||
          window.webkitRequestAnimationFrame ||
          function (callback) {
            window.setTimeout(callback, 1000 / 60);
          }
        );
      })();
      var frame = 0;
      var allFrameCount = 0;
      var lastTime = Date.now();
      var lastFameTime = Date.now();
      var loop = () => {
        var now = Date.now();
        var fs = now - lastFameTime;
        var fps = Math.round(1000 / fs);

        lastFameTime = now;
        // 不置 0，在动画的开头及结尾记录此值的差值算出 FPS
        allFrameCount++;
        frame++;

        if (now > 1000 + lastTime) {
          var fps = Math.round((frame * 1000) / (now - lastTime));
          console.log(`${new Date()} 1S内 FPS：`, fps);
          this.fps = fps
          this.minFps = this.minFps > fps ? fps :  this.minFps
          this.maxFps = this.maxFps < fps ? fps : this.maxFps
          frame = 0;
          lastTime = now;
        }
        rAF(loop);
      };
      loop();
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