import { DoKitBundle, FragmentCloseHandler } from '../../core/FragmentRenderer';
import { DokitMemoryConfig } from '../../config/DokitMemoryConfig';
import { DataSourceFactory } from '../performance/datasource/DataSourceFactory';
import { PerformanceDataManager } from '../performance/PerformanceDataManager';
import { PerformanceDokitViewManager } from '../performance/PerformanceDokitViewManager';
import { NetworkManager } from './NetworkManager';

class NetworkStrings {
  static readonly TITLE: string = '网络';
  static readonly SWITCH: string = '流量检测开关';
}

/** 对标 Android NetWorkMonitorFragment */
export class NetWorkMonitorFragmentMeta {
  static readonly CLASS_NAME: string = 'NetWorkMonitorFragment';
}

/**
 * 网络监测设置页，对标 Android NetWorkMonitorFragment（MVP：仅开关 + 流量浮层）
 */
@Component
export struct NetWorkMonitorFragment {
  onClose: () => void = () => {};
  @State private monitorOpen: boolean = NetworkManager.isActive();
  private readonly closeListener = (performanceType: number) => {
    this.onPerformanceOverlayClose(performanceType);
  };

  aboutToAppear(): void {
    this.monitorOpen = NetworkManager.isActive() || DokitMemoryConfig.NETWORK_STATUS;
  }

  aboutToDisappear(): void {
    PerformanceDokitViewManager.onPerformanceSettingFragmentDestroy(this.closeListener);
  }

  private onPerformanceOverlayClose(performanceType: number): void {
    if (performanceType !== DataSourceFactory.TYPE_NETWORK) {
      return;
    }
    if (!this.monitorOpen) {
      return;
    }
    this.monitorOpen = false;
    this.stopMonitor();
  }

  private onSwitchChange(isOn: boolean): void {
    this.monitorOpen = isOn;
    DokitMemoryConfig.NETWORK_STATUS = isOn;
    if (isOn) {
      this.startMonitor();
    } else {
      this.stopMonitor();
    }
  }

  private startMonitor(): void {
    NetworkManager.get().startMonitor();
    PerformanceDataManager.getInstance().startMonitorNetFlowInfo();
    PerformanceDokitViewManager.open(DataSourceFactory.TYPE_NETWORK, NetworkStrings.TITLE, this.closeListener);
  }

  private stopMonitor(): void {
    PerformanceDataManager.getInstance().stopMonitorNetFlowInfo();
    NetworkManager.get().stopMonitor();
    PerformanceDokitViewManager.close(DataSourceFactory.TYPE_NETWORK, NetworkStrings.TITLE);
  }

  build() {
    Column() {
      Row() {
        Text(NetworkStrings.TITLE)
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

      Row() {
        Text(NetworkStrings.SWITCH)
          .fontSize(16)
          .fontColor('#333333')
          .layoutWeight(1)

        Toggle({ type: ToggleType.Switch, isOn: this.monitorOpen })
          .selectedColor('#0070BB')
          .onChange((isOn: boolean) => {
            this.onSwitchChange(isOn);
          })
      }
      .width('100%')
      .height(52)
      .padding({ left: 16, right: 16 })
      .backgroundColor(Color.White)
    }
    .width('100%')
    .height('100%')
    .backgroundColor(Color.White)
  }
}

@Builder
export function renderNetWorkMonitorFragment(_bundle: DoKitBundle, onClose: FragmentCloseHandler) {
  NetWorkMonitorFragment({ onClose });
}
