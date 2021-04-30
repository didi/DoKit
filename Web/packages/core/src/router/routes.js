import Home from '../components/home'
const defaultRoute = [{
  name: 'home',
  component: Home
}]

export function getRoutes(features){
  let routes = []
  features.forEach(feature => {
    let {list, title:zoneTitle} = feature
    list.forEach(item => {
      let {name, nameZh, component} = item
      routes.push({
        name: name,
        component: component,
        meta: {
          title: nameZh,
          zone: zoneTitle
        }
      })
    })
  })
  return [...defaultRoute, ...routes]
}
