import { ColorPickConfig } from '../../config/ColorPickConfig';
import { DoKit } from '../../DoKit';

/**
 * 取色器浮层管理，对标 Android ColorPickManager + 浮层开关
 */
export class ColorPickManager {
  static readonly FLOAT_NAME: string = 'dokit.color';
  private static instance: ColorPickManager | null = null;

  private overlayOpen: boolean = false;
  private restoreIcon: boolean = true;
  private overlayChangeHandler: (() => void) | null = null;

  private constructor() {
  }

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
    this.restoreIcon = DoKit.isMainIconShow;
    this.overlayOpen = true;
    ColorPickConfig.setColorPickOpen(true);
    DoKit.detachToolPanel();
    DoKit.detachMainIcon();
    this.notifyChange();
  }

  close(): void {
    if (!this.overlayOpen) {
      return;
    }
    ColorPickConfig.setColorPickOpen(false);
    this.overlayOpen = false;
    if (this.restoreIcon) {
      DoKit.attachMainIcon();
    }
    DoKit.removeFloatingView(ColorPickManager.FLOAT_NAME);
    this.notifyChange();
  }

  private notifyChange(): void {
    if (this.overlayChangeHandler !== null) {
      this.overlayChangeHandler();
    }
  }
}
