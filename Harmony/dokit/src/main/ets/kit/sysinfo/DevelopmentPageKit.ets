import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../AbstractKit';
import { DoKitSystemUtil } from '../../util/DoKitSystemUtil';

/**
 * 开发者选项 Kit，对标 Android DevelopmentPageKit
 */
export class DevelopmentPageKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_comm_ck_devpage';
  }

  getName(): string {
    return '开发者选项';
  }

  getIcon(): Resource {
    return $r('app.media.dk_kit_devlop');
  }

  onClickWithReturn(context: common.UIAbilityContext): boolean {
    DoKitSystemUtil.startDevelopmentActivity(context);
    return true;
  }
}
