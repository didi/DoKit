import { common } from '@kit.AbilityKit';
import { DoKit } from '../../DoKit';
import { AbstractKit } from '../AbstractKit';
import { renderThirdLibInfoFragment, ThirdLibInfoFragment } from './ThirdLibInfoPage';

/**
 * 三方库信息 Kit，对标 Android sysinfo/ThirdLibInfoKit
 */
export class ThirdLibInfoKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_comm_ck_thirdlibinfo';
  }

  getName(): string {
    return '三方库信息';
  }

  getIcon(): Resource {
    return $r('app.media.dk_icon_third_lib');
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    DoKit.launchFullScreen(
      ThirdLibInfoFragment.CLASS_NAME,
      wrapBuilder(renderThirdLibInfoFragment),
      undefined,
      true
    );
    return true;
  }
}
