import {RouterPlugin} from '@dokit/web-core'
import Geolocation from './main.vue'

export default new RouterPlugin({
  nameZh: '位置信息',
  name: 'geolocation-information',
  icon: 'https://pt-starimg.didistatic.com/static/starimg/img/lxFgOzH5HN1655278941999.png',
  component: Geolocation
})