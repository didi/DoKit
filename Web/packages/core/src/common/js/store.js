import {reactive} from 'vue'
const storeKey = 'store'
/**
 * 简易版 Store 实现
 * 支持直接修改 Store 数据
 */
export class Store{
  constructor(options){
    let {state} = options
    this.initData(state)
  }

  initData(data = {}){
    this._state = reactive({
      data: data
    })
  }

  get state(){
    return this._state.data
  }

  install(app){
    app.provide(storeKey, this)
    app.config.globalProperties.$store = this
  }

}
