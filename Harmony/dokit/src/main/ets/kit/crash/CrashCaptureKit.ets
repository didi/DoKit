import { common } from '@kit.AbilityKit';
import { CrashCaptureConfig } from '../../config/CrashCaptureConfig';
import { DoKit } from '../../DoKit';
import { AbstractKit } from '../AbstractKit';
import { CrashCaptureMainFragmentMeta, renderCrashCaptureMainFragment } from './CrashCaptureMainFragment';
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
    DoKit.launchFullScreen(
      CrashCaptureMainFragmentMeta.CLASS_NAME,
      wrapBuilder(renderCrashCaptureMainFragment),
      undefined,
      true
    );
    return true;
  }
}
