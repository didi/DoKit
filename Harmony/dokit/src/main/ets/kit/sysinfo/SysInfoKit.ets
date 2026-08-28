import { common } from '@kit.AbilityKit';
import { ActiveToolPage } from '../../model/ActiveToolPage';
import { AbstractKit } from '../AbstractKit';

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

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(ActiveToolPage.SYS_INFO);
    return true;
  }
}
