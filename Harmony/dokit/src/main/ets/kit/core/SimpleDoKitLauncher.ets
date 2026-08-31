import { DoKitBundle } from './FragmentRenderer';
import { UniversalActivity } from './UniversalActivity';

/**
 * 悬浮窗和全屏启动器，对标 Android kit/core/SimpleDoKitLauncher
 */
export class SimpleDoKitLauncher {
  static launchFullScreen(
    fragmentClass: string,
    bundle?: DoKitBundle,
    isSystemFragment: boolean = false
  ): void {
    UniversalActivity.showContent(fragmentClass, bundle, isSystemFragment);
  }

  static finishFullScreen(): void {
    UniversalActivity.finish();
  }
}
