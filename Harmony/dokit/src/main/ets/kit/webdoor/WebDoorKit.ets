import { common } from '@kit.AbilityKit';
import { ActiveToolPage } from '../../model/ActiveToolPage';
import { AbstractKit } from '../AbstractKit';

/**
 * H5 任意门 Kit，对标 Android WebDoorKit
 */
export class WebDoorKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_comm_ck_h5';
  }

  getName(): string {
    return 'H5任意门';
  }

  getIcon(): Resource {
    return $r('app.media.dk_web_door');
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(ActiveToolPage.WEB_DOOR);
    return true;
  }
}
