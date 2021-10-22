
<template>
  <div class="h5-portal">
    <div class="portal-textarea-container">
      <textarea
        class="portal-textarea"
        rows="5"
        v-model="url"
        placeholder="请输入目标传送地址"
      ></textarea>
      <div class="portal-opt-area">
        <div class="opt-btn" @click="jumpToTarget">跳转</div>
      </div>
    </div>
    <div class="url-edit-container">
      <div class="url-edit-container-title">快捷编辑</div>
      <DoRow>
        <DoCol :span=6 class="url-edit-key">baseUrl</DoCol>
        <DoCol :span=18>
          <textarea
            rows="3"
            v-model="editUrlInfo.baseUrl"
            placeholder="eg. https://dokit.didi.cn"
          ></textarea>
        </DoCol>
      </DoRow>
      <DoRow class="url-edit-container-query-item" v-for="(query, index) in editUrlInfo.queryList" :key="index">
        <DoCol :span=6 class="url-edit-key">query参数{{index+1}}</DoCol>
        <DoCol :span=7>
          <input type="text" v-model="editUrlInfo.queryList[index].key"/>
        </DoCol>
        <DoCol :span=2 style="text-align:center;">=</DoCol>
        <DoCol :span=7>
          <input type="text" v-model="editUrlInfo.queryList[index].value"/>
        </DoCol>
        <DoCol class="url-edit-container-query-item__icon" :span="2" @click="delQuery(index)">
          <img src="data:image/png;base64,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" alt="" srcset="">
        </DoCol>
      </DoRow>
      <DoRow>
        <DoCol :span=16 :offset=6>
          <div class="add-btn" @click="addQuery">新增query参数</div>
        </DoCol>
      </DoRow>
      <DoRow>
        <DoCol :span=16 :offset=6>
          <div class="add-btn" @click="handleQuickEdit">跳转</div>
        </DoCol>
      </DoRow>
    </div>
    <div class="history-record-container">
      <div class="history-record-title">历史记录</div>
      <div class="history-record-list">
        <div class="history-record-list-item" v-for="(url, index) in historyList" :key="index">
          <DoRow>
            <DoCol class="history-record-list-item__url" :span="18">{{url}}</DoCol>
            <DoCol class="history-record-list-item__icon" :span="3" @click="jumpToUrl(url)">
              <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAMgAAADICAMAAACahl6sAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAutQTFRFAAAA////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////4uz3xdrup8fmrMrnzN7w6fH55e73bqLVM3zEhbHc8vf7x9vvXJbQydzvutPrtdDq/v//OH/FfKvZpMTlm7/iqMfmkrnfiLPdibPdYprSgK3aPILGd6jY+vz+WJTP8fb7i7XeZZzT6PD4t9Hq3+r24Ov2U5HN1uXzfavZSovLzd/xRojJQYXIxNnuqcjmOH/Gu9Prss7piLLdoMLkkLjfl7zhmL3hjrbeocLkc6XX+Pv9vNTsNH3Eap/U7/X6xtruPYLHYZrR5u/40OHxRYjJ3en12ef0TY3MT47M1OPz4+33VZLORonKy97w7fT6XpjQPYPHwtjt9/r9aJ7TudLr7vT60+PycqTWsMzo0uLyk7ngwdftdafXnsHjZZzSy93wa6DUUpDN7PP67fP6jbbeUI/MyNzvc6XWpcXl9Pj8lbvgNn7Fg6/bYJnR2uf0eqrZ/P3+ncDjTo3Mv9btcaTW4ez2gq/blLrgpMXlZ57TWpXP8/f7irTdwNbtTYzLzuDxb6LVkbnfXZfQ4u33tM/pVJHO6vL5d6fYTY3Lmb7hsc3o2+j1TIzLbaHVmb3hOoDGWZXPpsbl0eHyQ4bJdabXQIXIoMLjO4HG3ur1fqzafazZ3On1vdXshbDcutLrs87pw9juZJvS9vn85e74hrHcdKbX1OPyn8HjSYrKlrvgjrfebKDUUY/NOIDG9fj8Y5vSSIrKuNHqQITI1OTzk7rgW5XPh7Lc5O73eKjYi7XdcKPWkLffV5PPOoHGwtjun8LjS4vLr8zoNX3Er8voPILHRIfJUZDNz+Dx1+XzW5bQe6rZosPkqcfm3Oj11eTz3ur2+/3+4Vk3TwAAACd0Uk5TABiAmLDI4fngfxdHnyrKySHUxBO0BZdV+Isc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" alt="" srcset="">
            </DoCol>
            <DoCol class="history-record-list-item__icon" :span="3" @click="delHistory(index)">
              <img src="data:image/png;base64,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" alt="" srcset="">
            </DoCol>
          </DoRow>
        </div>
      </div>
    </div>
  </div>
</template>


<script>
let URL_REG = new RegExp(`(https?(://))?[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]`)

export default {
  data() {
    return {
      url: '',
      historyList: [],
      editUrlInfo: {
        baseUrl: '',
        queryList: [{
          key: '',
          value: ''
        }]
      }
    };
  },
  created() {
    this.historyList = JSON.parse(localStorage.getItem("dokit-history-list") || "[]")
  },
  methods: {
    jumpToTarget() {
      if (!URL_REG.test(this.url)) {
        window.alert('输入的地址不符合URL规则')
        return
      }

      let targetUrl = this.url.startsWith('http') ? this.url : `${location.protocol}//${this.url}`
      if (this.historyList.indexOf(targetUrl) === -1) {
        this.addHistory(targetUrl)
      }

      window.location.href = targetUrl
      this.url = ''
    },
    addQuery() {
      this.editUrlInfo.queryList.push({
        key: '',
        value: ''
      })
    },
    delQuery(index) {
      this.editUrlInfo.queryList.splice(index, 1)
    },
    handleQuickEdit() {
      let targetUrl = this.editUrlInfo.baseUrl.startsWith('http') ?this.editUrlInfo.baseUrl : `${location.protocol}//${this.editUrlInfo.baseUrl}`
      targetUrl = targetUrl.indexOf('?')>-1 ? targetUrl : targetUrl+ '?'
      this.editUrlInfo.queryList.forEach((query, index) => {
        if(query) {
          if (index === 0 && targetUrl.endsWith('?')) {
            targetUrl += `${query.key}=${query.value}`
          } else {
            targetUrl += `&${query.key}=${query.value}`
          }
        }
      });
      if (this.historyList.indexOf(targetUrl) === -1) {
        this.addHistory(targetUrl)
      }

      window.location.href = targetUrl
    },
    jumpToUrl(url) {
      window.location.href = url
    },
    addHistory(url) {
      this.historyList.push(url)
      this.updateStorage()
    },
    delHistory(index) {
      this.historyList.splice(index, 1)
      this.updateStorage()
    },
    clearHistory() {
      this.historyList = []
      this.updateStorage()
    },
    updateStorage() {
      localStorage.setItem("dokit-history-list", window.JSON.stringify(this.historyList))
    }
  },
};
</script>

<style lang="less" scoped>
.h5-portal {
  padding: 5px;
  textarea,input {
    font-size: 13px;
  }
  .portal-textarea-container{
    .portal-textarea{
      font-size: 13px;
      border-radius: 5px;
      box-sizing: border-box;
      width: 100%;
      border: 1px solid #d6e4ef;
      resize: vertical;
    }
    .portal-opt-area{
      margin-top: 5px;
      height: 32px;
      line-height: 32px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      .opt-btn{
        background-color: #337CC4;
        border-radius: 5px;
        font-size: 16px;
        width: 100%;
        text-align: center;
        color: #fff;
      }
    }
  }
  .url-edit-container{
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #d6e4ef;
    .url-edit-key{
      font-size: 15px;
    }
    .url-edit-container-title{
      text-align: center;
      font-size: 18px;
      color:#2c405a;
      margin-bottom: 20px;
    }
    .url-edit-container-query-item{
      margin-top: 5px;
      height: 32px;
      line-height: 32px;
    }
    .url-edit-container-query-item__icon{
      height: 32px;
      line-height: 32px;
      text-align: center;
      img{
        width: 15px;
        height: 15px;
      }
      
    }
    .add-btn{
      margin-top: 5px;
      height: 32px;
      line-height: 32px;
      background-color: #337CC4;
      border-radius: 5px;
      font-size: 16px;
      text-align: center;
      color: #fff;
    }
    textarea{
      border-radius: 5px;
      box-sizing: border-box;
      width: 100%;
      border: 1px solid #d6e4ef;
      resize: vertical;
    }
    input{
      height: 32px;
      line-height: 32px;
      border-radius: 5px;
      box-sizing: border-box;
      width: 100%;
      border: 1px solid #d6e4ef;
    }
  }
  .history-record-container{
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #d6e4ef;
    .history-record-title{
      text-align: center;
      font-size: 18px;
      color:#2c405a;
      margin-bottom: 20px;
    }
    .history-record-list-item{
      margin-top: 5px;
      background-color: #337CC4;
      border-radius: 5px;
      height: 32px;
      line-height: 32px;
      color: #fff;
      padding: 0 5px;
      .history-record-list-item__url{
        overflow:hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
      .history-record-list-item__icon{
        img{
          width: 15px;
          height: 15px;
        }
        text-align: center;
      }
      
    }
  }
}
</style>