/* eslint-disable */
import { defineComponent, h, computed, provide } from 'vue';

export default defineComponent({
  name: 'DoRow',
  componentName: 'DoRow',
  props: {
    /**
     * 自定义元素标签
     * 默认值：div
     */
    tag: {
      type: String,
      default: 'div'
    },
    /**
     * 栅格间距
     */
    gutter: Number,
    /**
     * 布局模式，你可以使用 flex 布局
     */
    type: {
      type: String,
      default: 'flex'
    },
    /**
     * flex 布局的水平对齐
     * start（默认值）
     * end
     * center
     * space-around
     * space-between
     */
    justify: {
      type: String,
      default: 'start'
    },

    /**
     * flex 布局的垂直对齐
     * top（默认值）
     * middle
     * bottom
     */
    align: {
      type: String,
      default: 'top'
    }
  },
  setup(props, { slots }) {
    let style = computed(() => {
      const ret = {};

      if (props.gutter) {
        ret.marginLeft = `-${props.gutter / 2}px`;
        ret.marginRight = ret.marginLeft;
      }

      return ret;
    });

    provide('gutter', props.gutter);

    return () =>
      h(
        props.tag,
        {
          class: [
            'do-row',
            props.justify !== 'start' ? `is-justify-${props.justify}` : '',
            props.align !== 'top' ? `is-align-${props.align}` : '',
            { 'do-row--flex': props.type === 'flex' }
          ],
          style
        },
        slots
      );
  }
});