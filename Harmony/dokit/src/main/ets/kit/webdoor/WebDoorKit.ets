import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../AbstractKit';
import { WebDoorFragment } from './WebDoorPage';

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

  onClickWithReturn(context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(WebDoorFragment.CLASS_NAME, context, undefined, true);
    return true;
  }
}
