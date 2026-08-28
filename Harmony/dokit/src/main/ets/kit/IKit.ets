import { common } from '@kit.AbilityKit';

/**
 * 工具入口接口，对标 Android IKit
 * 业务 Kit 请继承 AbstractKit
 */
export interface IKit {
  category: number;
  canShow: boolean;
  isInnerKit: boolean;
  innerKitId(): string;
  getName(): string;
  getIcon(): Resource;
  onAppInit(context: common.UIAbilityContext): void;
  onClickWithReturn(context: common.UIAbilityContext): boolean;
}
