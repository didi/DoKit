import { window } from '@kit.ArkUI';

/**
 * 主窗口避让区。把状态栏、导航条高度写入 AppStorage，并换算成布局用的 vp。
 */
export class DoKitWindowUtil {
  /** 导航条高度，单位 px。主窗口浮层和工具底栏都读这一份。 */
  static readonly NAV_INSET_PX: string = 'dokitNavInsetPx';
  /** 状态栏高度，单位 px。避免和业务自己的 topRectHeight 撞名。 */
  static readonly TOP_RECT_HEIGHT: string = 'dokitTopRectHeight';
  /** 主窗口。避免和业务自己的 windowStage 撞名。 */
  static readonly WINDOW_STAGE: string = 'dokitWindowStage';

  /** 正在监听避让区的主窗口。卸下监听时用同一份。 */
  private static mainWin: window.Window | null = null;
  /** 状态栏避让回调。off 必须传入 on 时的同一个函数。 */
  private static onSystem: ((data: window.AvoidAreaOptions) => void) | null = null;
  /** 导航条避让回调。off 必须传入 on 时的同一个函数。 */
  private static onNav: ((data: window.AvoidAreaOptions) => void) | null = null;

  /**
   * 把主窗口和当前避让高度写入 AppStorage，并监听后续变化。
   * 重复绑定时先卸掉上一份监听。
   */
  static bindWindowStageToAppStorage(windowStage: window.WindowStage): void {
    DoKitWindowUtil.unbindWindowAvoidArea();
    AppStorage.setOrCreate(DoKitWindowUtil.WINDOW_STAGE, windowStage);
    try {
      const mainWin = windowStage.getMainWindowSync();
      DoKitWindowUtil.mainWin = mainWin;
      const avoidArea = mainWin.getWindowAvoidArea(window.AvoidAreaType.TYPE_SYSTEM);
      AppStorage.setOrCreate(DoKitWindowUtil.TOP_RECT_HEIGHT, avoidArea.topRect.height);
      const nav = mainWin.getWindowAvoidArea(window.AvoidAreaType.TYPE_NAVIGATION_INDICATOR);
      AppStorage.setOrCreate(DoKitWindowUtil.NAV_INSET_PX, nav.bottomRect.height);
      DoKitWindowUtil.onSystem = (data: window.AvoidAreaOptions): void => {
        if (data.type === window.AvoidAreaType.TYPE_SYSTEM) {
          AppStorage.setOrCreate(DoKitWindowUtil.TOP_RECT_HEIGHT, data.area.topRect.height);
        }
      };
      DoKitWindowUtil.onNav = (data: window.AvoidAreaOptions): void => {
        if (data.type === window.AvoidAreaType.TYPE_NAVIGATION_INDICATOR) {
          AppStorage.setOrCreate(DoKitWindowUtil.NAV_INSET_PX, data.area.bottomRect.height);
        }
      };
      mainWin.on('avoidAreaChange', DoKitWindowUtil.onSystem);
      mainWin.on('avoidAreaChange', DoKitWindowUtil.onNav);
    } catch (_e) {
    }
  }

  /** 卸掉状态栏和导航条的避让监听。 */
  static unbindWindowAvoidArea(): void {
    const mainWin = DoKitWindowUtil.mainWin;
    if (mainWin !== null) {
      try {
        if (DoKitWindowUtil.onSystem !== null) {
          mainWin.off('avoidAreaChange', DoKitWindowUtil.onSystem);
        }
        if (DoKitWindowUtil.onNav !== null) {
          mainWin.off('avoidAreaChange', DoKitWindowUtil.onNav);
        }
      } catch (_e) {
      }
    }
    DoKitWindowUtil.onSystem = null;
    DoKitWindowUtil.onNav = null;
    DoKitWindowUtil.mainWin = null;
  }

  /** 状态栏高度，单位 vp。没有高度时不留白。 */
  static topInsetVp(topRectHeightPx: number): number {
    if (topRectHeightPx <= 0) {
      return 0;
    }
    return px2vp(topRectHeightPx);
  }

  /** 导航条高度，单位 vp。没有高度时不留白。 */
  static navInsetVp(navInsetPx: number): number {
    if (navInsetPx <= 0) {
      return 0;
    }
    return px2vp(navInsetPx);
  }

  /**
   * 全屏页和工具面板顶部要留出的高度，单位 vp。
   * 已经量到状态栏高度就避开。还没量到时看主窗口是不是全屏布局，
   * 全屏布局的内容会伸进状态栏，需要留出顶部。
   */
  static statusBarInsetVp(topRectHeightPx: number): number {
    if (!DoKitWindowUtil.shouldAvoidStatusBar(topRectHeightPx)) {
      return 0;
    }
    return DoKitWindowUtil.topInsetVp(topRectHeightPx);
  }

  /** 已经量到状态栏高度就避开。还没量到时，全屏布局才需要避开。 */
  private static shouldAvoidStatusBar(topRectHeightPx: number): boolean {
    if (topRectHeightPx > 0) {
      return true;
    }
    try {
      const stage = AppStorage.get<window.WindowStage>(DoKitWindowUtil.WINDOW_STAGE);
      if (stage === undefined || stage === null) {
        return false;
      }
      return stage.getMainWindowSync().getWindowProperties().isLayoutFullScreen;
    } catch (_err) {
      return false;
    }
  }
}
