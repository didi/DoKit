import {createApp} from 'vue'
import App from './components/app'
import Store from './store'
import {getRouter} from './router'
export class Dokit{
  constructor(options){
    let app = createApp(App);
    let {features} = options; 
    app.use(getRouter(features));
    app.use(Store);
    Store.state.features = features;

    this.app = app;
    this.init();
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


export default Dokit