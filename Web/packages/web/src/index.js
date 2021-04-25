import {Dokit} from '@dokit/web-core'
import {Features} from './feature'
/*
* TODO 全局注册 Dokit
*/
window.Dokit = new Dokit({
  features: Features,
});

