import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../AbstractKit';
import { DataCleanFragment } from './DataCleanPage';

/**
 * 清理缓存 Kit，对标 Android DataCleanKit
 */
export class DataCleanKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_comm_ck_cache';
  }

  getName(): string {
    return '清理缓存';
  }

  getIcon(): Resource {
    return $r('app.media.dk_data_clean');
  }

  onClickWithReturn(context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(DataCleanFragment.CLASS_NAME, context, undefined, true);
    return true;
  }
}
