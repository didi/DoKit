/**
 * 浮窗怎么摆。不传则是可拖的小卡片，盖住全屏页和工具面板。
 */
export class DoKitFloatOptions {
  /** 能拖。默认 true。铺满浮窗设为 false。 */
  draggable: boolean = true;
  /** 盖住全屏页。要留在全屏页下面时设为 false。 */
  abovePage: boolean = true;
  /** 盖住工具面板。要留在工具面板下面时设为 false。 */
  abovePanel: boolean = true;
  /** 铺满窗口，不使用 x、y，空白处不吃点击。 */
  passThrough: boolean = false;
  /** 卡片左上角横坐标，单位 vp。和 y 一起给才生效，且只在第一次打开时生效。 */
  x?: number;
  /** 卡片左上角纵坐标，单位 vp。和 x 一起给才生效，且只在第一次打开时生效。 */
  y?: number;
}
