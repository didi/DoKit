import { createRouter, createMemoryHistory } from 'vue-router'
import {routes, getRoutes} from './routes'

export function getRouter(features){
  return createRouter({
    routes: [...routes, ...getRoutes(features)],
    history: createMemoryHistory()
  })
}