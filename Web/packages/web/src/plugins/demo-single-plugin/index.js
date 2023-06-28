// import IndependPluginDemo from "./IndependPluginDemo.vue";
import FPS from "./FPS.vue";
import { IndependPlugin } from "@dokit/web-core";

export default new IndependPlugin({
  nameZh: "帧率显示",
  name: "test",
  icon: "https://pt-starimg.didistatic.com/static/starimg/img/z1346TQD531618997547642.png",
  component: FPS,
});
