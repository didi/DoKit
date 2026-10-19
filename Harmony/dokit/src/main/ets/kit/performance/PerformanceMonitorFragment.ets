import { hilog } from '@kit.PerformanceAnalysisKit';
import { DoKitBundle, FragmentCloseHandler } from '../../core/FragmentRenderer';
import { DokitMemoryConfig } from '../../config/DokitMemoryConfig';
import { DataSourceFactory } from './datasource/DataSourceFactory';
import { PerformanceDataManager } from './PerformanceDataManager';
import { PerformanceDokitViewManager } from './PerformanceDokitViewManager';
import { NetworkManager } from '../network/NetworkManager';

/**
 * 性能监测统一设置页：帧率 / CPU / 内存 / 流量 四个开关放在同一页
 * 四个 Kit 入口均打开本页
 */
export class PerformanceMonitorFragmentMeta {
  static readonly CLASS_NAME: string = 'PerformanceMonitorFragment';
}

@Component
export struct PerformanceMonitorFragment {
  private static readonly DOMAIN: number = 0xD0F1;
  private static readonly TAG: string = 'DoKitH5';

  onClose: () => void = () => {};
  @StorageLink('DoKitH5_PerfFpsOpen') fpsOpen: boolean = false;
  @StorageLink('DoKitH5_PerfCpuOpen') cpuOpen: boolean = false;
  @StorageLink('DoKitH5_PerfRamOpen') ramOpen: boolean = false;
  @StorageLink('DoKitH5_PerfNetworkOpen') networkOpen: boolean = false;
  /** 整块开关区 remount，规避 Toggle 外部改 isOn 不刷新 */
  @State private switchEpoch: number = 0;
  private ignoreToggleChange: boolean = false;

  aboutToAppear(): void {
    DokitMemoryConfig.publishSwitchToStorage();
    this.syncSwitchesFromConfig('aboutToAppear');
    PerformanceDokitViewManager.setSwitchSyncHandler(() => {
      this.syncSwitchesFromConfig('switchSyncHandler');
    });
  }

  aboutToDisappear(): void {
    hilog.info(PerformanceMonitorFragment.DOMAIN, PerformanceMonitorFragment.TAG,
      'DoKitH5|PerfSwitch fragment aboutToDisappear clearHandler');
    PerformanceDokitViewManager.setSwitchSyncHandler(null);
  }

  private syncSwitchesFromConfig(from: string): void {
    this.ignoreToggleChange = true;
    this.fpsOpen = DokitMemoryConfig.FPS_STATUS;
    this.cpuOpen = DokitMemoryConfig.CPU_STATUS;
    this.ramOpen = DokitMemoryConfig.RAM_STATUS;
    this.networkOpen = DokitMemoryConfig.NETWORK_STATUS;
    DokitMemoryConfig.publishSwitchToStorage();
    this.switchEpoch = this.switchEpoch + 1;
    hilog.info(PerformanceMonitorFragment.DOMAIN, PerformanceMonitorFragment.TAG,
      'DoKitH5|PerfSwitch sync from=%{public}s uiFps=%{public}s uiCpu=%{public}s uiRam=%{public}s uiNet=%{public}s epoch=%{public}d',
      from,
      this.fpsOpen ? '1' : '0',
      this.cpuOpen ? '1' : '0',
      this.ramOpen ? '1' : '0',
      this.networkOpen ? '1' : '0',
      this.switchEpoch);
    PerformanceDokitViewManager.logSwitchState(`fragment.${from}`);
    setTimeout(() => {
      this.ignoreToggleChange = false;
    }, 50);
  }

  private startFps(): void {
    DokitMemoryConfig.FPS_STATUS = true;
    this.fpsOpen = true;
    DokitMemoryConfig.publishSwitchToStorage();
    PerformanceDataManager.getInstance().startMonitorFrameInfo();
    PerformanceDokitViewManager.open(DataSourceFactory.TYPE_FPS, '帧率', () => {
    });
  }

  private stopFps(): void {
    DokitMemoryConfig.FPS_STATUS = false;
    this.fpsOpen = false;
    DokitMemoryConfig.publishSwitchToStorage();
    PerformanceDataManager.getInstance().stopMonitorFrameInfo();
    PerformanceDokitViewManager.close(DataSourceFactory.TYPE_FPS, '帧率');
  }

  private startCpu(): void {
    DokitMemoryConfig.CPU_STATUS = true;
    this.cpuOpen = true;
    DokitMemoryConfig.publishSwitchToStorage();
    PerformanceDataManager.getInstance().startMonitorCPUInfo();
    PerformanceDokitViewManager.open(DataSourceFactory.TYPE_CPU, 'CPU', () => {
    });
  }

  private stopCpu(): void {
    DokitMemoryConfig.CPU_STATUS = false;
    this.cpuOpen = false;
    DokitMemoryConfig.publishSwitchToStorage();
    PerformanceDataManager.getInstance().stopMonitorCPUInfo();
    PerformanceDokitViewManager.close(DataSourceFactory.TYPE_CPU, 'CPU');
  }

  private startRam(): void {
    DokitMemoryConfig.RAM_STATUS = true;
    this.ramOpen = true;
    DokitMemoryConfig.publishSwitchToStorage();
    PerformanceDataManager.getInstance().startMonitorMemoryInfo();
    PerformanceDokitViewManager.open(DataSourceFactory.TYPE_RAM, '内存', () => {
    });
  }

  private stopRam(): void {
    DokitMemoryConfig.RAM_STATUS = false;
    this.ramOpen = false;
    DokitMemoryConfig.publishSwitchToStorage();
    PerformanceDataManager.getInstance().stopMonitorMemoryInfo();
    PerformanceDokitViewManager.close(DataSourceFactory.TYPE_RAM, '内存');
  }

  private startNetwork(): void {
    DokitMemoryConfig.NETWORK_STATUS = true;
    this.networkOpen = true;
    DokitMemoryConfig.publishSwitchToStorage();
    NetworkManager.get().startMonitor();
    PerformanceDataManager.getInstance().startMonitorNetFlowInfo();
    PerformanceDokitViewManager.open(DataSourceFactory.TYPE_NETWORK, '流量', () => {
    });
  }

  private stopNetwork(): void {
    DokitMemoryConfig.NETWORK_STATUS = false;
    this.networkOpen = false;
    DokitMemoryConfig.publishSwitchToStorage();
    PerformanceDataManager.getInstance().stopMonitorNetFlowInfo();
    NetworkManager.get().stopMonitor();
    PerformanceDokitViewManager.close(DataSourceFactory.TYPE_NETWORK, '流量');
  }

  private applyToggle(label: string, next: boolean, start: () => void, stop: () => void): void {
    if (this.ignoreToggleChange) {
      hilog.info(PerformanceMonitorFragment.DOMAIN, PerformanceMonitorFragment.TAG,
        'DoKitH5|PerfSwitch toggleIgnore label=%{public}s next=%{public}s', label, next ? '1' : '0');
      return;
    }
    hilog.info(PerformanceMonitorFragment.DOMAIN, PerformanceMonitorFragment.TAG,
      'DoKitH5|PerfSwitch toggleChange label=%{public}s next=%{public}s', label, next ? '1' : '0');
    if (next) {
      start();
    } else {
      stop();
    }
  }

  @Builder
  private SwitchBlock() {
    Column() {
      Row() {
        Text('帧率检测开关')
          .fontSize(16)
          .fontColor('#333333')
          .layoutWeight(1)
        Toggle({ type: ToggleType.Switch, isOn: $$this.fpsOpen })
          .selectedColor('#0070BB')
          .onChange((next: boolean) => {
            this.applyToggle('帧率检测开关', next, () => {
              this.startFps();
            }, () => {
              this.stopFps();
            });
          })
      }
      .width('100%')
      .height(52)
      .padding({ left: 16, right: 16 })
      .backgroundColor(Color.White)

      Divider().color('#EEEEEE').margin({ left: 16 })

      Row() {
        Text('CPU检测开关')
          .fontSize(16)
          .fontColor('#333333')
          .layoutWeight(1)
        Toggle({ type: ToggleType.Switch, isOn: $$this.cpuOpen })
          .selectedColor('#0070BB')
          .onChange((next: boolean) => {
            this.applyToggle('CPU检测开关', next, () => {
              this.startCpu();
            }, () => {
              this.stopCpu();
            });
          })
      }
      .width('100%')
      .height(52)
      .padding({ left: 16, right: 16 })
      .backgroundColor(Color.White)

      Divider().color('#EEEEEE').margin({ left: 16 })

      Row() {
        Text('内存检测开关')
          .fontSize(16)
          .fontColor('#333333')
          .layoutWeight(1)
        Toggle({ type: ToggleType.Switch, isOn: $$this.ramOpen })
          .selectedColor('#0070BB')
          .onChange((next: boolean) => {
            this.applyToggle('内存检测开关', next, () => {
              this.startRam();
            }, () => {
              this.stopRam();
            });
          })
      }
      .width('100%')
      .height(52)
      .padding({ left: 16, right: 16 })
      .backgroundColor(Color.White)

      Divider().color('#EEEEEE').margin({ left: 16 })

      Row() {
        Text('流量检测开关')
          .fontSize(16)
          .fontColor('#333333')
          .layoutWeight(1)
        Toggle({ type: ToggleType.Switch, isOn: $$this.networkOpen })
          .selectedColor('#0070BB')
          .onChange((next: boolean) => {
            this.applyToggle('流量检测开关', next, () => {
              this.startNetwork();
            }, () => {
              this.stopNetwork();
            });
          })
      }
      .width('100%')
      .height(52)
      .padding({ left: 16, right: 16 })
      .backgroundColor(Color.White)
    }
    .width('100%')
  }

  build() {
    Column() {
      Row() {
        Text('性能监测')
          .fontSize(18)
          .fontWeight(FontWeight.Medium)
          .fontColor('#111111')
          .layoutWeight(1)
          .textAlign(TextAlign.Center)

        Text('✕')
          .fontSize(20)
          .fontColor('#666666')
          .padding(8)
          .onClick(() => {
            this.onClose();
          })
      }
      .width('100%')
      .height(56)
      .padding({ left: 8, right: 8 })
      .backgroundColor(Color.White)

      // key 绑 epoch：关闭浮层后整块重建，Toggle 必跟状态
      Column() {
        this.SwitchBlock()
      }
      .width('100%')
      .key(`perf_switch_block_${this.switchEpoch}`)
    }
    .width('100%')
    .height('100%')
    .backgroundColor(Color.White)
  }
}

@Builder
export function renderPerformanceMonitorFragment(_bundle: DoKitBundle, onClose: FragmentCloseHandler) {
  PerformanceMonitorFragment({ onClose });
}
