/**
 * 浮窗怎么摆。不传则是可拖的小卡片，盖住全屏页和工具面板。
 */
export class DoKitFloatOptions {
  draggable: boolean = true;
  abovePage: boolean = true;
  abovePanel: boolean = true;
  /** 铺满且不位移，空白处不吃点击。 */
  passThrough: boolean = false;
  /** 卡片左上角横坐标，单位 vp。和 y 一起给才生效。 */
  x?: number;
  /** 卡片左上角纵坐标，单位 vp。和 x 一起给才生效。 */
  y?: number;
}
