<template>
  <div class="web-vitals-time">
    <div class="title">性能指标(web vitals)</div>
    <div class="sub-title">请在页面加载完成后再获取指标</div>
    <div class="content">
      <div><span class="item important">LCP：</span>{{LCP}}</div>
      <div><span class="item">LOADED：</span>{{loadedTime}}</div>
<!--      <div><span class="item">FCP：</span>{{FCP}}</div>-->
      <div><span class="item">CLS：</span>{{CLS}}</div>
      <div><span class="item">FID：</span>{{FID}}</div>
      <div><span class="item">TTFB：</span>{{TTFB}}</div>
    </div>

    <div class="desc">
      <div class="title">备注</div>
      <div class="container">
        <div>1、LOADED指页面完全加载时间（页面load的总耗时）；</div>
        <div>2、由于IOS系统暂不支持获取LCP指标相关接口，可用LOADED时间作为参考；</div>
      </div>
    </div>
  </div>
</template>
<script>
import {getFCP, getLCP, getFID, getCLS, getTTFB} from 'web-vitals';
export default {
  data() {
    return {
      loadedTime: '--',
      FCP: '--',
      CLS: '--',
      FID: '--',
      LCP: '--',
      TTFB: '--'
    }
  },
  mounted() {
    getFCP(this.handleData);
    getLCP(this.handleData, true);
    getCLS(this.handleData, true);
    getFID(this.handleData);
    getTTFB(this.handleData);

    // window.addEventListener('load', this.getTiming, false)
    this.getTiming()
  },
  methods: {
    handleData(detail) {
      console.log(JSON.parse(JSON.stringify(detail)))
      const { name, value } = detail
      this[name] = Math.round(value) + ' ms'
    },
    getTiming() {
      setTimeout(() => {
        let t = window.performance.timing;
        const loadedTime = t.loadEventEnd - t.navigationStart
        this.loadedTime = loadedTime + ' ms'
        let performanceInfo = [{
          key: "Redirect",
          desc: "网页重定向的耗时",
          "value(ms)": t.redirectEnd - t.redirectStart
          },
          {
            key: "AppCache",
            desc: "检查本地缓存的耗时",
            "value(ms)": t.domainLookupStart - t.fetchStart
          },
          {
            key: "DNS",
            desc: "DNS查询的耗时",
            "value(ms)": t.domainLookupEnd - t.domainLookupStart
          },
          {
            key: "TCP",
            desc: "TCP链接的耗时",
            "value(ms)": t.connectEnd - t.connectStart
          },
          {
            key: "Waiting(TTFB)",
            desc: "从客户端发起请求到接收响应的时间",
            "value(ms)": t.responseStart - t.requestStart
          }, {
            key: "Content Download",
            desc: "下载服务端返回数据的时间",
            "value(ms)": t.responseEnd - t.responseStart
          },
          {
            key: "HTTP Total Time",
            desc: "http请求总耗时",
            "value(ms)": t.responseEnd - t.requestStart
          },
          {
            key: "First Time",
            desc: "首包时间",
            "value(ms)": t.responseStart - t.domainLookupStart
          },
          {
            key: "White screen time",
            desc: "白屏时间",
            "value(ms)": t.responseEnd - t.fetchStart
          },
          {
            key: "Time to Interactive(TTI)",
            desc: "首次可交互时间",
            "value(ms)": t.domInteractive - t.fetchStart
          },
          {
            key: "DOM Parsing",
            desc: "DOM 解析耗时",
            "value(ms)": t.domInteractive - t.responseEnd
          },
          {
            key: "DOMContentLoaded",
            desc: "DOM 加载完成的时间",
            "value(ms)": t.domInteractive - t.navigationStart
          },
          {
            key: "Loaded",
            desc: "页面load的总耗时",
            "value(ms)": loadedTime
          }]

        console.table(performanceInfo);
      }, 0)
    }
  }
}
</script>
<style lang="less" scoped>
.web-vitals-time {
  padding: 10px;
  text-align: center;
  font-size: 16px;
  .title {
    font-weight: bold;
    font-size: 22px;
  }
  .sub-title {
    font-size: 12px;
    color: #999999;
  }
  .content {
    text-align: left;
    margin-top: 20px;
    padding-left: 5%;
    >div {
      font-size: 22px;
    }
  }
  .item {
    display: inline-block;
    text-align: right;
    width: 80px;
    margin-top: 4px;
    font-size: 16px;
  }
  .important {
    color: red;
  }

  .desc {
    text-align: left;
    margin-top: 40px;
    .title {
      text-align: center;
      font-size: 18px;
      font-weight: bold;
    }
    .container {
      margin-top: 4px;
      font-size: 14px;
      padding: 10px;
      background-color: rgba(133,122,122,0.12);
      border-radius: 6px;
    }
  }
}
</style>
