import {createApp} from 'vue'
import App from './components/app'
import Store from './store'
import {applyLifecyle, LifecycleHooks} from './common/js/lifecycle' 
import {getRouter} from './router'
export class Dokit{
  options = null
  constructor(options){
    this.options = options
    let app = createApp(App);
    let {features} = options; 
    app.use(getRouter(features));
    app.use(Store);
    Store.state.features = features;
    this.app = app;
    this.init();
    this.onLoad();
  }

  onLoad(){
    // Lifecycle Load
    applyLifecyle(this.options.features, LifecycleHooks.LOAD)
  }

  onUnload(){
    // Lifecycle UnLoad
    applyLifecyle(this.options.features, LifecycleHooks.UNLOAD)
  }

  init(){
    let dokitRoot = document.createElement('div')
    dokitRoot.id = "dokit-root"
    document.documentElement.appendChild(dokitRoot);
    // dokit 容器
    let el = document.createElement('div')
    el.id = "dokit-container"
    this.app.mount(el)
    dokitRoot.appendChild(el)
  }
}

export * from './store'
export * from './common/js/feature'

export default {
  Dokit
}
