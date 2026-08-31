import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../AbstractKit';
import { DoKitSystemUtil } from '../../util/DoKitSystemUtil';

/**
 * 进入本地语言设置页面，对标 Android LocalLangKit
 */
export class LocalLangKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_comm_ck_local_lang';
  }

  getName(): string {
    return '本地语言';
  }

  getIcon(): Resource {
    return $r('app.media.dk_kit_local_lang');
  }

  onClickWithReturn(context: common.UIAbilityContext): boolean {
    DoKitSystemUtil.startLocalActivity(context);
    return true;
  }
}
