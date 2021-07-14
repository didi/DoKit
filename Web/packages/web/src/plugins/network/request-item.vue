<template>
  <div class="request-item" :class="requestInfo.resType === 'error' && 'request-item-error'">
    <do-row class="request-info" @click="showContent = !showContent">
        <do-col :span="12">
          <div class="request-url">{{ requestInfo.path }}</div>
        </do-col>
        <do-col :span="3">
          <div class="request-type">{{requestInfo.type}}</div>
        </do-col>
        <do-col :span="4">
          <div class="request-method">{{ requestInfo.method }}</div>
        </do-col>
        <do-col :span="3">
          <div class="request-status">{{ requestInfo.status }}</div>
        </do-col>
        <do-col :span="2">
          <div class="request-toggle-icon">
            <span v-if="!showContent">▸</span>
            <span v-else>▾</span>
          </div>
        </do-col>
    </do-row>
    <div class="response-info" v-show="showContent">
      <div class="response-info-item">
        <div class="response-info-item_title">origin url</div>
        <div class="response-info-item_content">
          <span class="response-info-item_content-key">{{requestInfo.url}}</span>
        </div>
      </div>
      <div class="response-info-item" v-if="requestInfo.queryMap">
        <div class="response-info-item_title">query params</div>
        <div class="response-info-item_content" v-for="(value, key) in requestInfo.queryMap" :key="key">
          <span class="response-info-item_content-key">{{key}}</span>
          <span class="response-info-item_content-value">{{value}}</span>
        </div>
      </div>
      <div class="response-info-item" v-if="requestInfo.body">
        <div class="response-info-item_title">body params</div>
        <div class="response-info-item_content">
          <span class="response-info-item_content-value">{{requestInfo.body}}</span>
        </div>
      </div>
      <div class="response-info-item" v-if="requestInfo.headers && requestInfo.headers.contentType">
        <div class="response-info-item_title">headers</div>
        <div class="response-info-item_content">
          <span class="response-info-item_content-key">content-type</span>
          <span class="response-info-item_content-value">{{requestInfo.headers&&requestInfo.headers.contentType}}</span>
        </div>
      </div>
      <div class="response-info-item" v-if="requestInfo.resRaw">
        <div class="response-info-item_title">response</div>
        <pre>{{requestInfo.resRaw}}</pre>
      </div>
    </div>
  </div>
</template>
<script>
import { getPartUrlByParam, getQueryMap } from "@dokit/web-utils";
export default {
  props: {
    requestItem: [Object],
    index: [Number]
  },
  computed: {
    requestInfo() {
      let { requestInfo: {url, method, contentType, body}, responseInfo: { status, resRaw, type: resType } = {}, type } = this.requestItem
      
      let queryString = getPartUrlByParam(url, 'query')
      let queryMap = queryString ? getQueryMap(queryString) : null
      try {
        resRaw = JSON.stringify(JSON.parse(resRaw), null, 2)
      } catch (error) {}
      return {
        path: getPartUrlByParam(url, 'path'),
        url,
        method,
        status,
        type,
        resRaw,
        headers: {
          contentType: contentType
        },
        queryMap,
        body,
        resType
      }

    }
  },
  data() {
    return {
      showContent: false
    }
  }
}
</script>
<style lang="less" scoped>
.request-item-error{
  background-color:#FEF0F0;
  border: 1px solid #F8D8D7 !important; 
}
.request-item{
  margin-top: 10px;
  border-radius: 5px;
  overflow: hidden;
  border: 1px solid #d6e4ef;
  font-size: 12px;
  padding: 5px;
  .request-info{
    display: flex;
    line-height: 24px;
    text-align: center;
    .request-url{
      text-align: left;
      word-wrap: break-word;
      word-break:normal;
      color: #1485ee;
    }
  }
  .response-info{
    border-top: 1px solid #d6e4ef;
    .response-info-item{
      margin-top: 5px;
      .response-info-item_title{
        font-size: 16px;
      }
      .response-info-item_content{
        .response-info-item_content-key{
          color: #1485ee;
          margin-right: 10px;
          word-break: break-all;
          white-space: normal;
        }
      }
    }
    pre{
      margin: 0;
      margin-top: 5px;
      min-height: 100px;
      max-height: 300px;
      overflow-y: scroll;
      border: 1px solid #aaa;
      border-radius: 5px;
      white-space: pre-wrap;
      word-break: break-word;
    }
  }
}

</style>