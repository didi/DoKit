import {getRoutes} from './routes'
import {createRouter} from './router'

export function getRouter(features){
  return createRouter({
    routes: [...getRoutes(features)],
  })
}