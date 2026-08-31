import { common } from '@kit.AbilityKit';
import { AbstractKit } from '../AbstractKit';
import { FileExplorerFragment } from './FileExplorerPage';

/**
 * 沙盒浏览 Kit，对标 Android FileExplorerKit
 */
export class FileExplorerKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_comm_ck_sandbox';
  }

  getName(): string {
    return '沙盒浏览';
  }

  getIcon(): Resource {
    return $r('app.media.dk_file_explorer');
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(FileExplorerFragment.CLASS_NAME);
    return true;
  }
}
