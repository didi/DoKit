import * as Vue  from 'vue'
import App from './App.vue'

window.dokit = {
  app: Vue.createApp(App),
  outPlugins: [],
  init() {
    // 构建挂载节点
    let dokitRoot = document.createElement('div')
    document.documentElement.appendChild(dokitRoot)

    // dokit 容器
    let el = document.createElement('div')
    Object.assign(el, {
      id: 'dokit-container',
      contentEditable: false
    })

    this.app.mount(el)
    dokitRoot.appendChild(el)
  },



  registerPlugin(option) {
    if (!(option.name && option.install )) return

    let component = option.install(Vue)
    // 全局注册组件
    this.app.component(`tool-${option.name}`, component)
    
    this.outPlugins.push({
      component: option.name,
      displayName: option.name
    })
    return this
  }
}