import { common } from '@kit.AbilityKit';
import { ActiveToolPage } from '../../model/ActiveToolPage';
import { AbstractKit } from '../AbstractKit';

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

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(ActiveToolPage.DATA_CLEAN);
    return true;
  }
}
