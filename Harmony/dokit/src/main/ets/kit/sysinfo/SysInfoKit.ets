import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../AbstractKit';
import { SysInfoFragment } from './SysInfoPage';

/**
 * App 信息 Kit，对标 Android SysInfoKit
 */
export class SysInfoKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_comm_ck_appinfo';
  }

  getName(): string {
    return 'App信息';
  }

  getIcon(): Resource {
    return $r('app.media.dk_sys_info');
  }

  onClickWithReturn(context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(SysInfoFragment.CLASS_NAME, context, undefined, true);
    return true;
  }
}
