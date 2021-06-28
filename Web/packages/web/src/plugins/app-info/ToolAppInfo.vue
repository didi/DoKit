<template>
  <div class="app-info-container">
    <div class="info-wrapper">
      <Card title="Page Info">
        <template v-slot:body>
          <DoRow class="app-info-item">
            <DoCol :span=8>UA</DoCol>
            <DoCol :span=16>{{ua}}</DoCol>
          </DoRow>
          <DoRow class="app-info-item">
            <DoCol :span=8>URL</DoCol>
            <DoCol :span=16>{{url}}</DoCol>
          </DoRow>
        </template>
        
      </Card>
    </div>
    <div class="info-wrapper" style="margin-top:20px">
      <Card title="Device Info">
        <template v-slot:body>
          <DoRow class="app-info-item">
            <DoCol :span=8>设备缩放比</DoCol>
            <DoCol :span=16>{{ratio}}</DoCol>
          </DoRow>
          <DoRow class="app-info-item">
            <DoCol :span=8>screen</DoCol>
            <DoCol :span=16>{{screen.width}} X {{screen.height}}</DoCol>
          </DoRow>
          <DoRow class="app-info-item">
            <DoCol :span=8>viewport</DoCol>
            <DoCol :span=16>{{viewport.width}} X {{viewport.height}}</DoCol>
          </DoRow>
        </template>
      </Card>
    </div>
  </div>
</template>
<script>
import Card from '@common/Card'

export default {
  components: {
    Card
  },
  data() {
    return {
      ua: window.navigator.userAgent,
      url: window.location.href,
      ratio: window.devicePixelRatio,
      screen: window.screen,
      viewport: {
        width: document.documentElement.clientWidth,
        height: document.documentElement.clientHeight
      }
    }
  },
}
</script>
<style lang="less" scoped>
.app-info-container{
  font-size: 14px;
  height: 100%;
  overflow: hidden;
}
.info-wrapper{
    margin: 5px 5px 0 5px;
    .key{
      font-weight: bold;
    }
    .app-info-item{
      padding: 5px 0;
    }
  }
  table{
    border-color: #eee;
    width: 100%;
    border-collapse: collapse;
    border-spacing: 0;
  }
  tr{
    width: 100%;
  }
  td,th{
    padding: 5px;
  }
</style>