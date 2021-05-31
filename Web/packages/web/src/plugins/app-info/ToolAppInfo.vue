<template>
  <div class="app-info-container">
    <div class="info-wrapper">
      <Card title="Page Info">
        <table>
          <tr>
            <td class="key">UA</td>
            <td class="value">{{ua}}</td>
          </tr>
          <tr>
            <td class="key">URL</td>
            <td class="value">{{url}}</td>
          </tr>
        </table>
      </Card>
    </div>
    <div class="info-wrapper" style="margin-top:20px">
      <Card title="Device Info">
        <table>
          <tr>
            <td class="key">设备缩放比</td>
            <td class="value">{{ratio}}</td>
          </tr>
          <tr>
            <td class="key">screen</td>
            <td>{{screen.width}} X {{screen.height}}</td>
          </tr>
          <tr>
            <td class="key">viewport</td>
            <td>{{viewport.width}} X {{viewport.height}}</td>
          </tr>
        </table>
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