import {Dokit} from '@dokit/web-core-independent'
import {Features} from '../../web/src/feature'
/*
* TODO 全局注册 Dokit
*/
window.Dokit = new Dokit({
  features: Features,
});

