<template>
  <div class="resource-item">
    <do-row>
      <do-col :span="22">
        <div
          class="resource-preview"
          v-html="resourcePreview"
          @click="toggleDetail"
        ></div>
      </do-col>
      <do-col :span="2">
        <div class="resource-toggle-icon">
          <span v-if="!showContent">▸</span>
          <span v-else>▾</span>
        </div>
      </do-col>
    </do-row>
    <div v-if="showContent">
      <div class="resource-detail">
        <div v-html="detailHtml"></div>
        <div class="resource-empty" v-if="!detailHtml">Loading ~</div>
      </div>
    </div>
  </div>
</template>
<script>
import {
  ResourceMap,
  imgLoad,
  url2blobPromise,
  trimLeft,
} from "./js/resources";

export default {
  components: {},
  props: {
    index: [Number],
    type: [Number],
    initiatorType: [String],
    entryName: [String],
    base64: [String],
  },
  data() {
    return {
      showContent: false,
      detailImgThumb: "",
      detailHtml: ""
    };
  },

  computed: {
    resourcePreview() {
      let index = this.index;
      let url = this.entryName;
      let html = `<div>${index + 1}.${url}</div>`;
      return html;
    },
  },
  methods: {
    toggleDetail() {
      this.showContent = !this.showContent;
      if (this.detailHtml) return;

      if (ResourceMap[this.type] === "img") {
        this.getDetailImgThumb((htmlSrc) => {
          this.detailHtml = htmlSrc;
        });
      } else {
        this.getDetailCode((htmlSrc) => {
          this.detailHtml =
            `<div style="max-height: 300px;overflow-y: scroll;overflow-x:hidden;word-break:break-all;text-align: left;">` +
            htmlSrc +
            `</div>`;
        });
      }
    },
    getDetailImgThumb(callback) {
      let url;
      if (this.base64 !== "") {
        url = this.base64;
      } else {
        url = this.entryName;
      }
      let htmlSrc = "";
      htmlSrc = `<img src = "${url}" style="object-fit: cover;height:100px" />`;
      this.getDetailImgSize((r) => {
        htmlSrc += r;
        callback(htmlSrc);
      });
    },
    getDetailImgSize(callback) {
      let url;
      if (this.base64 !== "") {
        url = this.base64;
      } else {
        url = this.entryName;
      }
      imgLoad(url, (w, h) => {
        callback(`<div>${w}*${h}</div>`);
      });
    },
    getDetailCode(callback) {
      url2blobPromise(this.entryName)
        .then((res) => {
          res = res.replace(/</g, "&lt;");
          res = res.replace(/>/g, "&gt;");
          let tmp = res;
          tmp = tmp.split("\n");
          let len = tmp.length;
          let r = ``;
          for (let i = 0; i < len; i = i + 1) {
            tmp[i] = trimLeft(tmp[i]);
            r += `
              <div class="codeline" style="display:flex;line-height:14px;">
                <div class="codeindex" style="min-width: 40px;background-color:#F0F0F0;color: #808080;text-align:right;padding-right:5px;">${i + 1}</div>
                <div class="codevalue">${tmp[i]}</div>
              </div>
            `;
          }
          callback(r);
        })
        .catch(() => {
          callback(`fail to load resource`);
        });
    },
  },
};
</script>
<style lang="less" scoped>
.resource-item {
  margin-top: 10px;
  border-radius: 5px;
  overflow: hidden;
  border: 1px solid #d6e4ef;
  font-size: 12px;
}
.resource-preview {
  word-break: break-all;
  color: #1485ee;
  padding: 5px;
}
.resource-toggle-icon {
  line-height: 24px;
}

.resource-detail {
  border-top: 1px solid #d6e4ef;
  text-align: center;
  .resource-empty {
    padding: 10px;
    font-size: 16px;
  }
}
</style>