import { DoKitOverlayState } from '../model/DoKitOverlayState';
import { DoKitFloatOptions } from '../DoKitFloatOptions';
import { CustomViewBuilder, DoKitParams } from './CustomViewRegistry';
import { DoKitPageStack } from './DoKitPageStack';
import { Callback } from '@kit.BasicServicesKit';

/**
 * 一条正在显示的浮窗记录。
 * 名字用来找它。参数交给浮窗内容。位置按横竖屏各记一份，旋转后还能回到这个方向上次停的地方。
 * 摆放开关决定它能不能拖、会不会盖住全屏页和工具面板、空白点击会不会穿透。
 */
export class ViewConfig {
  /** 浮窗名字。同名只保留一条。 */
  name: string = "";
  /** 打开时带进来的参数，绘制时交给浮窗内容。 */
  params?: DoKitParams;
  /** 关闭动作。按名字卸掉自己。 */
  closeHandler?: Callback<void>;
  /** 这条记录是浮窗。 */
  isFloat: boolean;
  /** 最近一次写下的位置。横竖屏各自的位置在下面四个字段。 */
  viewPoint: Position;
  /** 竖屏时卡片左上角横坐标，单位 vp。 */
  portraitX: number = 0;
  /** 竖屏时卡片左上角纵坐标，单位 vp。 */
  portraitY: number = 0;
  /** 横屏时卡片左上角横坐标，单位 vp。 */
  landscapeX: number = 0;
  /** 横屏时卡片左上角纵坐标，单位 vp。 */
  landscapeY: number = 0;
  /** 能拖。卡片浮窗默认能拖。铺满的浮窗不拖。 */
  draggable: boolean = true;
  /** 盖住全屏页。取色、标尺这类要留在页面下面时为 false。 */
  abovePage: boolean = true;
  /** 盖住工具面板。 */
  abovePanel: boolean = true;
  /** 铺满窗口，空白处不吃点击。 */
  passThrough: boolean = false;

  constructor(
    name: string,
    isFloat: boolean,
    params?: DoKitParams,
    viewPoint?: Position,
    closeHandler?: Callback<void>
  ) {
    this.name = name;
    this.isFloat = isFloat;
    this.params = params;
    this.viewPoint = viewPoint ?? { x: 0, y: 0 };
    this.closeHandler = closeHandler;
    // 第一次出现时横竖屏用同一个点。之后拖动只改当前方向。
    const x = ViewConfig.readLen(this.viewPoint.x ?? 0);
    const y = ViewConfig.readLen(this.viewPoint.y ?? 0);
    this.portraitX = x;
    this.portraitY = y;
    this.landscapeX = x;
    this.landscapeY = y;
  }

  /** 坐标可能是数字，也可能是带单位的字符串。这里收成数字，读不出来就当 0。 */
  static readLen(value: Length): number {
    if (typeof value === 'number') {
      return value;
    }
    const parsed = Number(value);
    if (Number.isNaN(parsed)) {
      return 0;
    }
    return parsed;
  }

  /**
   * 把打开时传入的摆放开关抄到这条记录上。
   * 不改位置。位置只在第一次创建时定，同名再打开不会挪卡片。
   */
  applyOptions(options?: DoKitFloatOptions): void {
    if (options === undefined) {
      return;
    }
    this.draggable = options.draggable;
    this.abovePage = options.abovePage;
    this.abovePanel = options.abovePanel;
    this.passThrough = options.passThrough;
  }
}

/**
 * 浮窗、悬浮球、工具面板和全屏页的管理入口。
 * 浮窗记在 viewMap，绘制时按这份表挂层、卸层。
 * 悬浮球和工具面板只记开关，开关在 overlayState。
 * 全屏页不记在这里，进栈出栈交给 DoKitPageStack。栈变化后页面栈用同一个 overlayState 通知浮层重画。
 * 这里改完记录都会 requestFrameSync。DoKitOverlayManager 负责对齐，DoKitOverlayRoot 按层表叠上去。
 */
export class DoKitViewManager {
  /** 浮层状态。页面栈绑定的是同一个实例，这样进页退页也能刷新浮层。 */
  static readonly overlayState: DoKitOverlayState = DoKitViewManager.createOverlayState();
  /** 当前还在的浮窗。key 是浮窗名字。 */
  private static viewMap: Map<string, ViewConfig> = new Map()

  /** 建一份浮层状态，并交给页面栈。页面栈因此不用反过来引用本类。 */
  private static createOverlayState(): DoKitOverlayState {
    const state = new DoKitOverlayState();
    DoKitPageStack.bindOverlayState(state);
    return state;
  }

  /** 当前全部浮窗记录。改这份表会直接影响下次刷新画出的浮窗。 */
  static getViewConfig(): Map<string, ViewConfig> {
    return DoKitViewManager.viewMap;
  }

  /** 按表里的顺序抄出一份列表，给浮层逐条挂浮窗层。 */
  static getViewConfigList(): ViewConfig[] {
    const list: ViewConfig[] = [];

    DoKitViewManager.viewMap.forEach((config, key) => {
      list.push(config);
    });
    return list;
  }

  /**
   * 挂上浮窗。画法由调用方提前登记，这里只记记录并刷新。
   * 同名已经在时，只更新参数和摆放开关，不改位置，也不先卸再挂。
   */
  static launchFloatingView(
    targetName: string,
    builder: CustomViewBuilder,
    params?: DoKitParams,
    options?: DoKitFloatOptions
  ): void {
    const existing = DoKitViewManager.viewMap.get(targetName);
    if (existing !== undefined) {
      existing.params = params;
      existing.applyOptions(options);
      DoKitViewManager.refreshWindow();
      return;
    }
    const config = new ViewConfig(targetName, true, params, DoKitViewManager.initialPoint(options), (): void => {
      DoKitViewManager.removeFloatingView(targetName);
    });
    config.applyOptions(options);
    DoKitViewManager.attachView(targetName, config);
  }

  /** 按名字卸下浮窗，再刷新。层表在下次对齐时去掉这一层。 */
  static removeFloatingView(name: string) {
    DoKitViewManager.viewMap.delete(name);
    DoKitViewManager.refreshWindow();
  }

  /** 清掉已有全屏页，只留这一页当根页。 */
  static launchFullScreen(
    targetName: string,
    params?: DoKitParams,
    isSystemFragment: boolean = false
  ): void {
    DoKitPageStack.open(targetName, params, isSystemFragment);
  }

  /** 在当前全屏页上面再压一页。 */
  static pushFullScreen(
    targetName: string,
    params?: DoKitParams,
    isSystemFragment: boolean = false
  ): void {
    DoKitPageStack.push(targetName, params, isSystemFragment);
  }

  /** 按名字从全屏栈里清掉页面，不一定是栈顶。 */
  static popFullScreenPage(targetName: string): void {
    DoKitPageStack.removeByName(targetName);
  }

  /** 关掉栈顶那一页。栈空时页面栈自己返回。 */
  static popFullScreen(): void {
    DoKitPageStack.pop();
  }

  /** 清掉全部全屏页。 */
  static finishFullScreen(): void {
    DoKitPageStack.clear();
  }


  /** 只改最近一次位置，不区分横竖屏。浮窗拖动后的分方向位置由浮窗自己写回。 */
  static updateViewPoint(name: string, x: number, y: number): void {
    const config = DoKitViewManager.viewMap.get(name);
    if (config === undefined) {
      return;
    }
    config.viewPoint = { x: x, y: y };
  }


  /** 通知浮层按当前记录重新对齐。球、面板、全屏页、浮窗都走这一次刷新。 */
  static refreshWindow() {
    DoKitViewManager.overlayState.requestFrameSync();
  }

  /** 显示悬浮球。全屏页开着时浮层仍会先藏球。 */
  static attachMainIcon(): void {
    DoKitViewManager.overlayState.setIconVisible(true);
  }

  /** 隐藏悬浮球。 */
  static detachMainIcon(): void {
    DoKitViewManager.overlayState.setIconVisible(false);
  }

  /** 悬浮球开关是否开着。全屏页盖住时，开关仍可能是开的。 */
  static isMainIconShow(): boolean {
    return DoKitViewManager.overlayState.isIconVisible();
  }

  /** 工具面板是否开着。全屏页进栈时会先把这个开关关掉。 */
  static isPanelVisible(): boolean {
    return DoKitViewManager.overlayState.isPanelVisible();
  }

  /** 按名字取一条浮窗记录。没有就返回空。 */
  static getViewConfigByName(name: string): ViewConfig | undefined {
    return DoKitViewManager.viewMap.get(name);
  }

  /** 打开工具面板。全屏页开着时直接返回，页关掉后面板也不会自己打开。 */
  static attachToolPanel(): void {
    if (!DoKitPageStack.isEmpty()) {
      return;
    }
    DoKitViewManager.overlayState.showPanel();
  }

  /** 关掉工具面板。已经关着就不再刷新。 */
  static detachToolPanel(): void {
    DoKitViewManager.overlayState.hidePanel();
  }

  /** 记下一条浮窗并刷新。已有同名会被盖掉。 */
  static attachView(name: string, viewConfig: ViewConfig) {
    DoKitViewManager.viewMap.set(name, viewConfig)
    DoKitViewManager.refreshWindow();
  }

  /**
   * 第一次打开时的初始点。
   * 铺满浮窗不用坐标。x、y 都给了才用指定点，否则停在 (20, 200)。
   */
  private static initialPoint(options?: DoKitFloatOptions): Position {
    if (options === undefined || options.passThrough) {
      return { x: 20, y: 200 };
    }
    const x = options.x;
    const y = options.y;
    if (x === undefined || y === undefined) {
      return { x: 20, y: 200 };
    }
    return { x: x, y: y };
  }

}
