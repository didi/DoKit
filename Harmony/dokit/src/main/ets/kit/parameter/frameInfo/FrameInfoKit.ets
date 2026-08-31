import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../../AbstractKit';
import { PerformanceDataManager } from '../../performance/PerformanceDataManager';
import { FrameInfoFragmentMeta } from './FrameInfoFragment';

/**
 * 帧率检测 Kit，对标 Android FrameInfoKit
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

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(FrameInfoFragmentMeta.CLASS_NAME);
    return true;
  }
}
