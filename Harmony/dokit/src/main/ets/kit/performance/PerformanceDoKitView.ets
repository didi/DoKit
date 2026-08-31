import { DataSourceFactory } from './datasource/DataSourceFactory';
import { PerformanceDataManager } from './PerformanceDataManager';

/**
 * 性能指标浮层，对标 Android PerformanceDoKitView（MVP 文字展示）
 */
@Component
export struct PerformanceDoKitView {
  performanceType: number = DataSourceFactory.TYPE_FPS;
  title: string = '帧率';
  unit: string = 'FPS';
  topOffset: number = 88;
  onClose: () => void = () => {};
  @State private metricValue: string = '0';
  private refreshTimer: number = -1;

  aboutToAppear(): void {
    this.refreshMetric();
    this.refreshTimer = setInterval(() => {
      this.refreshMetric();
    }, 500);
  }

  aboutToDisappear(): void {
    if (this.refreshTimer >= 0) {
      clearInterval(this.refreshTimer);
      this.refreshTimer = -1;
    }
  }

  private refreshMetric(): void {
    const manager = PerformanceDataManager.getInstance();
    if (this.performanceType === DataSourceFactory.TYPE_CPU) {
      this.metricValue = `${Math.round(manager.getLastCpuRate())}`;
      return;
    }
    if (this.performanceType === DataSourceFactory.TYPE_RAM) {
      this.metricValue = `${Math.round(manager.getLastMemoryInfo())}`;
      return;
    }
    this.metricValue = `${manager.getLastFrameRate()}`;
  }

  build() {
    Column() {
      Row() {
        Text(this.title)
          .fontSize(12)
          .fontColor('#FFFFFF')
        Text(this.metricValue)
          .fontSize(22)
          .fontWeight(FontWeight.Bold)
          .fontColor('#52C41A')
          .margin({ left: 8, right: 2 })
        Text(this.unit)
          .fontSize(12)
          .fontColor('#FFFFFF')
        Blank().layoutWeight(1)
        Text('✕')
          .fontSize(16)
          .fontColor('#CCCCCC')
          .padding({ left: 8, right: 4 })
          .onClick(() => {
            this.onClose();
          })
      }
      .width('100%')
      .alignItems(VerticalAlign.Center)
    }
    .width(160)
    .padding({ left: 12, right: 12, top: 8, bottom: 8 })
    .backgroundColor('#CC1A1A1A')
    .borderRadius(8)
    .position({ x: 16, y: this.topOffset })
    .hitTestBehavior(HitTestMode.Default)
  }
}
