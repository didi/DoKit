import { createApp } from 'vue'
import App from './App.vue'

window.dokit = {
  init() {
    // 构建shadowRoot
    let shadowRoot = document.createElement('div')
    document.documentElement.appendChild(shadowRoot)

    // dokit 容器
    let el = document.createElement('div')
    Object.assign(el, {
      id: 'dokit-container',
      contentEditable: false
    })

    const app = createApp(App)
    app.mount(el)
    shadowRoot.appendChild(el)
  }
}