import { common } from '@kit.AbilityKit';

/**
 * 工具面板里一项工具的接口，对标 Android IKit。
 * 业务工具请继承 AbstractKit，不要直接实现本接口。
 */
export interface IKit {
  /** 工具分类，对标 Android Category。已废弃，改不改都不影响面板展示。 */
  category: number;
  /** 是否出现在工具面板。登记业务 Kit 时写入 KitItem.checked。 */
  canShow: boolean;
  /** 是否为内置工具。业务工具保持 false。 */
  isInnerKit: boolean;
  /**
   * 工具 id。
   * 内置工具必须与 dokit_system_kits.json 的 innerKitId 一致。
   * 业务工具可不重写，登记时按名称生成。
   */
  innerKitId(): string;
  /** 工具面板上显示的名称。 */
  getName(): string;
  /** 工具面板上显示的图标。 */
  getIcon(): Resource;
  /** DoKit 安装时调用，用来做一次性初始化。 */
  onAppInit(context: common.UIAbilityContext): void;
  /**
   * 点击工具后的回调。
   * 返回 true 时关掉工具面板，返回 false 时面板留着。
   */
  onClickWithReturn(context: common.UIAbilityContext): boolean;
}
