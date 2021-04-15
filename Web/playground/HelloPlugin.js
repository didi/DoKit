// dokit 外部插件示例
export default {
  name: 'hello-plugin',
  install(Vue) {
    const { defineComponent, h } = Vue
    return defineComponent({
      name: 'ToolHelloPlugin',
      componentName: 'ToolHelloPlugin',
      setup() {
        return () => h('div', ['~hello plugin~', 'this is a external plugin'])
      }
    })
  }
}

