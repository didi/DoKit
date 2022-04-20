import {
  createApp
} from 'vue'
import App from './components/app'
import DokitUi from './common/components/dokit-ui'
import Store from './store'
import {
  applyLifecyle,
  LifecycleHooks
} from './common/js/lifecycle'
import {
  getRouter
} from './router'
import toast from './common/components/toast/index'
export class Dokit {
  options = null
  constructor(options) {
    this.options = options
    let app = createApp(App);
    let {
      features
    } = options;
    app.use(DokitUi);
    app.use(getRouter(features));
    app.use(Store);
    if (document.readyState === "loading") {
      // window.addEventListener("load", function (_event) {
      //   app.use(toast);
      // });
      document.addEventListener("readystatechange", function () {
        if (document.readyState === "interactive") {
          app.use(toast);
        }
      });
    }else{
      app.use(toast);
    }
    Store.state.features = features;
    this.app = app;
    this.init();
    this.onLoad();
  }

  onLoad() {
    // Lifecycle Load
    applyLifecyle(this.options.features, LifecycleHooks.LOAD)
  }

  onUnload() {
    // Lifecycle UnLoad
    applyLifecyle(this.options.features, LifecycleHooks.UNLOAD)
  }

  onProductReady() {
    applyLifecyle(this.options.features, LifecycleHooks.PRODUCT_READY)
  }

  init() {
    let dokitRoot = document.createElement('div')
    dokitRoot.id = "dokit-root"
    document.documentElement.appendChild(dokitRoot);
    // dokit 容器
    let el = document.createElement('div')
    el.id = "dokit-container"
    this.app.mount(el)
    dokitRoot.appendChild(el)
  }

  setProductId(productId) {
    this.productId = productId
    Store.state.productId = productId
    this.onProductReady()
  }

  startMultiControl(url, role) {
    if(role==='client'||role==='host') {
      role === 'host'&&(Store.state.isHost = true);
      Store.state.socketUrl = url;
      Store.state.socketConnect = true;
    }
  }

  stopMultiControl(){
    Store.state.socketConnect = false;
  }

  isNativeContainer(){
    if(!Store.state.socketConnect){
      Store.state.isNative = true;
      let nativeConnectSerial = localStorage.getItem('nativeConnectSerial')
      nativeConnectSerial&&(Store.state.connectSerial = nativeConnectSerial)
    }
  }
}

export * from './store'
export * from './common/js/feature'

//其他模块加载该模块时，import命令可以为该匿名函数指定任意名字
export default {
  Dokit
}