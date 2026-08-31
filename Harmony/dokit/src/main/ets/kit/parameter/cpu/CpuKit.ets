import { common } from '@kit.AbilityKit';
import { ActiveToolPage } from '../../../model/ActiveToolPage';
import { AbstractKit } from '../../AbstractKit';

/**
 * CPU 检测 Kit，对标 Android CpuKit
 */
export class CpuKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_performance_ck_cpu';
  }

  getName(): string {
    return 'CPU';
  }

  getIcon(): Resource {
    return $r('app.media.dk_cpu');
  }

  onAppInit(_context: common.UIAbilityContext): void {
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(ActiveToolPage.CPU_INFO);
    return true;
  }
}
