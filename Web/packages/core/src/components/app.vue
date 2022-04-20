<template>
  <div class="dokit-app">
    <div
      class="dokit-entry-btn"
      style="z-index: 10000"
      v-dragable="btnConfig"
      @click="toggleShowContainer"
    ></div>
    <div class="dokit-mask" v-show="showContainer" @click="toggleContainer"></div>
    <router-container v-show="showContainer"></router-container>
    <independ-container v-show="independPlugins.length"></independ-container>
    <elements-highlight
      v-if="showHighlightElement && highlightElement"
      :element="highlightElement"
    ></elements-highlight>
    <host-suspendedBall v-if="socketConnect"></host-suspendedBall>
  </div>
</template>

<script>
import { dragable } from "@dokit/web-utils";
import { uuid } from '../common/js/util.js'
import RouterContainer from "./router-container";
import IndependContainer from "./independ-container";
import ElementsHighlight from "./elements-highlight.vue";
import HostSuspendedBall from "./hostSuspendedBall.vue";
import { toggleContainer } from "@store/index";
import EventRecorder from "../common/js/EventRecorder";
import EventPlayback from "../common/js/EventPlayback";
export default {
  components: {
    RouterContainer,
    IndependContainer,
    ElementsHighlight,
    HostSuspendedBall,
  },
  directives: {
    dragable,
  },
  data() {
    return {
      btnConfig: {
        name: "dokit_entry",
        opacity: 0.5,
        left: window.innerWidth - 50,
        top: window.innerHeight - 100,
        safeBottom: 50,
        eventPlayback: null,
      },
    };
  },
  created() {
    this.$store.state.aid = uuid()
  },
  watch: {
    socketConnect: {
      handler(newVal, oldVal) {
        if (newVal) {
          this.eventPlayback = new EventPlayback(this.socketUrl);
          this.$store.state.socketHistoryList.set(this.socketUrl, "connect");
        } else {
          if (this.eventPlayback) {
            this.$store.state.socketHistoryList.set(this.socketUrl, "close");
            this.eventPlayback.close();
            this.eventPlayback = null;
          }
        }
      },
      immediate: true,
    },
    socketHistoryList: {
      handler(newVal, oldVal) {
        localStorage.setItem(
            "dokit-socket-history-list",
            JSON.stringify([...newVal])
          );
      },
      deep: true,
    },
    isHost: {
      handler(newVal, oldVal) {
        if (newVal) {
          this.eventPlayback?.state?.mySocket?.webSocketState&&this.eventPlayback.state.mySocket.send({
            type: "BROADCAST",
            contentType:'mc_host',
            channelSerial: this.channelSerial,
            data: JSON.stringify({
              connectSerial: this.eventPlayback.state.connectSerial,
            }),
          });
          if (!window.eventRecorder) {
            window.eventRecorder = new EventRecorder(this.socketUrl);
          }
          this.$store.state.startPlayback = false;
          window?.eventRecorder?.boot();
        } else {
          window?.eventRecorder?.off();
        }
      },
      immediate: true,
    },
  },
  computed: {
    channelSerial(){
      return this.state.channelSerial;
    },
    socketHistoryList(){
      return this.state.socketHistoryList;
    },
    highlightElement() {
      return this.state.highlightElement;
    },
    showHighlightElement() {
      return this.state.showHighlightElement;
    },
    state() {
      return this.$store.state;
    },
    showContainer() {
      return this.state.showContainer;
    },
    independPlugins() {
      return this.$store.state.independPlugins;
    },
    socketConnect() {
      return this.state.socketConnect;
    },
    socketUrl() {
      return this.state.socketUrl;
    },
    isHost() {
      return this.state.isHost;
    },
  },
  methods: {
    toggleShowContainer() {
      toggleContainer();
    },
  },
};
</script>

<style lang="less" scoped>
.dokit-app {
  font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
  pointer-events: none;
  position: fixed;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: 100000;
  & > * {
    pointer-events: all;
    font-size: 16px;
  }
}
.dokit-entry-btn {
  width: 50px;
  height: 50px;
  padding: 10px;
  box-sizing: border-box;
  background-image: url(//pt-starimg.didistatic.com/static/starimg/img/OzaetKDzHr1618905183992.png);
  background-size: 50px;
  background-position: center;
  background-repeat: no-repeat;
}
.dokit-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 3;
  background-color: #333333;
  opacity: 0.3;
}
</style>