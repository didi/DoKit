import { common } from '@kit.AbilityKit';
import { DoKit } from '../../DoKit';
import { AbstractKit } from '../AbstractKit';
import { PerformanceDataManager } from '../performance/PerformanceDataManager';
import { PerformanceMonitorFragmentMeta, renderPerformanceMonitorFragment } from '../performance/PerformanceMonitorFragment';

/**
 * 网络监测 Kit：入口打开统一性能监测页
 */
export class NetworkKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_performance_ck_network';
  }

  getName(): string {
    return '网络';
  }

  getIcon(): Resource {
    return $r('app.media.dk_net_monitor');
  }

  onAppInit(context: common.UIAbilityContext): void {
    PerformanceDataManager.getInstance().init(context);
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
