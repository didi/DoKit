import {shallowRef, unref} from 'vue'

/**
 * DoKit 专用 Router
 * 1. 处理普通的 Router Container
 */
export const createRouter = function({routes:mainRoutes}){
  const routes = mainRoutes;
  const history = [];
  const defaultRoute = 'home'
  const homeRoute = matchRoute(defaultRoute)
  const currentRoute = shallowRef(homeRoute)
  /* Route Operation Start */
  function addRoute(route){
    routes.push(route)
  }
  function removeRoute(name){
    let index = routes.findIndex((item)=> item.name === name)
    if(index != -1){
      return routes.splice(index, 1)
    }else{
      return null
    }
  }
  function hasRoute(name){
    let index = routes.findIndex((item)=> item.name === name)
    return index !== -1
  }
  function getRoutes(){
    return routes
  }
  /* Route Operation End */
  /* Router Operation Start */
  function push({name}){
    history.push(name)
    updateCurrentRoute({name})
  }

  function replace(name){
    history.pop()
    history.push(name)
    updateCurrentRoute({name})
  }

  function back(){
    history.pop();
    let index = history.length - 1
    updateCurrentRoute({
      name: history[index]
    })
  }

  /* Router Operation End */

  function install(app){
    // Install To Vue
    const router = this
    app.config.globalProperties.$router = router
    Object.defineProperty(app.config.globalProperties, '$route', {
      get: () => unref(currentRoute)
    })
  }

  function matchRoute(name){
    let route = routes.find((item) => {
      return item.name === name
    })
    return route
  }
  
  function updateCurrentRoute({name}){
    let route = matchRoute(name) || homeRoute
    currentRoute.value = route
  }
  return {
    currentRoute,
    addRoute,
    removeRoute,
    hasRoute,
    getRoutes,
    push,
    replace,
    back,
    install
  }
}
