import { common } from '@kit.AbilityKit';
import { Category } from './Category';

/**
 * 业务自定义 Kit 基类，对标 Android AbstractKit / IKit
 * 外部 Kit 无需实现 innerKitId，isInnerKit 默认为 false
 */
export abstract class AbstractKit {
  /** 对标 Android category */
  category: number = Category.DEFAULT;

  /** 是否在面板展示，对标 Android canShow */
  canShow: boolean = true;

  get isInnerKit(): boolean {
    return false;
  }

  /** 业务 Kit 可选实现，用于埋点或去重；默认空 */
  innerKitId(): string {
    return '';
  }

  abstract getName(): string;

  abstract getIcon(): Resource;

  /** app 初始化时调用，对标 onAppInit */
  onAppInit(_context: common.UIAbilityContext): void {
  }

  /**
   * 点击回调，对标 onClickWithReturn
   * @returns true 隐藏工具面板，false 保持面板
   */
  onClickWithReturn(_context: common.UIAbilityContext): boolean {
    return true;
  }
}
