/**
 * 拖拽指令 v-dragable
 * 减少外部依赖
 * 默认使用v-dragable
 * 也接受传入一个config对象 v-dragable="config"
 * config支持 name opacity left top safeBottom 等属性
*/
const INIT_VALUE = 9999
// const SAFE_BOTTOM = 50 // 底部防误触

let MOUSE_DOWN_FLAG = false

const DEFAULT_EL_CONF = {
  name: '',         // 名称 用于存储位置storage的标识，没有则不存储
  opacity: 1,       // 默认透明度
  left: '',         // 初始位置, 没有则居中
  top: '',          // 初始位置, 没有则居中
  safeBottom: 0
}

// TODO 拖拽事件兼容 Pc处理
// TODO 默认初始位置为右下角
export const dragable = {
  mounted (el, binding) {
    el.config = {
      ...DEFAULT_EL_CONF,
      ...binding.value
    }
    // 初始化变量
    el.dokitEntryLastX = INIT_VALUE
    el.dokitEntryLastY = INIT_VALUE
    // 初始化样式
    el.style.position = 'fixed'
    el.style.opacity = el.config.opacity
    el.dokitPositionLeft = getDefaultX(el)
    el.dokitPositionTop = getDefaultY(el)
    el.style.top = `${el.dokitPositionTop}px`
    el.style.left = `${el.dokitPositionLeft}px`

    adjustPosition(el);

    // 触摸事件监听
    el.ontouchstart = () => {
      moveStart(el)
    }
    el.ontouchmove = (e) => {
      e.preventDefault()
      moving(el, e)
    }
    el.ontouchend = (e) => {
      moveEnd(el, e)
    }
    // PC鼠标事件
    el.onmousedown = (e) => {
      e.preventDefault()
      moveStart(el)
      MOUSE_DOWN_FLAG = true
    }

    window.addEventListener('mousemove', (e)=> {
      if (MOUSE_DOWN_FLAG) moving(el, e)
    })

    window.addEventListener('mouseup', (e)=> {
      if (MOUSE_DOWN_FLAG) {
        moveEnd(el, e)
        MOUSE_DOWN_FLAG = false
      }
    })

    window.addEventListener('resize', ()=> {
      adjustPosition(el)
    })
  }
}

function moveStart(el) {
  el.style.opacity = 1
}

function moving(el, e) {
  let target = e.touches ? e.touches[0] : e
  if (el.dokitEntryLastX === INIT_VALUE) {
    el.dokitEntryLastX = target.clientX
    el.dokitEntryLastY = target.clientY
    return
  }

  el.dokitPositionTop += (target.clientY - el.dokitEntryLastY)
  el.dokitPositionLeft += (target.clientX - el.dokitEntryLastX)
  el.dokitEntryLastX = target.clientX
  el.dokitEntryLastY = target.clientY

  // el.style.top = `${getAvailableTop(el)}px`
  // el.style.left = `${getAvailableLeft(el)}px`
  el.style.top = `${el.dokitPositionTop}px`
  el.style.left = `${el.dokitPositionLeft}px`
}

function moveEnd(el, e) {
  setTimeout(() => {
    adjustPosition(el)
    el.config.name && localStorage.setItem(`dokitPositionTop_${el.config.name}`, el.dokitPositionTop);
    el.config.name && localStorage.setItem(`dokitPositionLeft_${el.config.name}`, el.dokitPositionLeft);
  }, 100)
  el.dokitEntryLastX = INIT_VALUE
  el.dokitEntryLastY = INIT_VALUE
  el.style.opacity = el.config.opacity
}

function getDefaultX(el){
  let defaultX = el.config.left || Math.round(window.innerWidth/2)
  return localStorage.getItem(`dokitPositionLeft_${el.config.name}`) ? parseInt(localStorage.getItem(`dokitPositionLeft_${el.config.name}`)) : defaultX
}
function getDefaultY(el){
  let defaultY = el.config.top || Math.round(window.innerHeight/2)
  return localStorage.getItem(`dokitPositionTop_${el.config.name}`) ? parseInt(localStorage.getItem(`dokitPositionTop_${el.config.name}`)) : defaultY
}

// function getAvailableLeft(el){
//   return standardNumber(el.dokitPositionLeft, window.innerWidth - el.clientWidth)
// }
// function getAvailableTop(el){
//   return standardNumber(el.dokitPositionTop, window.innerHeight - el.clientHeight)
// }
// function standardNumber(number, max){
//   if(number < 0){
//     return 0
//   }
//   if(number >= max){
//     return max
//   }
//   return number
// }

function adjustPosition(el) {
  if (el.dokitPositionLeft < 0) {
    el.dokitPositionLeft = 0
    el.style.left = `${el.dokitPositionLeft}px`
  } else if (el.dokitPositionLeft + el.getBoundingClientRect().width > window.innerWidth) {
    el.dokitPositionLeft = window.innerWidth - el.getBoundingClientRect().width
    el.style.left = `${el.dokitPositionLeft}px`
  }
  
  if (el.dokitPositionTop < 0) {
    el.dokitPositionTop = 0
    el.style.top = `${el.dokitPositionTop}px`

  } else if (el.dokitPositionTop + el.getBoundingClientRect().height + el.config.safeBottom > window.innerHeight) {
    el.dokitPositionTop = window.innerHeight - el.getBoundingClientRect().height - el.config.safeBottom
    el.style.top = `${el.dokitPositionTop}px`
  }
}