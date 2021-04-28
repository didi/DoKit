<template>
  <div class="app-info-container">
    <div class="info-wrapper">
      <Card title="Page Info">
        <table border="1">
          <tr>
            <td>UA</td>
            <td>{{ua}}</td>
          </tr>
          <tr>
            <td>URL</td>
            <td>{{url}}</td>
          </tr>
        </table>
      </Card>
    </div>
    <div class="info-wrapper">
      <Card title="Device Info">
        <table border="1">
          <tr>
            <td>设备缩放比</td>
            <td>{{ratio}}</td>
          </tr>
          <tr>
            <td>screen</td>
            <td>{{screen.width}}X{{screen.height}}</td>
          </tr>
          <tr>
            <td>viewport</td>
            <td>{{viewport.width}}X{{viewport.height}}</td>
          </tr>
        </table>
      </Card>
    </div>
  </div>
</template>
<script>
import Card from '../../common/Card'

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
    margin: 20px 20px 0 20px;
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