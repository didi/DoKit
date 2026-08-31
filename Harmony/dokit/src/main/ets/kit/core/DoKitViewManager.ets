import { DoKitOverlayState } from '../../model/DoKitOverlayState';
import { CustomDoKitViewRenderer, DoKitIntent, DoKitViewCloseHandler } from './DoKitIntent';

/**
 * 悬浮窗管理门面，对标 Android kit/core/DoKitViewManager
 */
export class DoKitViewManager {
  private static toolPanelChangeHandler: ((visible: boolean) => void) | null = null;
  private static floatingIntent: DoKitIntent | null = null;
  private static floatingAttachHandler: ((intent: DoKitIntent) => void) | null = null;
  private static floatingDetachHandler: (() => void) | null = null;

  static setToolPanelChangeHandler(handler: ((visible: boolean) => void) | null): void {
    DoKitViewManager.toolPanelChangeHandler = handler;
  }

  static setFloatingAttachHandler(handler: ((intent: DoKitIntent) => void) | null): void {
    DoKitViewManager.floatingAttachHandler = handler;
  }

  static setFloatingDetachHandler(handler: (() => void) | null): void {
    DoKitViewManager.floatingDetachHandler = handler;
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
