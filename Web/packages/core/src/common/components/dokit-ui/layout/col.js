/* eslint-disable */
import { defineComponent, h, computed, inject } from 'vue';
export default defineComponent({
  name: 'DoCol',
  componentName: 'DoCol',
  props: {
    /**
     * 网格跨度的列数
     * 默认值：24
     */
    span: {
      type: Number,
      default: 24
    },
    /**
     * 自定义元素标签
     * 默认值：div
     */
    tag: {
      type: String,
      default: 'div'
    },
    /**
     * 网格左侧的间距数
     * 默认值：0
     */
    offset: Number,
    /**
     * 网格向左移动的列数
     * 默认值：0
     */
    pull: Number,
    /**
     * 网格向右移动的列数
     * 默认值：0
     */
    push: Number,
    /**
     * <768px 响应列 | {span, offset}对象
     */
    // @ts-ignore
    xs: [Number, Object],
    /**
     * ≥768px 响应列 | {span, offset}对象
     */
    // @ts-ignore
    sm: [Number, Object],
    /**
     * ≥992px 响应列 | {span, offset}对象
     */
    // @ts-ignore
    md: [Number, Object],
    /**
     * ≥1200px 响应列 | {span, offset}对象
     */
    // @ts-ignore
    lg: [Number, Object],
    /**
     * ≥1920px 响应列 | {span, offset}对象
     */
    // @ts-ignore
    xl: [Number, Object]
  },

  setup(props, { slots }) {
    let gutter = computed(() => {
      return inject('gutter', 0);
    });

    let classList = [];
    let style = {
      paddingLeft: '',
      paddingRight: ''
    };
    if (gutter) {
      style.paddingLeft = gutter.value / 2 + 'px';
      style.paddingRight = style.paddingLeft;
    }
    ['span', 'offset', 'pull', 'push'].forEach(prop => {
      if (props[prop] || props[prop] === 0) {
        classList.push(prop !== 'span' ? `do-col-${prop}-${props[prop]}` : `do-col-${props[prop]}`);
      }
    });
    ['xs', 'sm', 'md', 'lg', 'xl'].forEach(size => {
      if (typeof props[size] === 'number') {
        classList.push(`do-col-${size}-${props[size]}`);
      } else if (typeof props[size] === 'object') {
        let iProps = props[size];
        Object.keys(iProps).forEach(prop => {
          classList.push(
            prop !== 'span'
              ? `do-col-${size}-${prop}-${iProps[prop]}`
              : `do-col-${size}-${iProps[prop]}`
          );
        });
      }
    });
    return () =>
      h(
        props.tag,
        {
          class: ['do-col', classList],
          style
        },
        slots
      );
  }
});