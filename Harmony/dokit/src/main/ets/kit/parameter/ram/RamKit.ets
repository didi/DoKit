import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../../AbstractKit';
import { RamMainPageFragmentMeta } from './RamMainPageFragment';

/**
 * 内存检测 Kit，对标 Android RamKit
 */
export class RamKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_performance_ck_arm';
  }

  getName(): string {
    return '内存';
  }

  getIcon(): Resource {
    return $r('app.media.dk_ram');
  }

  onAppInit(_context: common.UIAbilityContext): void {
  }

  onClickWithReturn(context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(RamMainPageFragmentMeta.CLASS_NAME, context, undefined, true);
    return true;
  }
}
