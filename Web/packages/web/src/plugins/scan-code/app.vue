<template>
  <div class="scan">
    <div class="nav">
      <a class="close" @click="() => $router.back()"></a>
      <p class="title">Scan QRcode</p>
    </div>
    <div class="scroll-container">
      <Scaner
        @code-scanned="codeScanned"
        @error-captured="errorCaptured"
        :stop-on-scanned="true"
        :draw-on-found="true"
        :responsive="false"
      />
    </div>
  </div>
</template>

<script>
import Scaner from "../../components/ScanerComponent.vue";
import { $bus } from "../../assets/util";
export default {
  name: "Scan",
  components: {
    Scaner,
  },
  data() {
    return {
      errorMessage: "",
      scanned: "",
    };
  },
  methods: {
    codeScanned(code) {
      this.scanned = code;
      $bus.emit("scanCode", code);
      setTimeout(() => {
        alert(`扫码解析成功: ${code}`);
      }, 200);
    },
    errorCaptured(error) {
      switch (error.name) {
        case "NotAllowedError":
          this.errorMessage = "Camera permission denied.";
          break;
        case "NotFoundError":
          this.errorMessage = "There is no connected camera.";
          break;
        case "NotSupportedError":
          this.errorMessage =
            "Seems like this page is served in non-secure context.";
          break;
        case "NotReadableError":
          this.errorMessage =
            "Couldn't access your camera. Is it already in use?";
          break;
        case "OverconstrainedError":
          this.errorMessage = "Constraints don't match any installed camera.";
          break;
        default:
          this.errorMessage = "UNKNOWN ERROR: " + error.message;
      }
      console.error(this.errorMessage);
      alert("相机调用失败");
    },
  },
  mounted() {
    var str = navigator.userAgent.toLowerCase();
    var ver = str.match(/cpu iphone os (.*?) like mac os/);
    if (ver && ver[1].replace(/_/g, ".") < "10.3.3") {
      alert("相机调用失败");
    }
  },
};
</script>

<style lang="less" scoped>
.scan {
  height: 100%;
  width: 100%;
  .nav {
    width: 100%;
    height: 48px;
    line-height: 48px;
    position: fixed;
    top: 0;
    left: 0;
    background-color: #ffffff;
    .title {
      padding: 0;
      margin: 0;
      font-size: 16px;
      color: #000000;
      text-align: center;
    }
    .close {
      display: inline-block;
      height: 22px;
      width: 22px;
      background: url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACQAAAAzCAMAAADIDVqJAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAXFQTFRFAAAANX7GNHzFM3zFM3zFNn3ENYDHNHzFM3zENIDHNH7INH3ENX3GNn7GM33ENYDFNYDGM4DINH7HM3zENH3HNHzENYDGM37FM33FNH3FNn3FNX7HN4DINHzEM33HNH7FNIDGNn7GM33ENX3FNXzHNoDENH7FNH3EM3zEN33INHzFNH3FM4DHNX3HNH3GNX7ENHzENHzFNYDFNH3FM33ENn3JM33GN4DINX7EM3zEM33GNH7FM33FM33FNIDFNH3GNX3KM37HNHzFN4DINX7FNH3EOHzHNH3GM3zFM4DGM33EM3zENHzENH3EM33FOoTFgID/NHzFOIDHM3zEM3zFNoPJM33FM33ENH3FNHzENYDL////NHzENH3FM4PFM3zFOYDGNH3FM3zFN3zINHzEM33FNoDJM3zFM33FNH3ENHzFM37FNH3ENH3FM33ENH7GNX7FM33ENH3FNH7FM37ENH7FM3zFNH3EM4jMM3zE////TdHL6gAAAHl0Uk5TAEPy+vA9RPb0QEX3P0fzPkg8Sfg7SjpL+fE5TThON082Ue81UjRT++4zVO0yVjFX/OwwWOsvWi5b6i1d/eksXitf6Cph5yli/ihk5ieh3x8CoiCg4CGf4Z3iIgGc4yOaJJnkJZjlJpaVk5KQj42LioiHhYSCgHOyD3AmCqsAAAABYktHRFt0vJU0AAAACXBIWXMAAABIAAAASABGyWs+AAABPUlEQVQ4y43UxVoCUBQE4Ctgd3cXKAYoKqioINiBrdjdXby9C+6c3ZzPWf+rE2MMS4bDmY4rk5qs7FQ6ObnU5OXDFFBTCFNUTE2JmFJqysqtcVRQUymmiprqGmtq66ipF9NATSOMq4maZidMCzWtMG3t1HSI6aSmq9sat4eaHpheLzV9YvqpGRi0xuenZghmOEDNiJhRasZggiFqxiesmQxTMyVmmpoZmEiUmlmYWJyauXmYBWoWYZaWqTEr1qRW1zhaT4jaUNQm1Na2onagdjW1B7V/wFXyEOroWFEnok4VdQZ1fqGoS6ira0XdpP4x+uStKL5EY+6gYpq6h4rEFfUgKqqoRyjlkI15ggqGFfUsKqSoFyjlTY15hfIFFPUmyq+odyilhIz5gHJ7FfUpyqOoLyilYo35/rFJ/Jo/DZ3bT7fEcIgAAAAldEVYdGRhdGU6Y3JlYXRlADIwMjEtMDQtMjFUMTc6MzI6MjgrMDg6MDBBnT5hAAAAJXRFWHRkYXRlOm1vZGlmeQAyMDIxLTA0LTIxVDE3OjMyOjI4KzA4OjAwMMCG3QAAAABJRU5ErkJggg==")
        no-repeat center;
      background-size: auto 100%;
      position: absolute;
      left: 16px;
      top: 14px;
    }
  }
}
</style>