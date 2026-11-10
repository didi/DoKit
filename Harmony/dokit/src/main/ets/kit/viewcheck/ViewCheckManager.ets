import { ViewCheckConfig } from '../../config/ViewCheckConfig';
import { DoKit } from '../../DoKit';

/**
 * 控件检查浮层开关，对标 Android ViewCheckerKit 拉起准星、描边、信息条。
 */
export class ViewCheckManager {
  static readonly FLOAT_NAME: string = 'dokit.view';
  private static instance: ViewCheckManager | null = null;

  private overlayOpen: boolean = false;
  private overlayChangeHandler: (() => void) | null = null;

  static getInstance(): ViewCheckManager {
    if (ViewCheckManager.instance === null) {
      ViewCheckManager.instance = new ViewCheckManager();
    }
    return ViewCheckManager.instance;
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
    this.overlayOpen = true;
    ViewCheckConfig.setViewCheckOpen(true);
    DoKit.detachToolPanel();
    this.notifyChange();
  }

  close(): void {
    if (!this.overlayOpen) {
      return;
    }
    ViewCheckConfig.setViewCheckOpen(false);
    this.overlayOpen = false;
    DoKit.removeFloatingView(ViewCheckManager.FLOAT_NAME);
    this.notifyChange();
  }

  private notifyChange(): void {
    if (this.overlayChangeHandler !== null) {
      this.overlayChangeHandler();
    }
  }
}
