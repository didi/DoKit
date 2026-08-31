import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../AbstractKit';
import { BlockMonitorFragmentMeta } from './BlockMonitorFragment';

/**
 * 卡顿检测 Kit，对标 Android BlockMonitorKit
 */
export class BlockMonitorKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_performance_ck_block';
  }

  getName(): string {
    return '卡顿';
  }

  getIcon(): Resource {
    return $r('app.media.dk_block_monitor');
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(BlockMonitorFragmentMeta.CLASS_NAME);
    return true;
  }
}
