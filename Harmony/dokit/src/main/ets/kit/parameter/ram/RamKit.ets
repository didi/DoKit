import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../../AbstractKit';
import { PerformanceMonitorFragmentMeta } from '../../performance/PerformanceMonitorFragment';

/**
 * 内存检测 Kit：入口打开统一性能监测页
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
    this.startUniversalActivity(PerformanceMonitorFragmentMeta.CLASS_NAME, context, undefined, true);
    return true;
  }
}
