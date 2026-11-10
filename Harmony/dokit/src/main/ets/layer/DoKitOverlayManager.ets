import { ComponentContent, window } from '@kit.ArkUI';
import { DoKitViewManager } from '../core/DoKitViewManager';
import { DoKitPageStack } from '../core/DoKitPageStack';
import { DoKitOverlayName, DoKitOverlayTable } from './DoKitOverlayTable';
import { DoKitWindowUtil } from '../util/DoKitWindowUtil';
import { BALL_LAYER_BUILDER } from './DoKitOverlayBall';
import { FLOAT_LAYER_BUILDER } from './DoKitOverlayFloat';
import { PAGE_LAYER_BUILDER } from './DoKitOverlayPage';
import { PANEL_LAYER_BUILDER } from './DoKitOverlayPanel';
import { DOKIT_OVERLAY_ROOT_BUILDER } from './DoKitOverlayRoot';

/**
 * 主窗口浮层的挂卸和对齐。整个进程只有一个实例。
 * 按当前状态决定全屏页、工具面板、悬浮球和浮窗在不在。
 * 叠层在 DoKitOverlayRoot，每一层的画法在对应组件文件里。
 */
export class DoKitOverlayManager {
  private static instance: DoKitOverlayManager | null = null;
  /** 主窗口。用来取 UIContext，把浮层挂到 OverlayManager。 */
  private stage: window.WindowStage | undefined = undefined;
  /** 已经挂上的浮层内容。卸下时用它从 OverlayManager 移除。 */
  private content: ComponentContent<DoKitOverlayTable> | null = null;
  /** 当前叠放顺序。根层拿到的是同一份。 */
  private readonly table: DoKitOverlayTable = new DoKitOverlayTable();
  /** 浮层是否已经挂上。挂过就不再挂第二次。 */
  private shown: boolean = false;
  /** 当前全屏栈顶。变了才让全屏层重建，避免压栈后还画着旧页。 */
  private lastPageKey: string = '';

  private constructor() {
  }

  /** 拿到唯一实例。第一次调用时创建。 */
  static getInstance(): DoKitOverlayManager {
    if (DoKitOverlayManager.instance === null) {
      DoKitOverlayManager.instance = new DoKitOverlayManager();
    }
    return DoKitOverlayManager.instance;
  }

  /** 记下主窗口，并开始监听状态栏、导航栏避让。 */
  addRootComponent(stage: window.WindowStage): void {
    this.stage = stage;
    DoKitWindowUtil.bindWindowStageToAppStorage(stage);
  }

  /** 把浮层挂到主窗口的 OverlayManager。已经挂过、或还没有窗口时直接返回。 */
  addRootComponentContent(): void {
    const stage = this.stage;
    if (this.shown || stage === undefined) {
      return;
    }
    this.bindHandlers();
    try {
      const uiContext = stage.getMainWindowSync().getUIContext();
      this.content = new ComponentContent<DoKitOverlayTable>(uiContext, DOKIT_OVERLAY_ROOT_BUILDER, this.table);
      uiContext.getOverlayManager().addComponentContent(this.content);
      this.shown = true;
      this.syncFrame();
    } catch (_e) {
      this.unbindHandlers();
      this.content = null;
      this.shown = false;
    }
  }

  /** 浮层是否已经挂上。 */
  isShown(): boolean {
    return this.shown;
  }

  /** 从 OverlayManager 卸下浮层。窗口和层表还留着。 */
  removeRootComponentContent(): void {
    this.removeContent();
  }

  /** 卸下浮层，停止避让监听，并清空层表和窗口。单例对象还留着。 */
  removeRootComponent(): void {
    this.removeContent();
    DoKitWindowUtil.unbindWindowAvoidArea();
    this.table.clear();
    this.stage = undefined;
    this.lastPageKey = '';
  }

  /** 移除 OverlayManager 上的那一块内容，并解掉刷新回调。 */
  private removeContent(): void {
    this.unbindHandlers();
    const stage = this.stage;
    const content = this.content;
    if (stage !== undefined && content !== null) {
      try {
        stage.getMainWindowSync().getUIContext().getOverlayManager().removeComponentContent(content);
      } catch (_e) {
      }
    }
    this.content = null;
    this.shown = false;
  }

  /** 按当前开关和页面栈重排层。这里只读状态，不改开关。 */
  private syncFrame(): void {
    // 先拿掉不该在的再挂。先挂再拿的话，新层会插到马上要拿掉的那层下面
    if (DoKitPageStack.isEmpty()) {
      this.table.detach(DoKitOverlayName.PAGE);
      this.lastPageKey = '';
      if (DoKitViewManager.isPanelVisible()) {
        this.table.addOverlayOrUpdateDraw(DoKitOverlayName.PANEL, PANEL_LAYER_BUILDER, false, false);
      } else {
        this.table.detach(DoKitOverlayName.PANEL);
      }
      if (DoKitViewManager.isMainIconShow()) {
        this.table.addOverlayOrUpdateDraw(DoKitOverlayName.BALL, BALL_LAYER_BUILDER, false, false);
      } else {
        this.table.detach(DoKitOverlayName.BALL);
      }
    } else {
      this.table.detach(DoKitOverlayName.PANEL);
      this.table.detach(DoKitOverlayName.BALL);
      this.table.addOverlayOrUpdateDraw(DoKitOverlayName.PAGE, PAGE_LAYER_BUILDER, false, false);
      this.touchPageIfChanged();
    }
    this.syncFloatLayers();
    this.table.requestRedraw();
  }

  /** 栈顶换了就让全屏层重建。这一层刚挂上时也走这里，最后只请求一次重画。 */
  private touchPageIfChanged(): void {
    const top = DoKitPageStack.peek();
    const pageKey = top === undefined ? '' : `${top.name}|${DoKitPageStack.getRevision()}`;
    if (pageKey !== this.lastPageKey) {
      this.table.touch(DoKitOverlayName.PAGE);
    }
    this.lastPageKey = pageKey;
  }

  /** 浮窗表里有的挂上，没有的从层表去掉。球、面板、全屏页不动。 */
  private syncFloatLayers(): void {
    const names: string[] = [];
    const configs = DoKitViewManager.getViewConfigList();
    for (let i = 0; i < configs.length; i++) {
      const config = configs[i];
      if (config === undefined) {
        continue;
      }
      names.push(config.name);
      this.table.addOverlayOrUpdateDraw(config.name, FLOAT_LAYER_BUILDER, config.abovePage, config.abovePanel);
    }
    this.table.removeUnlistedFloats(names);
  }

  /** 面板、浮窗、页面栈、悬浮球变化时，重新对齐层。只登记一次。 */
  private bindHandlers(): void {
    DoKitViewManager.overlayState.setFrameSyncHandler(() => {
      DoKitOverlayManager.getInstance().syncFrame();
    });
  }

  /** 卸下 bindHandlers 注册的回调。 */
  private unbindHandlers(): void {
    DoKitViewManager.overlayState.setFrameSyncHandler(null);
  }
}
