<template>
  <div class="dokit-element-details">
    <ElementBreadcrumb
      :parentTag="parentTag"
      :highlightElement="highlightElement"
    />
    <ElementAttributes :highlightnNode="highlightnNode" />
    <ElementStyles :styles="styles" />
    <ElementComputedStyle
      v-if="highlightElement"
      :highlightElement="highlightElement"
      :highlightnNode="highlightnNode"
      :contentWidth="contentWidth"
      :contentHeight="contentHeight"
    />
  </div>
</template>

<script>
import CssStore from '../../assets/CssStore';
import ElementComputedStyle from './components/ElementComputedStyle';
import ElementStyles from './components/ElementStyles';
import ElementAttributes from './components/ElementAttributes';
import ElementBreadcrumb from './components/ElementBreadcrumb';
export default {
  components: {
    ElementComputedStyle,
    ElementStyles,
    ElementAttributes,
    ElementBreadcrumb,
  },
  data() {
    return {
      parentTag: [],
      curCssStore: null,
      styles: [],
      computedStyle: null,
    };
  },
  computed: {
    state() {
      return this.$store.state;
    },
    highlightElement() {
      return this.state.highlightElement;
    },
    highlightnNode() {
      return this.highlightElement?.__dokitForWeb_node;
    },
    contentWidth() {
      return this.computedStyle.width === "auto"
        ? "auto"
        : this.highlightnNode.boxStyle.contentWidth;
    },
    contentHeight() {
      return this.computedStyle.height === "auto"
        ? "auto"
        : this.highlightnNode.boxStyle.contentHeight;
    },
  },
  watch: {
    highlightElement(val) {
      // 元素改变时获取新的样式对象和父元素
      if (val) {
        this.curCssStore = new CssStore(val);
        this.getStyle();
        this.parentTag = this.getParentTag(val).reverse();
        this.computedStyle = this.curCssStore.getComputedStyle();
      }
    },
  },
  methods: {
    getParentTag(startTag, parentTagList = []) {
      // 递归查找父元素
      let self = this;
      if (startTag?.parentElement) {
        // 放入集合
        parentTagList.push(startTag?.parentElement);
        // 再上一层寻找
        return self.getParentTag(startTag?.parentElement, parentTagList);
      } else {
        return parentTagList;
      }
    },
    getStyle() {
      // 获取元素样式对象
      const styles = this.curCssStore.getMatchedCSSRules();
      styles.unshift(this.getInlineStyle(this.highlightElement?.style));
      styles.forEach((style) => this.processStyleRules(style?.style));
      this.styles = styles;
    },
    getInlineStyle(style) {
      // 获取行内样式
      const ret = {
        selectorText: "element.style",
        style: {},
      };
      for (let i = 0, len = style?.length; i < len; i++) {
        const s = style[i];
        if (style[s] === "initial") continue;
        ret.style[s] = style[s];
      }
      return ret;
    },
    processStyleRules(style) {
      // 把颜色和url替换成标签 有助于查看
      Object.keys(style).forEach((key) => {
        style[key] = this.processStyleRule(style[key]);
      });
    },
    processStyleRule(val) {
      const regColor = /rgba?\((.*?)\)/g;
      const regCssUrl = /url\("?(.*?)"?\)/g;
      val = val + "";
      return val
        .replace(
          regColor,
          '<span class="dokit-style-color" style="background-color: $&"></span>$&'
        )
        .replace(regCssUrl, (match, url) => `url("${this.wrapLink(url)}")`);
    },
    wrapLink(link) {
      return `<a href="${link}" target="_blank">${link}</a>`;
    },
  },
};
</script>

<style lang="less" scoped>
.dokit-element-details {
  overflow-y: auto;
  height: calc(100% - 76px);
  &:deep(.dokit-section) {
    border-bottom: 1px solid #ccc;
    color: #333;
    margin-bottom: 10px;
    &:deep(h2) {
      background: #f3f3f3;
      border-top: 1px solid #ccc;
      padding: 10px;
      font-size: 18px;
      margin: 0;
    }
  }
}
</style>