/**
 * 拖拽指令 v-dragable
 * 减少外部依赖
*/
const INIT_VALUE = 9999
const DEFAULT_OPACITY = 0.5
const SAFE_BOTTOM = 50 // 底部防误触
// TODO 拖拽事件兼容 Pc处理
// TODO 默认初始位置为右下角
export default {
  mounted (el) {
    // 初始化变量
    el.dokitEntryLastX = INIT_VALUE
    el.dokitEntryLastY = INIT_VALUE
    // 初始化样式
    el.style.position = 'fixed'
    el.style.opacity = DEFAULT_OPACITY
    el.dokitPositionLeft = getDefaultX(el)
    el.dokitPositionTop = getDefaultY(el)
    el.style.top = `${el.dokitPositionTop}px`
    el.style.left = `${el.dokitPositionLeft}px`

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

      el.style.top = `${getAvailableTop(el)}px`
      el.style.left = `${getAvailableLeft(el)}px`
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

function getDefaultX(el){
  let defaultX = Math.round(window.outerWidth/2)
  return localStorage.getItem('dokitPositionLeft') ? parseInt(localStorage.getItem('dokitPositionLeft')) : defaultX
}
function getDefaultY(el){
  let defaultY = Math.round(window.outerHeight/2)
  return localStorage.getItem('dokitPositionTop') ? parseInt(localStorage.getItem('dokitPositionTop')) : defaultY
}
function getAvailableLeft(el){
  return standardNumber(el.dokitPositionLeft, window.outerWidth - el.clientWidth)
}
function getAvailableTop(el){
  return standardNumber(el.dokitPositionTop, window.outerHeight - el.clientHeight)
}
function standardNumber(number, max){
  if(number < 0){
    return 0
  }
  if(number >= max){
    return max
  }
  return number
}