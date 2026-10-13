import { LayoutBorderConfig } from '../../config/LayoutBorderConfig';
import { DoKitViewManager } from '../../core/DoKitViewManager';

/**
 * 布局边框浮层开关，对标 Android LayoutBorderManager.start / stop。
 */
export class LayoutBorderManager {
  private static instance: LayoutBorderManager | null = null;

  private overlayOpen: boolean = false;
  private overlayChangeHandler: (() => void) | null = null;

  static getInstance(): LayoutBorderManager {
    if (LayoutBorderManager.instance === null) {
      LayoutBorderManager.instance = new LayoutBorderManager();
    }
    return LayoutBorderManager.instance;
  }

  setOverlayChangeHandler(handler: (() => void) | null): void {
    this.overlayChangeHandler = handler;
  }

  isOverlayOpen(): boolean {
    return this.overlayOpen;
  }

  open(): void {
    if (this.overlayOpen) {
      return;
    }
    DoKitViewManager.detachToolPanel();
    LayoutBorderConfig.setLayoutBorderOpen(true);
    this.overlayOpen = true;
    this.notifyChange();
  }

  close(): void {
    if (!this.overlayOpen) {
      return;
    }
    LayoutBorderConfig.setLayoutBorderOpen(false);
    this.overlayOpen = false;
    this.notifyChange();
  }

  private notifyChange(): void {
    if (this.overlayChangeHandler !== null) {
      this.overlayChangeHandler();
    }
  }
}
