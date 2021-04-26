export const LifecycleHooks = {
  LOAD: 'load',
  UNLOAD: 'unload'
}

export const applyLifecyle = function(features, lifecycle){
  features.forEach(feature => {
    let {list} = feature
    list.forEach(item => {
      if(isFunction(item[lifecycle])){
        item[lifecycle]()
      }
    });
  });
}

export const isFunction = function(ob){
  return typeof ob === 'function'
}