import { common } from '@kit.AbilityKit';
import { ActiveToolPage } from '../../model/ActiveToolPage';
import { AbstractKit } from '../AbstractKit';
import { CrashCaptureConfig } from '../../config/CrashCaptureConfig';
import { CrashCaptureManager } from './CrashCaptureManager';

/**
 * Crash 捕获 Kit，对标 Android CrashCaptureKit
 */
export class CrashCaptureKit extends AbstractKit {
  get isInnerKit(): boolean {
    return true;
  }

  innerKitId(): string {
    return 'dokit_sdk_comm_ck_crash';
  }

  getName(): string {
    return 'Crash';
  }

  getIcon(): Resource {
    return $r('app.media.dk_crash_catch');
  }

  onAppInit(context: common.UIAbilityContext): void {
    CrashCaptureManager.getInstance().init(context);
    if (CrashCaptureConfig.isCrashCaptureOpen()) {
      CrashCaptureManager.getInstance().start();
    } else {
      CrashCaptureManager.getInstance().stop();
    }
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    this.startUniversalActivity(ActiveToolPage.CRASH_CAPTURE);
    return true;
  }
}
