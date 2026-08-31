import { displaySync } from '@kit.ArkGraphics2D';
import { PerformanceDataManager } from './PerformanceDataManager';

/**
 * 帧回调采集器，对标 Android Choreographer.FrameCallback
 * 通过 displaySync 订阅每帧事件并上报 PerformanceDataManager
 */
@Component
export struct FpsFrameCollector {
  private backDisplaySync: displaySync.DisplaySync | null = null;
  private readonly frameCallback = (_info: displaySync.IntervalInfo) => {
    PerformanceDataManager.getInstance().recordFrame();
  };

  aboutToAppear(): void {
    try {
      this.backDisplaySync = displaySync.create();
      this.backDisplaySync.on('frame', this.frameCallback);
      this.backDisplaySync.start();
    } catch (_err) {
      // displaySync 不可用时跳过，FPS 统计将为 0
    }
  }

  aboutToDisappear(): void {
    if (this.backDisplaySync !== null) {
      this.backDisplaySync.off('frame', this.frameCallback);
      this.backDisplaySync.stop();
      this.backDisplaySync = null;
    }
  }

  build() {
    Row()
      .width(0)
      .height(0)
      .opacity(0)
  }
}
