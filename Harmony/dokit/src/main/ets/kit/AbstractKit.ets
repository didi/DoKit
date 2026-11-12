import { common } from '@kit.AbilityKit';
import { Category } from './Category';
import { DoKitParams } from '../core/CustomViewRegistry';
import { DoKitLauncher } from '../core/DoKitLauncher';
import { IKit } from './IKit';

/**
 * 工具面板里一项工具的基类，对标 Android AbstractKit。
 * 业务工具继承后实现名称、图标和点击逻辑。
 * 内置工具还要重写 isInnerKit 和 innerKitId，且 innerKitId 与 dokit_system_kits.json 中的一致，否则面板里点不开。
 */
export abstract class AbstractKit implements IKit {
  /** 工具分类，对标 Android Category。已废弃，改不改都不影响面板展示。 */
  category: number = Category.DEFAULT;
  /** 是否出现在工具面板。登记业务 Kit 时写入 KitItem.checked。 */
  canShow: boolean = true;

  /** 是否为内置工具。业务工具保持 false。 */
  get isInnerKit(): boolean {
    return false;
  }

  /**
   * 工具 id。
   * 内置工具必须与 dokit_system_kits.json 的 innerKitId 一致。
   * 业务工具可不重写，登记时按名称生成。
   */
  innerKitId(): string {
    return '';
  }

  /** 工具面板上显示的名称。 */
  abstract getName(): string;

  /** 工具面板上显示的图标。 */
  abstract getIcon(): Resource;

  /** DoKit 安装时调用，用来做一次性初始化。 */
  onAppInit(_context: common.UIAbilityContext): void {
  }

  /**
   * 点击工具后的回调。
   * 返回 true 时关掉工具面板，返回 false 时面板留着。
   */
  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    return true;
  }
}
