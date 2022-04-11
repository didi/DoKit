<template>
  <div>
    <div class="dokit-ruler-center-bg" :style="centerStyle"></div>
    <div class="dokit-ruler-center-round" :style="centerStyle"></div>
    <div class="dokit-ruler-center-dot" :style="centerStyle"></div>
    <div class="dokit-ruler-line dokit-ruler-horizon-line" :style="horizonLineStyle"></div>
    <div
      class="dokit-ruler-line dokit-ruler-vertical-line"
      :style="verticalLineStyle"
    ></div>
    <div class="dokit-ruler-info" :style="topInfoStyle">{{ position.top }}</div>
    <div class="dokit-ruler-info" :style="rightInfoStyle">
      {{ position.right }}
    </div>
    <div class="dokit-ruler-info" :style="bottomInfoStyle">
      {{ position.bottom }}
    </div>
    <div class="dokit-ruler-info" :style="leftInfoStyle">{{ position.left }}</div>

    <InfoBox :position="position" @remove="remove" />

    <div
      class="dokit-ruler-drag-mask"
      :style="centerStyle"
      @mousedown="drag"
      @touchstart="drag"
    ></div>
  </div>
</template>

<script>
import InfoBox from "./info-box";
import { removeIndependPlugin } from "@dokit/web-core";

export default {
  name: "align-ruler",
  components: {
    InfoBox,
  },
  data() {
    return {
      posX: 200,
      posY: 200,
      screenWidth: document.documentElement.clientWidth,
      screenHeight: document.documentElement.clientHeight,
    };
  },
  computed: {
    position() {
      return {
        top: this.posY,
        left: this.posX,
        right: this.screenWidth - this.posX,
        bottom: this.screenHeight - this.posY,
      };
    },

    centerStyle() {
      return {
        top: this.posY + "px",
        left: this.posX + "px",
      };
    },
    horizonLineStyle() {
      return {
        width: this.screenWidth + "px",
        top: this.posY + "px",
      };
    },
    verticalLineStyle() {
      return {
        height: this.screenHeight + "px",
        left: this.posX + "px",
      };
    },
    topInfoStyle() {
      return {
        top: this.posY / 2 + "px",
        left: this.posX + "px",
      };
    },
    rightInfoStyle() {
      return {
        top: this.posY + "px",
        left: (this.posX + this.screenWidth) / 2 + "px",
      };
    },
    bottomInfoStyle() {
      return {
        top: (this.screenHeight + this.posY) / 2 + "px",
        left: this.posX + "px",
      };
    },
    leftInfoStyle() {
      return {
        top: this.posY + "px",
        left: this.posX / 2 + "px",
      };
    },
  },

  // 监听浏览器缩放
  mounted() {
    window.addEventListener("resize", this.handleResize);
  },
  beforeUnmount() {
    window.removeEventListener("resize", this.handleResize);
  },

  methods: {
    remove() {
      removeIndependPlugin("align-ruler");
    },
    handleResize(event) {
      this.screenWidth = document.documentElement.clientWidth;
      this.screenHeight = document.documentElement.clientHeight;
      this.posX = this.screenWidth * 0.3;
      this.posY = this.screenHeight * 0.3;
    },
    drag(e) {
      e.preventDefault();
      e.stopPropagation();
      let el = e.target;

      // 算出鼠标相对元素的位置
      // 兼容触摸和鼠标
      let startX = e.clientX ? e.clientX : e.touches[0].clientX;
      let startY = e.clientY ? e.clientY : e.touches[0].clientY;
      let offsetX = startX - el.offsetLeft;
      let offsetY = startY - el.offsetTop;

      let update = (X, Y) => {
        let left = X - offsetX;
        let top = Y - offsetY;

        this.posX = Math.round(left);
        this.posY = Math.round(top);
      };

      let handleMousemove = (e) => {
        update(e.clientX, e.clientY);
      };

      let handleTouchmove = (e) => {
        update(e.touches[0].clientX, e.touches[0].clientY);
      };

      let removeDragHandle = (e) => {
        document.removeEventListener("mousemove", handleMousemove);
        document.removeEventListener("touchmove", handleTouchmove);
        document.removeEventListener("mouseup", removeDragHandle);
        document.removeEventListener("touchend", removeDragHandle);
      };

      document.addEventListener("mousemove", handleMousemove);
      document.addEventListener("touchmove", handleTouchmove);
      document.addEventListener("mouseup", removeDragHandle);
      document.addEventListener("touchend", removeDragHandle);
    },
  },
};
</script>


<style scoped>
.color {
  color: #ffffff;
  color: #cc3a4b;
  color: #337cc4;
  color: #cc3a4b30;
}

.dokit-ruler-center-bg {
  box-sizing: border-box;
  background-color: rgba(255, 255, 255, 0.392156863);
  position: fixed;
  width: 60px;
  height: 60px;
  transform: translate(-30px, -30px);
  border-radius: 30px;
  border: solid 1px rgba(51, 124, 196, 0.392156863);
}

.dokit-ruler-center-round {
  background-color: rgba(204, 58, 75, 0.196078431);
  position: fixed;
  width: 40px;
  height: 40px;
  transform: translate(-20px, -20px);
  border-radius: 20px;
}

.dokit-ruler-center-dot {
  background-color: #cc3a4b;
  position: fixed;
  width: 6px;
  height: 6px;
  transform: translate(-3px, -3px);
  border-radius: 3px;
}

.dokit-ruler-line {
  position: fixed;
  background-color: #cc3a4b;
}

.dokit-ruler-horizon-line {
  left: 0;
  height: 1px;
  transform: translate(0px, -0.5px);
}

.dokit-ruler-vertical-line {
  top: 0;
  width: 1px;
  transform: translate(-0.5px, 0px);
}

.dokit-ruler-info {
  position: fixed;
}

.dokit-ruler-drag-mask {
  position: fixed;
  width: 60px;
  height: 60px;
  transform: translate(-30px, -30px);
  border-radius: 30px;
}
</style>
