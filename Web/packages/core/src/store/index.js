import { Store } from "../common/js/store";

const store = new Store({
  state: {
    showContainer: false,
    singlePlugins: [],
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

export function pushContainer(plugin){
  // Unique Container
  store.state.singlePlugins.push(plugin)
  console.log(store.state.singlePlugins)
}
export function popContainer(){
  store.state.singlePlugins.pop()
}

export default store