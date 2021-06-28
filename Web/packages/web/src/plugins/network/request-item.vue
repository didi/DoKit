<template>
  <div class="request-item">
    <div class="request-info" @click="showContent = !showContent">
      <div class="request-url">{{ requestItem.requestInfo.url }}</div>
      <div class="request-type">{{requestItem.type}}</div>
      <div class="request-method">{{ requestItem.requestInfo.method }}</div>
      <div class="request-status">{{ requestItem.responseInfo && requestItem.responseInfo.status }}</div>
    </div>
    <div class="response-info" v-show="showContent">
      <div class="response-info-item">
        <div>headers</div>
        <div>content-type: {{requestItem.responseInfo&&requestItem.responseInfo.contentType}}</div>
      </div>
      <div class="response-info-item">
        <div>response</div>
        <pre>{{requestItem.responseInfo&&JSON.parse(requestItem.responseInfo.resRaw)}}</pre>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    requestItem: [Object]
  },
  data() {
    return {
      showContent: false
    }
  }
}
</script>
<style lang="less" scoped>
.request-item{
  font-size: 12px;
  border-bottom: 1px solid #eee;
  .request-info{
    display: flex;
    line-height: 24px;
    .request-url{
      flex: 5;
      overflow-x: scroll;
      white-space: nowrap;
    }
    .request-type{
      flex: 1;
      text-align: center;
      border-left: 1px solid #eee;
    }
    .request-method{
      flex: 1;
      text-align: center;
      border-left: 1px solid #eee;
    }
    .request-status{
      flex: 1;
      text-align: center;
            border-left: 1px solid #eee;

    }
  }
  .response-info{
    background-color: #f1f2f3;
    pre{
      margin: 0;
      overflow: auto;
      white-space: pre-wrap;
      word-break: break-word;
    }
  }
}

</style>