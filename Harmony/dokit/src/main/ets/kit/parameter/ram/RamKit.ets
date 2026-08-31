import { common } from '@kit.AbilityKit';
import { ActiveToolPage } from '../../../model/ActiveToolPage';
import { AbstractKit } from '../../AbstractKit';

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

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(ActiveToolPage.RAM_INFO);
    return true;
  }
}
