const INIT_VALUE = 9999
const DEFAULT_ZINDEX = 99
const DEFAULT_OPACITY = 0.5
const SAFE_BOTTOM = 50

export default {
  mounted (el) {
    // 初始化变量
    el.dokitEntryLastX = INIT_VALUE
    el.dokitEntryLastY = INIT_VALUE
    el.dokitPositionTop = localStorage.getItem('dokitPositionTop') ? parseInt(localStorage.getItem('dokitPositionTop')) : 0
    el.dokitPositionLeft = localStorage.getItem('dokitPositionLeft') ? parseInt(localStorage.getItem('dokitPositionLeft')) : 0
    // 初始化样式
    el.style.position = 'fixed'
    el.style.opacity = DEFAULT_OPACITY
    el.style.top = `${el.dokitPositionTop}px`
    el.style.left = `${el.dokitPositionLeft}px`
    el.style.zIndex = DEFAULT_ZINDEX

    // 触摸事件监听
    el.ontouchstart = () => {
      el.style.opacity = 1
    }

    el.ontouchmove = (e) => {
      e.preventDefault()
      
      if (el.dokitEntryLastX === INIT_VALUE) {
        el.dokitEntryLastX = e.touches[0].clientX
        el.dokitEntryLastY = e.touches[0].clientY
        return
      }

      el.dokitPositionTop += (e.touches[0].clientY - el.dokitEntryLastY)
      el.dokitPositionLeft += (e.touches[0].clientX - el.dokitEntryLastX)
      el.dokitEntryLastX = e.touches[0].clientX
      el.dokitEntryLastY = e.touches[0].clientY

      el.style.top = `${el.dokitPositionTop}px`
      el.style.left = `${el.dokitPositionLeft}px`
    }

    el.ontouchend = (e) => {
      setTimeout(() => {
        if (el.dokitPositionLeft < 0) {
          el.dokitPositionLeft = 0
          el.style.left = `${el.dokitPositionLeft}px`
        } else if (el.dokitPositionLeft + e.target.clientWidth > window.screen.availWidth) {
          el.dokitPositionLeft = window.screen.availWidth - e.target.clientWidth
          el.style.left = `${el.dokitPositionLeft}px`

        }
        
        if (el.dokitPositionTop < 0) {
          el.dokitPositionTop = 0
          el.style.top = `${el.dokitPositionTop}px`

        } else if (el.dokitPositionTop + e.target.clientHeight + SAFE_BOTTOM > window.screen.availHeight) {
          el.dokitPositionTop = window.screen.availHeight - e.target.clientHeight - SAFE_BOTTOM
          el.style.top = `${el.dokitPositionTop}px`

        }
        localStorage.setItem('dokitPositionTop', el.dokitPositionTop);
        localStorage.setItem('dokitPositionLeft', el.dokitPositionLeft);
      }, 100)
      el.dokitEntryLastX = INIT_VALUE
      el.dokitEntryLastY = INIT_VALUE
      el.style.opacity = 0.5
    }
  }
}