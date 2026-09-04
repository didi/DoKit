import { DoKitOverlayState } from '../model/DoKitOverlayState';
import { DoKitParams } from './CustomViewRegistry';
import { CustomDoKitViewRenderer, DoKitIntent, DoKitViewCloseHandler } from './DoKitIntent';
import { Position } from '@kit.ArkUI';


/**
 * 视图配置信息
 */
export class ViewConfig {
  name: string = "";
  params?: DoKitParams;
  closeHandler?: () => void;
  isFloat: boolean = false;
  viewPoint?: Position;
}

/**
 * 悬浮窗管理门面，对标 Android kit/core/DoKitViewManager
 */
export class DoKitViewManager {
  private static toolPanelChangeHandler: ((visible: boolean) => void) | null = null;
  private static floatingIntent: DoKitIntent | null = null;
  private static floatingAttachHandler: ((intent: DoKitIntent) => void) | null = null;
  private static floatingDetachHandler: (() => void) | null = null;
  private static windowRefreshHandler: (() => void) | null = null;
  private static viewMap: Map<string, ViewConfig> = new Map()

  static setToolPanelChangeHandler(handler: ((visible: boolean) => void) | null): void {
    DoKitViewManager.toolPanelChangeHandler = handler;
  }

  static setFloatingAttachHandler(handler: ((intent: DoKitIntent) => void) | null): void {
    DoKitViewManager.floatingAttachHandler = handler;
  }

  static setFloatingDetachHandler(handler: (() => void) | null): void {
    DoKitViewManager.floatingDetachHandler = handler;
  }

  static setWindowRefreshHandler(handler: (() => void) | null) {
    DoKitViewManager.windowRefreshHandler = handler;
  }

  static refreshWindow() {
    if (DoKitViewManager.windowRefreshHandler != null) {
      DoKitViewManager.windowRefreshHandler()
    }
  }

  static attachMainIcon(): void {
    DoKitOverlayState.setIconVisible(true);
  }

  static detachMainIcon(): void {
    DoKitOverlayState.setIconVisible(false);
  }

  static attachToolPanel(): void {
    DoKitOverlayState.requestOpenPanel();
    if (DoKitViewManager.toolPanelChangeHandler !== null) {
      DoKitViewManager.toolPanelChangeHandler(true);
    }
  }

  static detachToolPanel(): void {
    if (DoKitViewManager.toolPanelChangeHandler !== null) {
      DoKitViewManager.toolPanelChangeHandler(false);
    }
  }

  static attachView(name: string, viewConfig: ViewConfig) {
    DoKitViewManager.viewMap.set(name, viewConfig)
    DoKitViewManager.refreshWindow();
  }

  static detachView(name: string) {
    DoKitViewManager.viewMap.delete(name);
    DoKitViewManager.refreshWindow();
  }

  static getViewConfig(): Map<string, ViewConfig> {
    return DoKitViewManager.viewMap;
  }

  static getViewConfigList(): ViewConfig[] {
    const list: ViewConfig[] = [];

    DoKitViewManager.viewMap.forEach((config, key) => {
      list.push(config);
    });
    return list;
  }

  /** 对标 Android DoKitViewManager.attach(DoKitIntent) */
  static attach(doKitIntent: DoKitIntent): void {
    DoKitViewManager.floatingIntent = doKitIntent;
    DoKitViewManager.detachToolPanel();
    if (DoKitViewManager.floatingAttachHandler !== null) {
      DoKitViewManager.floatingAttachHandler(doKitIntent);
    }
  }

  /** 对标 Android DoKitViewManager.detach(tag) */
  static detach(tag: string): void {
    if (DoKitViewManager.floatingIntent === null || DoKitViewManager.floatingIntent.tag !== tag) {
      return;
    }
    DoKitViewManager.floatingIntent = null;
    if (DoKitViewManager.floatingDetachHandler !== null) {
      DoKitViewManager.floatingDetachHandler();
    }
  }

  static getFloatingIntent(): DoKitIntent | null {
    return DoKitViewManager.floatingIntent;
  }

  static hasFloating(): boolean {
    return DoKitViewManager.floatingIntent !== null;
  }
}

export { DoKitViewCloseHandler as CustomKitCloseHandler };

export type { CustomDoKitViewRenderer as CustomKitPageRenderer };
