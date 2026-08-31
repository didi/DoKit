import { common } from '@kit.AbilityKit';
import { Category } from './Category';
import { DoKitBundle } from './core/FragmentRenderer';
import { SimpleDoKitLauncher } from './core/SimpleDoKitLauncher';
import { IKit } from './IKit';

/**
 * 业务自定义 Kit 基类，对标 Android AbstractKit / IKit
 */
export abstract class AbstractKit implements IKit {
  category: number = Category.DEFAULT;
  canShow: boolean = true;

  get isInnerKit(): boolean {
    return false;
  }

  innerKitId(): string {
    return '';
  }

  abstract getName(): string;

  abstract getIcon(): Resource;

  onAppInit(_context: common.UIAbilityContext): void {
  }

  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    return true;
  }

  /**
   * 启动 UniversalActivity，对标 startUniversalActivity(BaseFragment, context, bundle, isSystemFragment)
   */
  protected startUniversalActivity(
    fragmentClass: string,
    context?: common.UIAbilityContext,
    bundle?: DoKitBundle,
    isSystemFragment: boolean = false
  ): void {
    SimpleDoKitLauncher.launchFullScreen(fragmentClass, bundle, isSystemFragment);
  }
}
