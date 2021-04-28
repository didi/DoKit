import { Store } from "../common/js/store";

const store = new Store({
  state: {
    features: []
  }
})

// 更新全局 Store 数据
export function updateGlobalData(key, value){
  store.state[key] = value
}

// 获取当前 Store 数据的状态
export function getGlobalData(){
  return store.state
}

export default store