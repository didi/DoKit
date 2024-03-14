<template>
  <div class="hello-independ" v-dragable>
    <div style="font-weight: bold; font-size: 30px; font-style: italic">
      Hello doitsa
    </div>
    <div>Demo Inded Plugin</div>
    <div @click="remove" style="background-color: red; color:white;margin-top:10px">
      点击移除当前独立插件
    </div>
    <div>
    当前FPS: <span id="fps"></span><br>
    最大FPS: <span id="maxfps"></span><br>
    最小FPS: <span id="minfps"></span><br>
    <button @click="showFPS('fps');showmaxFPS('maxfps');showminFPS('minfps')">点我开始测试FPS</button>
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
    showFPS(id) {
    let requestAnimationFrame =
        window.requestAnimationFrame ||
        window.webkitRequestAnimationFrame ||
        window.mozRequestAnimationFrame ||
        window.oRequestAnimationFrame ||
        window.msRequestAnimationFrame ||
        function (callback) {
            window.setTimeout(callback, 1000 / 60);
        };

    let st = Date.now();
    let tt;
    let fps = 0;
    let frame = 0;

    let calcFPS = function () {
        (function loop() {
            tt = Date.now()
            if (tt > st + 1000) {
                fps = Math.round((frame * 1000) / (tt - st));
                st = Date.now();
                frame = 0;
                document.getElementById(id).innerHTML = fps;
            }
            frame++;
            requestAnimationFrame(loop);
        })();

    }
    calcFPS();

},
 showmaxFPS(id) {
    let requestAnimationFrame =
        window.requestAnimationFrame ||
        window.webkitRequestAnimationFrame ||
        window.mozRequestAnimationFrame ||
        window.oRequestAnimationFrame ||
        window.msRequestAnimationFrame ||
        function (callback) {
            window.setTimeout(callback, 1000 / 60);
        };

    let st = Date.now();
    let tt;
    let fps = 0;
    let frame = 0;
    let maxfps=0;

    let calcFPS = function () {
        (function loop() {
            tt = Date.now()
            if (tt > st + 1000) {
                fps = Math.round((frame * 1000) / (tt - st));
                st = Date.now();
                frame = 0;
                if(fps>maxfps)
                {
                  maxfps=fps;
                }
                document.getElementById(id).innerHTML = maxfps;
            }
            
            frame++;
            requestAnimationFrame(loop);
        })();

    }
    calcFPS();

},
 showminFPS(id) {
    let requestAnimationFrame =
        window.requestAnimationFrame ||
        window.webkitRequestAnimationFrame ||
        window.mozRequestAnimationFrame ||
        window.oRequestAnimationFrame ||
        window.msRequestAnimationFrame ||
        function (callback) {
            window.setTimeout(callback, 1000 / 60);
        };

    let st = Date.now();
    let tt;
    let fps = 0;
    let frame = 0;
    let minfps=100;

    let calcFPS = function () {
        (function loop() {
            tt = Date.now()
            if (tt > st + 1000) {
                fps = Math.round((frame * 1000) / (tt - st));
                st = Date.now();
                frame = 0;
                if(fps<minfps)
                {
                  minfps=fps;
                }
                document.getElementById(id).innerHTML = minfps;
            }
            frame++;
            requestAnimationFrame(loop);
        })();

    }
    calcFPS();

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
