export const noop = () => {}

export class BasePlugin{
  type = ''
  name = ''
  nameZh = ''
  icon = ''
  component = null
  _onLoad = noop
  _onUnload = noop
  _onProductReady = noop
  constructor(options){
    let {name, nameZh, icon, component, onLoad, onUnload, onProductReady} = options;
    this.name = name;
    this.nameZh = nameZh;
    this.icon = icon;
    this.component = component;
    this._onLoad = onLoad || noop;
    this._onUnload = onUnload || noop;
    this._onProductReady = onProductReady || noop
  }
  load(){
    this._onLoad.call(this)
  }
  unload(){
    this._onUnload.call(this)
  }
  productReady(){
    this._onProductReady.call(this)
  }
}

/**
 * 基于路由容器的插件
 */
export class RouterPlugin extends BasePlugin{
  type = "RouterPlugin"
  constructor(options){
    super(options)
  }
}

/**
 * 独立容器的插件
 */
export class IndependPlugin extends BasePlugin{
  type = "IndependPlugin"
  constructor(options){
    super(options)
  }
}

export const isRouterPlugin =  function(plugin){
  return plugin instanceof RouterPlugin
}

export const isIndependPlugin =  function(plugin){
  return plugin instanceof IndependPlugin
}