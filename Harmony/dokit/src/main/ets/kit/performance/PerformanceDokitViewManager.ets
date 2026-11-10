import { hilog } from '@kit.PerformanceAnalysisKit';
import { DokitMemoryConfig } from '../../config/DokitMemoryConfig';
import { DoKit } from '../../DoKit';
import { DoKitFloatOptions } from '../../DoKitFloatOptions';
import { NetworkManager } from '../network/NetworkManager';
import { DataSourceFactory } from './datasource/DataSourceFactory';
import { PerformanceDataManager } from './PerformanceDataManager';
import { PerformanceFragmentCloseListener } from './PerformanceFragmentCloseListener';
import { PERFORMANCE_FLOAT_BUILDER, setPerformanceFloatClose } from './PerformanceFloat';

/**
 * 性能浮层管理，对标 Android PerformanceDokitViewManager
 */
export class PerformanceDokitViewManager {
  static readonly FLOAT_NAME: string = 'dokit.perf';
  private static readonly DOMAIN: number = 0xD0F1;
  private static readonly TAG: string = 'DoKitH5';

  private static overlayOpenMap: Map<number, boolean> = new Map();
  private static closeListenerMap: Map<number, PerformanceFragmentCloseListener> = new Map();
  private static overlayChangeHandler: (() => void) | null = null;
  private static switchSyncHandler: (() => void) | null = null;

  static setOverlayChangeHandler(handler: (() => void) | null): void {
    PerformanceDokitViewManager.overlayChangeHandler = handler;
  }

  static setSwitchSyncHandler(handler: (() => void) | null): void {
    PerformanceDokitViewManager.switchSyncHandler = handler;
    hilog.info(PerformanceDokitViewManager.DOMAIN, PerformanceDokitViewManager.TAG,
      'DoKitH5|PerfSwitch setSwitchSyncHandler hasHandler=%{public}s',
      handler !== null ? '1' : '0');
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

  static isNetworkOverlayOpen(): boolean {
    return PerformanceDokitViewManager.isOverlayOpen(DataSourceFactory.TYPE_NETWORK);
  }

  static isOverlayOpen(performanceType: number): boolean {
    return PerformanceDokitViewManager.overlayOpenMap.get(performanceType) ?? false;
  }

  static open(performanceType: number, _title: string, listener: PerformanceFragmentCloseListener): void {
    PerformanceDokitViewManager.overlayOpenMap.set(performanceType, true);
    PerformanceDokitViewManager.closeListenerMap.set(performanceType, listener);
    PerformanceDokitViewManager.logSwitchState(`open type=${performanceType}`);
    PerformanceDokitViewManager.ensureFloat();
    DokitMemoryConfig.publishSwitchToStorage();
    PerformanceDokitViewManager.notifyChange();
  }

  static close(performanceType: number, _title: string): void {
    PerformanceDokitViewManager.overlayOpenMap.set(performanceType, false);
    if (!PerformanceDokitViewManager.anyOverlayOpen()) {
      DoKit.removeFloatingView(PerformanceDokitViewManager.FLOAT_NAME);
    }
    DokitMemoryConfig.publishSwitchToStorage();
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
    hilog.info(PerformanceDokitViewManager.DOMAIN, PerformanceDokitViewManager.TAG,
      'DoKitH5|PerfSwitch closeOverlayFromView type=%{public}d', performanceType);
    PerformanceDokitViewManager.stopMonitoring(performanceType);
    PerformanceDokitViewManager.close(performanceType, '');
    PerformanceDokitViewManager.publishAndNotifySwitchSync('closeOne');
  }

  /** 关闭整块性能色块：四项全部停掉并重置开关 */
  static closeAllOverlaysFromView(): void {
    hilog.info(PerformanceDokitViewManager.DOMAIN, PerformanceDokitViewManager.TAG,
      'DoKitH5|PerfSwitch closeAllOverlaysFromView begin');
    PerformanceDokitViewManager.logSwitchState('closeAll.before');
    PerformanceDokitViewManager.stopMonitoring(DataSourceFactory.TYPE_FPS);
    PerformanceDokitViewManager.stopMonitoring(DataSourceFactory.TYPE_CPU);
    PerformanceDokitViewManager.stopMonitoring(DataSourceFactory.TYPE_RAM);
    PerformanceDokitViewManager.stopMonitoring(DataSourceFactory.TYPE_NETWORK);
    PerformanceDokitViewManager.overlayOpenMap.set(DataSourceFactory.TYPE_FPS, false);
    PerformanceDokitViewManager.overlayOpenMap.set(DataSourceFactory.TYPE_CPU, false);
    PerformanceDokitViewManager.overlayOpenMap.set(DataSourceFactory.TYPE_RAM, false);
    PerformanceDokitViewManager.overlayOpenMap.set(DataSourceFactory.TYPE_NETWORK, false);
    DoKit.removeFloatingView(PerformanceDokitViewManager.FLOAT_NAME);
    PerformanceDokitViewManager.notifyChange();
    PerformanceDokitViewManager.publishAndNotifySwitchSync('closeAll');
    PerformanceDokitViewManager.logSwitchState('closeAll.after');
  }

  static closeFpsOverlayFromView(): void {
    PerformanceDokitViewManager.closeOverlayFromView(DataSourceFactory.TYPE_FPS);
  }

  private static stopMonitoring(performanceType: number): void {
    if (performanceType === DataSourceFactory.TYPE_FPS) {
      DokitMemoryConfig.FPS_STATUS = false;
      PerformanceDataManager.getInstance().stopMonitorFrameInfo();
      return;
    }
    if (performanceType === DataSourceFactory.TYPE_CPU) {
      DokitMemoryConfig.CPU_STATUS = false;
      PerformanceDataManager.getInstance().stopMonitorCPUInfo();
      return;
    }
    if (performanceType === DataSourceFactory.TYPE_RAM) {
      DokitMemoryConfig.RAM_STATUS = false;
      PerformanceDataManager.getInstance().stopMonitorMemoryInfo();
      return;
    }
    if (performanceType === DataSourceFactory.TYPE_NETWORK) {
      DokitMemoryConfig.NETWORK_STATUS = false;
      PerformanceDataManager.getInstance().stopMonitorNetFlowInfo();
      NetworkManager.get().stopMonitor();
    }
  }

  private static publishAndNotifySwitchSync(from: string): void {
    DokitMemoryConfig.publishSwitchToStorage();
    hilog.info(PerformanceDokitViewManager.DOMAIN, PerformanceDokitViewManager.TAG,
      'DoKitH5|PerfSwitch publishStorage from=%{public}s hasHandler=%{public}s listenerSize=%{public}d',
      from,
      PerformanceDokitViewManager.switchSyncHandler !== null ? '1' : '0',
      PerformanceDokitViewManager.closeListenerMap.size);
    // 只回调一次，避免多次 sync 把 epoch 连加、Toggle 重建异常
    if (PerformanceDokitViewManager.switchSyncHandler !== null) {
      PerformanceDokitViewManager.switchSyncHandler();
    }
  }

  static logSwitchState(tag: string): void {
    hilog.info(PerformanceDokitViewManager.DOMAIN, PerformanceDokitViewManager.TAG,
      'DoKitH5|PerfSwitch %{public}s cfgFps=%{public}s cfgCpu=%{public}s cfgRam=%{public}s cfgNet=%{public}s overlayFps=%{public}s overlayCpu=%{public}s overlayRam=%{public}s overlayNet=%{public}s',
      tag,
      DokitMemoryConfig.FPS_STATUS ? '1' : '0',
      DokitMemoryConfig.CPU_STATUS ? '1' : '0',
      DokitMemoryConfig.RAM_STATUS ? '1' : '0',
      DokitMemoryConfig.NETWORK_STATUS ? '1' : '0',
      PerformanceDokitViewManager.isFpsOverlayOpen() ? '1' : '0',
      PerformanceDokitViewManager.isCpuOverlayOpen() ? '1' : '0',
      PerformanceDokitViewManager.isRamOverlayOpen() ? '1' : '0',
      PerformanceDokitViewManager.isNetworkOverlayOpen() ? '1' : '0');
  }

  private static anyOverlayOpen(): boolean {
    return PerformanceDokitViewManager.isOverlayOpen(DataSourceFactory.TYPE_FPS) ||
      PerformanceDokitViewManager.isOverlayOpen(DataSourceFactory.TYPE_CPU) ||
      PerformanceDokitViewManager.isOverlayOpen(DataSourceFactory.TYPE_RAM) ||
      PerformanceDokitViewManager.isOverlayOpen(DataSourceFactory.TYPE_NETWORK);
  }

  /** 四项里第一次打开才挂上。之后只改开关，曲线不重建。 */
  private static ensureFloat(): void {
    setPerformanceFloatClose((): void => {
      PerformanceDokitViewManager.closeAllOverlaysFromView();
    });
    if (DoKit.getViewConfigByName(PerformanceDokitViewManager.FLOAT_NAME) !== undefined) {
      return;
    }
    const options = new DoKitFloatOptions();
    options.draggable = false;
    options.abovePage = true;
    options.abovePanel = true;
    options.passThrough = true;
    DoKit.launchFloatingView(
      PerformanceDokitViewManager.FLOAT_NAME,
      PERFORMANCE_FLOAT_BUILDER,
      undefined,
      options
    );
  }

  private static notifyChange(): void {
    if (PerformanceDokitViewManager.overlayChangeHandler !== null) {
      PerformanceDokitViewManager.overlayChangeHandler();
    }
  }
}
