import toastcom from './toast.vue'
import { createApp } from "vue"
const toast = {
}
toast.install = vue => {
  const ins = createApp(toastcom)
  let parent = document.createElement("div")
  ins.mount(parent)
  document.body.appendChild(parent)
  vue.config.globalProperties.$toast = (msg, duration = 3000) => {
    ins._instance.ctx.setMessage(msg)
    ins._instance.ctx.showToast()
    setTimeout(() => {
      ins._instance.ctx.closeToast()
    }, duration)
  }
}
export default toast
