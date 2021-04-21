import Index from '../components/index'
export const routes = [{
  path: '/',
  name: 'index',
  component: Index
}]

export function getRoutes(features){
  let routes = []
  features.forEach(feature => {
    let {list, title:featureTitle} = feature
    list.forEach(item => {
      // TODO 暂时只支持路由方式的插件
      let {name, title, component} = item
      routes.push({
        path: `/${name}`,
        name: name,
        component: component,
        meta: {
          title: title,
          feature: featureTitle
        }
      })
    })
  })
  return routes
}