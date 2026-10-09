import { AlignRulerConfig } from '../../config/AlignRulerConfig';
import { DoKitViewManager } from '../../core/DoKitViewManager';

/**
 * 对齐标尺浮层开关，对标 Android AlignRulerKit 拉起/关闭三层悬浮窗。
 * 只关工具面板，主图标保持原样。
 */
export class AlignRulerManager {
  private static instance: AlignRulerManager | null = null;

  private overlayOpen: boolean = false;
  private overlayChangeHandler: (() => void) | null = null;

  static getInstance(): AlignRulerManager {
    if (AlignRulerManager.instance === null) {
      AlignRulerManager.instance = new AlignRulerManager();
    }
    return AlignRulerManager.instance;
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
    AlignRulerConfig.setAlignRulerOpen(true);
    this.overlayOpen = true;
    this.notifyChange();
  }

  close(): void {
    if (!this.overlayOpen) {
      return;
    }
    AlignRulerConfig.setAlignRulerOpen(false);
    this.overlayOpen = false;
    this.notifyChange();
  }

  private notifyChange(): void {
    if (this.overlayChangeHandler !== null) {
      this.overlayChangeHandler();
    }
  }
}
