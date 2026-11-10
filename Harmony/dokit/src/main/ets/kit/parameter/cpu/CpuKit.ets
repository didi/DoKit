import { common } from '@kit.AbilityKit';
import { DoKit } from '../../../DoKit';
import { AbstractKit } from '../../AbstractKit';
import { PerformanceMonitorFragmentMeta, renderPerformanceMonitorFragment } from '../../performance/PerformanceMonitorFragment';

/**
 * CPU 检测 Kit：入口打开统一性能监测页
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
    DoKit.launchFullScreen(
      PerformanceMonitorFragmentMeta.CLASS_NAME,
      wrapBuilder(renderPerformanceMonitorFragment),
      undefined,
      true
    );
    return true;
  }
}
