import { CustomViewBuilder, DoKitParams, CloseRegistrar } from '../../core/CustomViewRegistry';
import { DokitMemoryConfig } from '../../config/DokitMemoryConfig';
import { DoKitWindowUtil } from '../../util/DoKitWindowUtil';
import { FpsFrameCollector } from './FpsFrameCollector';
import { PerformanceDoKitView } from './PerformanceDoKitView';

let onPerformanceClose: () => void = (): void => {
};

/** 曲线关闭按钮回调。由 PerformanceDokitViewManager 在打开前设上。 */
export function setPerformanceFloatClose(handler: () => void): void {
  onPerformanceClose = handler;
}

@Component
struct PerformanceFloatLayer {
  @StorageLink(DokitMemoryConfig.STORE_FPS) private showFps: boolean = false;
  @StorageLink(DokitMemoryConfig.STORE_CPU) private showCpu: boolean = false;
  @StorageLink(DokitMemoryConfig.STORE_RAM) private showRam: boolean = false;
  @StorageLink(DokitMemoryConfig.STORE_NETWORK) private showNetwork: boolean = false;
  @StorageProp(DoKitWindowUtil.TOP_RECT_HEIGHT) private topRectPx: number = 0;
  @State private stageH: number = 0;

  private anySeries(): boolean {
    return this.showFps || this.showCpu || this.showRam || this.showNetwork;
  }

  build() {
    Stack({ alignContent: Alignment.TopStart }) {
      if (this.showFps) {
        FpsFrameCollector()
      }
      if (this.anySeries()) {
        PerformanceDoKitView({
          canDrag: false,
          showFps: this.showFps,
          showCpu: this.showCpu,
          showRam: this.showRam,
          showNetwork: this.showNetwork,
          topOffset: DoKitWindowUtil.topInsetVp(this.topRectPx),
          containerHeight: this.stageH,
          onClose: (): void => {
            onPerformanceClose();
          }
        })
      }
    }
    .width('100%')
    .height('100%')
    .hitTestBehavior(HitTestMode.Transparent)
    .onAreaChange((_oldValue: Area, area: Area) => {
      const height = Number(area.height);
      if (height > 1 && height !== this.stageH) {
        this.stageH = height;
      }
    })
  }
}

@Builder
function renderPerformanceFloat(_params: DoKitParams, _register: CloseRegistrar) {
  PerformanceFloatLayer()
}

export const PERFORMANCE_FLOAT_BUILDER: CustomViewBuilder = wrapBuilder(renderPerformanceFloat);
