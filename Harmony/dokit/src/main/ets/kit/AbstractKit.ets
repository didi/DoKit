import { common } from '@kit.AbilityKit';
import { ActiveToolPage } from '../model/ActiveToolPage';
import { Category } from './Category';
import { SimpleDoKitLauncher } from './core/SimpleDoKitLauncher';
import { IKit } from './IKit';

/**
 * 业务自定义 Kit 基类，对标 Android AbstractKit / IKit
 * 内置 Kit 必须重写 innerKitId，且与 dokit_system_kits.json 一致
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

  /**
   * 点击回调，对标 onClickWithReturn
   * @returns true 隐藏工具面板，false 保持面板
   */
  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    return true;
  }

  /**
   * 打开内置工具全屏页，对标 startUniversalActivity(BaseFragment)
   */
  protected startUniversalActivity(page: ActiveToolPage): void {
    SimpleDoKitLauncher.launchFullScreen(page);
  }
}
