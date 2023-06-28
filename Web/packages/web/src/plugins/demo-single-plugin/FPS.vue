<template>
  <div class="hello-independ" v-dragable>
    <div
      style="
        font-weight: bold;
        font-size: 30px;
        font-style: italic;
        color: #ce7c6e;
      "
    >
      实时帧率展示
    </div>
    <div style="color: #ce7c6e">
      平均FPS: {{ avgFps }} {{ "\xa0\xa0\xa0\xa0" }} 最小FPS: {{ minFps }}
      {{ "\xa0\xa0\xa0\xa0" }} 最大FPS: {{ maxFps }}
    </div>
    <div
      class="nowEcharts"
      id="nowEcharts"
      style="width: 800px; height: 400px"
    ></div>
    <button @click="remove">点击移除当前独立插件</button>
  </div>
</template>
<script>
import { dragable } from "@dokit/web-utils";
import { removeIndependPlugin } from "@dokit/web-core";
import * as echarts from "echarts";

export default {
  directives: {
    dragable,
  },
  data() {
    return {
      fps: 0,
      avgFps: 60,
      minFps: 60,
      maxFps: 60,
      oneDay: 1000,
      nowOptions: {
        visualMap: [
          {
            show: false,
            type: "continuous",
            seriesIndex: 0,
            min: 0,
            max: 400,
          },
        ],
        title: {
          left: "left",
          text: "实时帧率折线图",
          textStyle: {
            color: "#ce7c6e",
          },
        },
        grid: {
          top: "15%",
          bottom: "10%",
        },
        xAxis: {
          type: "time",
          axisLabel: {
            textStyle: {
              color: "#ce7c6e",
            },
          },
          axisLine: {
            lineStyle: {
              type: "solid",
              color: "#ce7c6e",
              width: "1",
            },
          },
          triggerEvent: true,
        },
        yAxis: {
          type: "value",
          boundaryGap: [0, "100%"],
          min: (value) => {
            return Math.floor(value.min / 10) * 10;
          },
          max: (value) => {
            return Math.ceil(value.max / 10) * 10;
          },
          axisLabel: {
            textStyle: {
              color: "#ce7c6e",
            },
          },
          axisPointer: {
            show: false,
          },
          splitLine: {
            show: true,
            interval: "auto",
            lineStyle: {
              type: "dashed",
              width: 1,
              color: "#ce7c6e",
            },
          },
        },
        series: [
          {
            type: "line",
            itemStyle: {
              color: "#fb563a",
            },
            showSymbol: false,
            hoverAnimation: false,
            data: [],
            smooth: true,
            lineStyle: {
              width: 1,
              color: "#e9967a",
            },
            showSymbol: false,
            areaStyle: {
              color: {
                type: "linear",
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  {
                    offset: 0,
                    color: "#fd9786",
                  },
                  {
                    offset: 1,
                    color: "#fff",
                  },
                ],
              },
            },
          },
        ],
      },
      myChart: null,
      timer: null,
      data: [],
      temp: 59,
      now: "",
    };
  },
  mounted() {
    this.nowChart();
    this.getData();
    this.showFPS();
  },
  methods: {
    remove() {
      removeIndependPlugin("test");
    },
    showFPS() {
      this.fps = 60;
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
        this.fps = fps;

        lastFameTime = now;
        allFrameCount++;
        frame++;

        if (now > 1000 + lastTime) {
          var fps = Math.round((frame * 1000) / (now - lastTime));
          this.avgFps = fps;
          this.minFps = this.minFps > fps ? fps : this.minFps;
          this.maxFps = this.maxFps < fps ? fps : this.maxFps;
          frame = 0;
          lastTime = now;
        }
        rAF(loop);
      };
      loop();
    },
    //   初始化图表
    nowChart() {
      let that = this;
      this.now = +new Date();
      var value = this.avgFps;
      for (var i = 0; i < 60; i++) {
        this.now = new Date(+this.now + this.oneDay);
        this.data.push(this.randomData(this.now, 60));
      }
      this.myChart = echarts.init(document.getElementById("nowEcharts"));

      // 绘制图表
      let options = Object.assign(that.nowOptions, {});
      options.series.forEach((item) => {
        item.data = that.data;
        item.markPoint = {
          data: [
            [
              {
                symbol: "none",
                x: "95%",
              },
              {
                symbol: "circle",
                name: "实时数据",
                value: this.data[this.temp].value[1],
                xAxis: this.data[this.temp].value[0],
              },
            ],
          ],
        };
      });
      this.myChart.setOption(options);
    },
    // 获取接口返回数据 这里用定时器模拟一秒一个数据
    getData() {
      let that = this;
      var value = this.avgFps;
      this.timer = setInterval(() => {
        for (var i = 0; i < 1; i++) {
          that.data.shift();
          that.now = new Date(+that.now + that.oneDay);
          that.data.push(this.randomData(that.now, this.avgFps));
        }
        that.updateChart();
      }, 1000);
    },
    //更新图表
    updateChart() {
      let options = Object.assign(this.nowOptions, {});
      options.series.forEach((item) => {
        item.data = this.data;
        item.markPoint = {
          data: [
            [
              {
                symbol: "none",
                x: "95%",
              },
              {
                symbol: "circle",
                name: "实时数据",
                value: this.data[this.temp].value[1],
                xAxis: this.data[this.temp].value[0],
              },
            ],
          ],
        };
      });
      this.myChart.setOption(options);
    },
    // 产生随机数
    randomData(now, value) {
      var valueName =
        now.getFullYear() +
        "/" +
        (now.getMonth() + 1) +
        "/" +
        now.getDate() +
        " " +
        (now.getHours() >= 10 ? now.getHours() : "0" + now.getHours()) +
        ":" +
        (now.getMinutes() >= 10 ? now.getMinutes() : "0" + now.getMinutes()) +
        ":" +
        (now.getSeconds() >= 10 ? now.getSeconds() : "0" + now.getSeconds());
      return {
        name: now.toString(),
        value: [valueName, value],
      };
    },
  },
};
</script>
<style scoped>
.hello-independ {
  display: inline-block;
  width: 800px;
  height: 500px;
  padding: 10px;
  text-align: center;
  background-color: white;
  border-radius: 20px;
  box-shadow: 0 4px 6px #5d5d5d;
  overflow: hidden;
  border: 1px solid #ce7c6e;
  position: relative;
}
button {
  font-size: 15px;
  border-radius: 8px;
  border: 2px solid #ce7c6e;
  background-color: #ce7c6e;
  color: white;
  /* margin-top: 10px; */
  margin-bottom: 2px;
  width: 200px;
  height: 40px;
}
</style>
