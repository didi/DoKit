import { DataSourceFactory } from './datasource/DataSourceFactory';
import { PerformanceFragmentCloseListener } from './PerformanceFragmentCloseListener';

/**
 * 性能浮层管理，对标 Android PerformanceDokitViewManager
 */
export class PerformanceDokitViewManager {
  private static overlayOpenMap: Map<number, boolean> = new Map();
  private static closeListenerMap: Map<number, PerformanceFragmentCloseListener> = new Map();
  private static overlayChangeHandler: (() => void) | null = null;

  static setOverlayChangeHandler(handler: (() => void) | null): void {
    PerformanceDokitViewManager.overlayChangeHandler = handler;
  }

  static isFpsOverlayOpen(): boolean {
    return PerformanceDokitViewManager.isOverlayOpen(DataSourceFactory.TYPE_FPS);
  }

  static isCpuOverlayOpen(): boolean {
    return PerformanceDokitViewManager.isOverlayOpen(DataSourceFactory.TYPE_CPU);
  }

  static isRamOverlayOpen(): boolean {
    return PerformanceDokitViewManager.isOverlayOpen(DataSourceFactory.TYPE_RAM);
  }

  static isOverlayOpen(performanceType: number): boolean {
    return PerformanceDokitViewManager.overlayOpenMap.get(performanceType) ?? false;
  }

  static open(performanceType: number, _title: string, listener: PerformanceFragmentCloseListener): void {
    PerformanceDokitViewManager.overlayOpenMap.set(performanceType, true);
    PerformanceDokitViewManager.closeListenerMap.set(performanceType, listener);
    PerformanceDokitViewManager.notifyChange();
  }

  static close(performanceType: number, _title: string): void {
    PerformanceDokitViewManager.overlayOpenMap.set(performanceType, false);
    PerformanceDokitViewManager.notifyChange();
  }

  static onPerformanceSettingFragmentDestroy(listener: PerformanceFragmentCloseListener): void {
    PerformanceDokitViewManager.closeListenerMap.forEach((value: PerformanceFragmentCloseListener, key: number) => {
      if (value === listener) {
        PerformanceDokitViewManager.closeListenerMap.delete(key);
      }
    });
  }

  static closeOverlayFromView(performanceType: number): void {
    const listener = PerformanceDokitViewManager.closeListenerMap.get(performanceType);
    if (listener !== undefined) {
      listener(performanceType);
    }
    PerformanceDokitViewManager.close(performanceType, '');
  }

  static closeFpsOverlayFromView(): void {
    PerformanceDokitViewManager.closeOverlayFromView(DataSourceFactory.TYPE_FPS);
  }

  private static notifyChange(): void {
    if (PerformanceDokitViewManager.overlayChangeHandler !== null) {
      PerformanceDokitViewManager.overlayChangeHandler();
    }
  }
}
