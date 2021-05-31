import Resources from './main.vue'
import { RouterPlugin } from '@dokit/web-core'


export default new RouterPlugin({
    name: 'resources',
    nameZh: '静态资源',
    component: Resources,
    icon: 'https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png',

    onLoad() {
        //console.log(window.performance.now())
        console.log('resources')
    },
    onUnload() {

    },

})