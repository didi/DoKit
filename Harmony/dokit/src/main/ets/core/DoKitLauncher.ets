import { DoKitBundle } from './FragmentRenderer';
import { DoKitIntent, DoKitViewLaunchMode } from './DoKitIntent';
import { DoKitViewManager } from './DoKitViewManager';
import { SystemFragmentRegistry } from './SystemFragmentRigistry';

/**
 * 悬浮窗和全屏启动器，对标 Android kit/core/SimpleDoKitLauncher
 */
export class DoKitLauncher {
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
    SystemFragmentRegistry.showContent(targetClass, bundle, isSystemFragment);
  }

  static finishFullScreen(): void {
    SystemFragmentRegistry.finish();
  }
}
