import promptAction from '@ohos.promptAction';
import { DoKitBundle, FragmentCloseHandler } from '../../../core/FragmentRenderer';
import { DokitMemoryConfig } from '../../../config/DokitMemoryConfig';
import { DataSourceFactory } from '../../performance/datasource/DataSourceFactory';
import { PerformanceDataManager } from '../../performance/PerformanceDataManager';
import { PerformanceDokitViewManager } from '../../performance/PerformanceDokitViewManager';
import { SettingItem } from '../../../core/SettingItem';

class CpuStrings {
  static readonly TITLE: string = 'CPU';
  static readonly SWITCH: string = 'CPU检测开关';
  static readonly CACHE_LOG: string = '查看缓存日志';
}

/** 对标 Android CpuMainPageFragment */
export class CpuMainPageFragmentMeta {
  static readonly CLASS_NAME: string = 'CpuMainPageFragment';
}

/**
 * CPU 检测设置页，对标 Android CpuMainPageFragment
 */
@Component
export struct CpuMainPageFragment {
  onClose: () => void = () => {};
  @State private monitorOpen: boolean = DokitMemoryConfig.CPU_STATUS;
  private readonly closeListener = (performanceType: number) => {
    this.onPerformanceOverlayClose(performanceType);
  };

  aboutToAppear(): void {
    this.monitorOpen = DokitMemoryConfig.CPU_STATUS;
  }

  aboutToDisappear(): void {
    PerformanceDokitViewManager.onPerformanceSettingFragmentDestroy(this.closeListener);
  }

  private onPerformanceOverlayClose(performanceType: number): void {
    if (performanceType !== DataSourceFactory.TYPE_CPU) {
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
    DokitMemoryConfig.CPU_STATUS = isOn;
    if (isOn) {
      this.startMonitor();
    } else {
      this.stopMonitor();
    }
  }

  private startMonitor(): void {
    PerformanceDataManager.getInstance().startMonitorCPUInfo();
    PerformanceDokitViewManager.open(DataSourceFactory.TYPE_CPU, CpuStrings.TITLE, this.closeListener);
  }

  private stopMonitor(): void {
    PerformanceDataManager.getInstance().stopMonitorCPUInfo();
    PerformanceDokitViewManager.close(DataSourceFactory.TYPE_CPU, CpuStrings.TITLE);
  }

  private openCacheLog(): void {
    promptAction.showToast({ message: '缓存日志功能暂未实现', duration: 2000 });
  }

  @Builder
  private buildClickRow(item: SettingItem, onClick: () => void) {
    Row() {
      Text(item.desc)
        .fontSize(16)
        .fontColor('#333333')
        .layoutWeight(1)
      Text('›')
        .fontSize(20)
        .fontColor('#CCCCCC')
    }
    .width('100%')
    .height(52)
    .padding({ left: 16, right: 16 })
    .backgroundColor(Color.White)
    .onClick(() => {
      onClick();
    })
  }

  build() {
    Column() {
      Row() {
        Text(CpuStrings.TITLE)
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
        Text(CpuStrings.SWITCH)
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

      Divider().color('#EEEEEE').margin({ left: 16 })

      this.buildClickRow(new SettingItem(CpuStrings.CACHE_LOG), () => {
        this.openCacheLog();
      })
    }
    .width('100%')
    .height('100%')
    .backgroundColor(Color.White)
  }
}

@Builder
export function renderCpuMainPageFragment(_bundle: DoKitBundle, onClose: FragmentCloseHandler) {
  CpuMainPageFragment({ onClose });
}
