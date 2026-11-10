import { common } from '@kit.AbilityKit';
import { DoKit } from '../../DoKit';
import { AbstractKit } from '../AbstractKit';
import { renderSysInfoFragment, SysInfoFragment } from './SysInfoPage';

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
    DoKit.launchFullScreen(
      SysInfoFragment.CLASS_NAME,
      wrapBuilder(renderSysInfoFragment),
      undefined,
      true
    );
    return true;
  }
}
