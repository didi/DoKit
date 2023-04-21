import {Dokit} from '@dokit/web-core-independent'
import {Features} from '../../web/src/feature'
/**
 * 0.0.3 alpha 3
* TODO 全局注册 Dokit
*/
window.Dokit = new Dokit({
  features: Features,
});

