import { Store } from "../common/js/store";

const store = new Store({
  state: {
    showContainer: false,
    independPlugins: [],
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

export function toggleContainer(flag){
  if(flag){
    store.state.showContainer = flag;
  }else{
    store.state.showContainer = !store.state.showContainer;
  }
}

export function addIndependPlugin(plugin){
  // Unique Container
  let index = store.state.independPlugins.findIndex(ele => {
    return ele.name === plugin.name
  })
  if (index > -1) return
  store.state.independPlugins.push(plugin)
}

export function removeIndependPlugin(name){
  let index = store.state.independPlugins.findIndex(ele => {
    return ele.name === name
  })
  if (index === -1) return
  store.state.independPlugins.splice(index, 1)
}

export default store