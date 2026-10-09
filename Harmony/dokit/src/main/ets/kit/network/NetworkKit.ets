import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../AbstractKit';
import { PerformanceDataManager } from '../performance/PerformanceDataManager';
import { NetWorkMonitorFragmentMeta } from './NetWorkMonitorFragment';

/**
 * 网络监测 Kit，对标 Android NetworkKit
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

  onClickWithReturn(context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(NetWorkMonitorFragmentMeta.CLASS_NAME, context, undefined, true);
    return true;
  }
}
