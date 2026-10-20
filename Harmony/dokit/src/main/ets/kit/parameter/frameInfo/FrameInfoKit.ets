import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../../AbstractKit';
import { PerformanceDataManager } from '../../performance/PerformanceDataManager';
import { PerformanceMonitorFragmentMeta } from '../../performance/PerformanceMonitorFragment';

/**
 * 帧率检测 Kit：入口打开统一性能监测页
 */
export class FrameInfoKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_performance_ck_fps';
  }

  getName(): string {
    return '帧率';
  }

  getIcon(): Resource {
    return $r('app.media.dk_frame_hist');
  }

  onAppInit(context: common.UIAbilityContext): void {
    PerformanceDataManager.getInstance().init(context);
  }

  onClickWithReturn(context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(PerformanceMonitorFragmentMeta.CLASS_NAME, context, undefined, true);
    return true;
  }
}
