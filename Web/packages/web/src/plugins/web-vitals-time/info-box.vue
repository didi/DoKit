<template>
  <div class="web-vitals-time">
    <div class="title">性能指标(web vitals)</div>
    <div class="desc">请在页面加载完成后再获取指标</div>
    <div class="content">
      <div><span class="item important">FCP：</span>{{FCP}}</div>
      <div><span class="item important">LCP：</span>{{LCP}}</div>
      <div><span class="item">CLS：</span>{{CLS}}</div>
      <div><span class="item">FID：</span>{{FID}}</div>
      <div><span class="item">TTFB：</span>{{TTFB}}</div>
    </div>
  </div>
</template>
<script>
import {getFCP, getLCP, getFID, getCLS, getTTFB} from 'web-vitals';
export default {
  data() {
    return {
      FCP: '',
      CLS: '',
      FID: '',
      LCP: '',
      TTFB: ''
    }
  },
  mounted() {
    getFCP(this.handleData);
    getLCP(this.handleData, true);
    getCLS(this.handleData, true);
    getFID(this.handleData);
    getTTFB(this.handleData);
  },
  methods: {
    handleData(detail) {
      console.log(JSON.parse(JSON.stringify(detail)))
      const { name, value } = detail
      this[name] = Math.round(value) + ' ms'
    }
  }
}
</script>
<style lang="less" scoped>
.web-vitals-time {
  padding: 10px;
  text-align: center;
  .title {
    font-weight: bold;
    font-size: 22px;
  }
  .desc {
    font-size: 12px;
    color: #999999;
  }
  .content {
    text-align: left;
    margin-top: 20px;
    padding-left: 5%;
    >div {
      font-size: 16px;
    }
  }
  .item {
    display: inline-block;
    text-align: right;
    width: 60px;
    margin-top: 4px;
    font-size: 16px;
  }
  .important {
    color: red;
  }
}
</style>
