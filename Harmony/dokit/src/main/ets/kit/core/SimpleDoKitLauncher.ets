import { DoKitBundle } from './FragmentRenderer';
import { DoKitIntent, DoKitViewLaunchMode } from './DoKitIntent';
import { DoKitViewManager } from './DoKitViewManager';
import { UniversalActivity } from './UniversalActivity';

/**
 * 悬浮窗和全屏启动器，对标 Android kit/core/SimpleDoKitLauncher
 */
export class SimpleDoKitLauncher {
  static launchFloating(
    targetClass: string,
    mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE,
    bundle?: DoKitBundle
  ): void {
    const doKitIntent = new DoKitIntent(targetClass);
    doKitIntent.mode = mode;
    doKitIntent.bundle = bundle ?? {};
    DoKitViewManager.attach(doKitIntent);
  }

  static removeFloating(targetClass: string): void {
    DoKitViewManager.detach(targetClass);
  }

  static launchFullScreen(
    targetClass: string,
    bundle?: DoKitBundle,
    isSystemFragment: boolean = false
  ): void {
    UniversalActivity.showContent(targetClass, bundle, isSystemFragment);
  }

  static finishFullScreen(): void {
    UniversalActivity.finish();
  }
}
