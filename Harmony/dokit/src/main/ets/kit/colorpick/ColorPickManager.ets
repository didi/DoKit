import { ColorPickConfig } from '../../config/ColorPickConfig';
import { DoKitViewManager } from '../../core/DoKitViewManager';
import { DoKitOverlayState } from '../../model/DoKitOverlayState';

/**
 * 取色器浮层管理，对标 Android ColorPickManager + 浮层开关
 */
export class ColorPickManager {
  private static instance: ColorPickManager | null = null;

  private overlayOpen: boolean = false;
  private restoreIcon: boolean = true;
  private overlayChangeHandler: (() => void) | null = null;

  static getInstance(): ColorPickManager {
    if (ColorPickManager.instance === null) {
      ColorPickManager.instance = new ColorPickManager();
    }
    return ColorPickManager.instance;
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
    this.restoreIcon = DoKitOverlayState.isIconVisible();
    DoKitViewManager.detachToolPanel();
    DoKitViewManager.detachMainIcon();
    ColorPickConfig.setColorPickOpen(true);
    this.overlayOpen = true;
    this.notifyChange();
  }

  close(): void {
    if (!this.overlayOpen) {
      return;
    }
    ColorPickConfig.setColorPickOpen(false);
    this.overlayOpen = false;
    if (this.restoreIcon) {
      DoKitViewManager.attachMainIcon();
    }
    this.notifyChange();
  }

  private notifyChange(): void {
    if (this.overlayChangeHandler !== null) {
      this.overlayChangeHandler();
    }
  }
}
