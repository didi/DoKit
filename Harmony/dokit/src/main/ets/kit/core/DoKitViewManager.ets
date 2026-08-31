import { DoKitOverlayState } from '../../model/DoKitOverlayState';

/**
 * 悬浮窗管理门面，对标 Android kit/core/DoKitViewManager
 */
export class DoKitViewManager {
  private static toolPanelChangeHandler: ((visible: boolean) => void) | null = null;

  static setToolPanelChangeHandler(handler: ((visible: boolean) => void) | null): void {
    DoKitViewManager.toolPanelChangeHandler = handler;
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
}
