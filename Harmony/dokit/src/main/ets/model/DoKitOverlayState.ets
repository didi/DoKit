/**
 * DoKit 浮层状态。
 * 面板、浮窗、页面栈、悬浮球变化时 requestFrameSync，浮层只登记一次。
 * 实例挂在 DoKitViewManager 上，页面栈用的是同一个实例。
 */
export class DoKitOverlayState {
  /** 浮层对齐回调。DoKitOverlayManager 挂上时登记，卸下时清空。 */
  private frameSyncHandler: (() => void) | null = null;
  /** 悬浮球开关。默认开。全屏页盖住时，开关仍可能是开的。 */
  private iconVisible: boolean = true;
  /** 工具面板开关。默认关。 */
  private panelVisible: boolean = false;

  /** 登记浮层对齐回调。传空表示卸下。 */
  setFrameSyncHandler(handler: (() => void) | null): void {
    this.frameSyncHandler = handler;
  }

  /** 通知已登记的浮层按当前状态重新对齐。还没登记时什么都不做。 */
  requestFrameSync(): void {
    if (this.frameSyncHandler !== null) {
      this.frameSyncHandler();
    }
  }

  /** 设置悬浮球的初始开关。这时浮层通常还没挂上，所以不通知重画。 */
  init(defaultIconVisible: boolean = true): void {
    this.iconVisible = defaultIconVisible;
  }

  /** 改悬浮球开关，并让浮层重新对齐。 */
  setIconVisible(visible: boolean): void {
    this.iconVisible = visible;
    this.requestFrameSync();
  }

  /** 悬浮球开关是否开着。 */
  isIconVisible(): boolean {
    return this.iconVisible;
  }

  /** 工具面板是否开着。 */
  isPanelVisible(): boolean {
    return this.panelVisible;
  }

  /** 打开工具面板，并让浮层重新对齐。全屏页开着时由调用方直接返回。 */
  showPanel(): void {
    this.panelVisible = true;
    this.requestFrameSync();
  }

  /** 关掉工具面板。已经关着就不再通知。 */
  hidePanel(): void {
    if (!this.panelVisible) {
      return;
    }
    this.panelVisible = false;
    this.requestFrameSync();
  }

  /** 关掉面板开关，不单独刷新。全屏页进栈时用，随后的栈通知会一起对齐。 */
  dismissPanel(): void {
    this.panelVisible = false;
  }
}

