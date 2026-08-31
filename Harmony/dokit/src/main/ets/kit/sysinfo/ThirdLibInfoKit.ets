import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../AbstractKit';
import { ThirdLibInfoFragment } from './ThirdLibInfoPage';

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

  onClickWithReturn(context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(ThirdLibInfoFragment.CLASS_NAME, context, undefined, true);
    return true;
  }
}
