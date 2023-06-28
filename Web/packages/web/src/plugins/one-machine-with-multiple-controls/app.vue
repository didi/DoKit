<template>
  <div class="one-machine-with-multiple-controls-content">
    <div class="portal-textarea-container">
      <textarea
        class="portal-textarea"
        rows="5"
        v-model="socketUrl"
        placeholder="请输入联网地址"
      ></textarea>
      <div class="portal-opt-area">
        <div class="opt-btn" @click="scanCode">扫码</div>
      </div>
      <div class="portal-opt-area">
        <div class="opt-btn" @click="connectHandle()">
          {{ connect ? "断开联网" : "联网" }}
        </div>
      </div>
      <div class="portal-opt-area">
        <div class="opt-btn" @click="recordHandle">
          {{ recording ? "暂停录制" : "开始录制" }}
        </div>
      </div>
    </div>
    <div class="history-record-container">
      <div class="history-record-title">历史记录</div>
      <div class="history-record-list" v-if="historyList.length > 0">
        <div
          class="history-record-list-item"
          v-for="(item, index) in historyList"
          :key="index"
        >
          <svg
            t="1646903397439"
            class="connectState"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="7559"
            width="200"
            height="200"
          >
            <path
              d="M969.586403 624.769747l-129.946199-130.564991c-34.911813-35.071501-77.458713-52.607251-127.401169-52.607251-50.830721 0-94.146121 18.30425-129.946199 55.172242l-54.912749-55.172242c36.568577-36.089513 54.912749-79.74425 54.912749-131.203743 0-50.172008-17.32616-92.678986-51.848733-127.361247L401.885195 53.246004C367.362622 17.795244 324.815721 0 274.484025 0c-49.942456 0-92.229864 17.406004-126.762417 52.098246l-91.850604 91.521247c-35.291072 34.682261-52.996491 77.179259-52.996492 127.351267 0 50.172008 17.455906 92.928499 52.357739 128l129.946199 130.684757c34.901832 35.071501 77.458713 52.617232 127.40117 52.617232 50.830721 0 94.156101-18.30425 129.946198-55.172242l54.912749 55.172242c-36.688343 36.089513-54.912749 79.74425-54.912749 131.203742 0 50.172008 17.32616 92.669006 51.858714 127.361248l128.538947 129.916257c34.522573 35.460741 76.949708 53.246004 127.40117 53.246004 49.942456 0 92.239844-17.406004 126.762417-52.227992l91.850604-91.521248c35.291072-34.692242 52.996491-77.179259 52.996492-127.361247 0.019961-50.042261-17.435945-92.798752-52.347759-128.119766zM460.790146 365.556023c-1.626823-1.337388-5.90846-5.938402-13.453723-13.813021-7.395556-7.575205-12.555478-12.924756-15.519688-15.599532-2.95423-2.525068-7.545263-6.237817-13.753139-10.838831-6.357583-4.601014-12.41575-7.724912-18.473918-9.511423-6.058168-1.77653-12.715166-2.664795-19.961014-2.664795-19.212476 0-35.620429 6.676959-49.223859 20.350253-13.603431 13.513606-20.250448 30.001404-20.250449 49.453412 0 7.28577 1.027992 13.97271 2.664796 20.050838 1.626823 5.938402 4.870487 12.325926 9.45154 18.563743 4.581053 6.237817 8.283821 10.998519 10.788928 13.813021 2.515088 2.824483 7.68499 8.024327 15.529668 15.589552 7.68499 7.575205 12.276023 12.026511 13.743158 13.513606-14.481715 15.010682-31.917661 22.585887-52.17809 22.585887-19.801326 0-36.069552-6.387524-49.223859-19.601715L110.145 315.952904C96.541569 302.429318 89.894551 285.951501 89.894551 266.489513c0-19.012865 6.647018-35.201248 20.250449-48.864562l106.591813-106.3423c14.042573-13.074464 30.300819-19.611696 49.223859-19.611696 19.222456 0 35.630409 6.68694 49.22386 20.350254l149.168655 150.755555c13.59345 13.513606 20.250448 30.001404 20.250448 49.463392-0.009981 20.350253-7.844678 38.165458-23.813489 53.315867z m455.849669 443.334113L809.62882 915.511891c-13.503626 12.824951-29.981442 19.082729-49.423469 19.082729-19.89115 0-36.209279-6.417466-49.42347-19.68156L561.034356 763.708382c-13.663314-13.573489-20.330292-30.12117-20.330292-49.653021 0-20.430097 8.014347-38.325146 24.043041-53.385731 1.626823 1.347368 6.078129 6.118051 13.503626 13.872904 7.425497 7.754854 12.615361 13.124366 15.589551 15.649435 2.964211 2.535049 7.565224 6.257778 13.793061 10.888733 6.387524 4.620975 12.475634 7.904561 18.553762 9.541364 6.088109 1.936218 12.765068 2.684756 20.040858 2.684757 19.29232 0 35.770136-6.716881 49.423469-20.430098 13.653333-13.573489 20.330292-30.12117 20.330293-49.653021 0-7.305731-1.037973-14.012632-2.674776-20.130683-1.626823-5.958363-4.900429-12.375828-9.501442-18.643586-4.601014-6.257778-8.164055-10.888733-10.838831-13.862924-2.525068-2.844444-7.714932-8.054269-15.579571-15.659415-7.714932-7.605146-12.315945-12.076413-13.803041-13.563509 14.541598-15.509708 32.057388-23.414269 52.397661-23.414269 19.29232 0 35.760156 6.716881 49.413489 20.430097l151.384328 152.102924c13.653333 13.563509 20.330292 30.111189 20.330292 49.653022 0.009981 19.082729-6.816686 35.490682-20.470019 48.754775z"
              p-id="7560"
              :fill="connectStateColor(item[1])"
            ></path>
          </svg>
          <div class="name">{{ item[0] }}</div>
          <div
            class="opt-btn"
            :style="`background-color:${item[1] === 'connect' ? '#d81e06' : '#28af31'}`"
            @click="connectHandle(item[0])"
          >
            {{ item[1] === "connect" ? "断开连接" : "连接" }}
          </div>
          <img
            class="delBtn"
            @click="delHistory(item[0])"
            src="data:image/png;base64,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"
            alt=""
            srcset=""
          />
        </div>
      </div>
      <div class="history-record-list-empty" v-else>暂无</div>
    </div>
  </div>
</template>

<script>
import { $bus } from "../../assets/util";
export default {
  data() {
    return {
      recording: false,
      socketUrl: "",
      historyList: [],
      testSocket: null,
    };
  },
  watch: {
    socketHistoryList: {
      handler: function (newVal, oldVal) {
        this.historyList = newVal && [...newVal] || [];
      },
      deep: true,
      immediate: true,
    },
  },
  computed: {
    connect() {
      return this.$store.state.socketConnect;
    },
    isHost() {
      return this.$store.state.isHost;
    },
    socketHistoryList() {
      return this.$store.state.socketHistoryList;
    },
  },
  created() {
    let list = JSON.parse(localStorage.getItem("dokit-socket-history-list") || "[]");
    list.forEach((item) => {
      if (this.connect && item[0] === this.$store.state.socketUrl) {
        this.$store.state.socketHistoryList.set(item[0], "connect");
        this.socketUrl = this.$store.state.socketUrl;
      } else {
        this.$store.state.socketHistoryList.set(item[0], "close");
      }
    });
    $bus.on("scanCode", this.scanCodeCallback);
  },
  destroyed() {
    $bus.off("scanCode", this.scanCodeCallback);
  },
  methods: {
    recordHandle() {},
    connectHandle(url) {
      if (this.$store.state.socketConnect) {
        this.$store.state.socketConnect = false;
        return;
      }
      if (
        (url && !/^(ws?s:\/\/)/.test(url)) ||
        (this.socketUrl && !/^(ws?s:\/\/)/.test(this.socketUrl))
      ) {
        this.$toast("url地址格式不对", 1000);
        return;
      }
      try {
        this.testSocket = new WebSocket(url || this.socketUrl);
        this.testSocket.addEventListener("error", (e) => {
          this.$toast("url地址无法连接", 2000);
          this.testSocket.close();
          this.testSocket = null;
        });
        this.testSocket.addEventListener("open", (e) => {
          url && (this.socketUrl = url);
          this.$store.state.socketUrl = this.socketUrl;
          this.$nextTick(() => {
            this.$store.state.socketConnect = !this.$store.state.socketConnect;
            this.$store.state.socketConnect && (this.$store.state.showContainer = false);
          });
          this.testSocket.close();
          this.testSocket = null;
        });
      } catch (error) {
        this.$toast("url地址无法连接", 1000);
        return;
      }
    },
    scanCode() {
      this.$router.push({
        name: "scanCode",
        params: { multiControl: true },
      });
    },
    scanCodeCallback(e) {
      this.$router.back();
      this.socketUrl = e;
      this.connectHandle();
    },
    delHistory(key) {
      this.$store.state.socketHistoryList.delete(key);
    },
    connectStateColor(value) {
      if (value === "connect") {
        return "#1afa29";
      } else {
        return "#d81e06";
      }
    },
  },
};
</script>

<style lang="less" scoped>
.one-machine-with-multiple-controls-content {
  padding: 5px;
  textarea {
    font-size: 13px;
  }
  .portal-textarea-container {
    .portal-textarea {
      font-size: 13px;
      border-radius: 5px;
      box-sizing: border-box;
      width: 100%;
      border: 1px solid #d6e4ef;
      resize: vertical;
    }
    .portal-opt-area {
      margin-top: 5px;
      height: 32px;
      line-height: 32px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      .opt-btn {
        background-color: #337cc4;
        border-radius: 5px;
        font-size: 16px;
        width: 100%;
        text-align: center;
        color: #fff;
      }
    }
  }
  .history-record-container {
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #d6e4ef;
    .history-record-title {
      text-align: center;
      font-size: 18px;
      color: #2c405a;
      margin-bottom: 20px;
    }
    .history-record-list-item {
      margin-top: 5px;
      background-color: #337cc4;
      border-radius: 5px;
      color: #fff;
      padding: 5px;
      display: flex;
      align-items: center;
      .delBtn {
        width: 15px;
        height: 15px;
      }
      .name {
        word-break: break-all;
        margin-right: 5px;
        font-size: 15px;
      }
      .connectState {
        width: 20px;
        height: 20px;
        margin-right: 7px;
      }
      .opt-btn {
        border-radius: 5px;
        font-size: 16px;
        text-align: center;
        color: #fff;
        white-space: nowrap;
        padding: 2px 15px;
        margin-right: 5px;
      }
    }
    .history-record-list-empty {
      display: flex;
      align-items: center;
      justify-content: center;
      margin-top: 30px;
      font-size: 17px;
    }
  }
}
</style>
