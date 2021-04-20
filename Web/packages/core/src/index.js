import * as vue from 'vue'
import App from './components/app'
import Router from './router'
export class Dokit{
  constructor(options){
    let app = vue.createApp(App);
    let {plugins} = options; 
    console.log('plugins1', plugins)
    app.use(Router);
    this.app = app;
    this._vue = vue;
    this.init();
  }

  init(options){
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