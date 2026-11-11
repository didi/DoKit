import { common } from '@kit.AbilityKit';
import { KitGroup, KitItem } from '../model/KitModels';
import { AbstractKit } from '../kit/AbstractKit';
import { KitItemHelper } from '../kit/KitItemHelper';
import { DoKitCallBack } from '../DoKitCallBack';
import { SystemKitRegistry } from './SystemKitRegistry';
import { KitWrapItem } from './toolpanel/KitWrapItem';
import { ToolPanelUtil } from './toolpanel/ToolPanelUtil';
import { SystemFragmentRegistry } from './SystemFragmentRigistry';

/**
 * 全局工具表，对标 Android kit/core/DoKitManager。
 * 安装时读入系统工具，并记下业务分组。工具面板从这里取分组。
 */
export class DoKitManager {
  /** 日志标签。 */
  static readonly TAG: string = 'DoKitConstant';
  /** 平台工具分组 id，与 dokit_system_kits.json 的 groupId 一致。 */
  static readonly GROUP_ID_PLATFORM: string = 'dk_category_platform';
  /** 常用工具分组 id。 */
  static readonly GROUP_ID_COMM: string = 'dk_category_comms';
  /** Weex 分组 id。 */
  static readonly GROUP_ID_WEEX: string = 'dk_category_weex';
  /** 性能监控分组 id。 */
  static readonly GROUP_ID_PERFORMANCE: string = 'dk_category_performance';
  /** 视觉工具分组 id。 */
  static readonly GROUP_ID_UI: string = 'dk_category_ui';
  /** LBS 分组 id。 */
  static readonly GROUP_ID_LBS: string = 'dk_category_lbs';

  /** 从配置读出的系统工具。键是分组 id。对标 GLOBAL_SYSTEM_KITS。 */
  static GLOBAL_SYSTEM_KITS: Map<string, KitWrapItem[]> = new Map();
  /** 面板实际使用的工具表。当前内容与系统工具表相同。对标 GLOBAL_KITS。 */
  static GLOBAL_KITS: Map<string, KitWrapItem[]> = new Map();
  /** 业务分组。键是分组名，同名再次登记会覆盖。 */
  private static customKitGroups: Map<string, KitItem[]> = new Map();

  /** 安装时记下的宿主 UIAbility。 */
  private static context: common.UIAbilityContext | null = null;
  /** 性能数据回调。 */
  private static callBack: DoKitCallBack | null = null;

  /** 记下宿主，登记系统页和内置工具，读面板配置，再调用各内置工具的 onAppInit。 */
  static init(context: common.UIAbilityContext): void {
    DoKitManager.context = context;
    SystemFragmentRegistry.initDefaultFragments();
    SystemKitRegistry.init();
    DoKitManager.GLOBAL_SYSTEM_KITS = ToolPanelUtil.loadSystemKitsFromRawFile(context);
    DoKitManager.syncGlobalKits();
    SystemKitRegistry.onAppInit(context);
  }

  /** 用系统工具表覆盖面板工具表。 */
  static syncGlobalKits(): void {
    DoKitManager.GLOBAL_KITS.clear();
    DoKitManager.GLOBAL_SYSTEM_KITS.forEach((items: KitWrapItem[], groupId: string) => {
      DoKitManager.GLOBAL_KITS.set(groupId, items);
    });
  }

  /** 记下性能数据回调。传空表示取消。 */
  static setCallBack(callback: DoKitCallBack | null): void {
    DoKitManager.callBack = callback;
  }

  /** 取出性能数据回调。还没设置时返回空。 */
  static getCallBack(): DoKitCallBack | null {
    return DoKitManager.callBack;
  }

  /** 取出安装时记下的宿主。还没安装时返回空。 */
  static getContext(): common.UIAbilityContext | null {
    return DoKitManager.context;
  }

  /** 面板分组。业务分组在前，系统分组接在后面。 */
  static getKitGroups(): KitGroup[] {
    const groups: KitGroup[] = [];
    DoKitManager.customKitGroups.forEach((kits: KitItem[], groupName: string) => {
      groups.push({
        groupId: `custom_${groupName}`,
        groupName,
        groupColor: '#666666',
        kits
      });
    });
    ToolPanelUtil.toKitGroups(groups,DoKitManager.GLOBAL_KITS);
    return groups;
  }

  /** 各组里勾选展示的工具。 */
  static getAllKits(): KitItem[] {
    const result: KitItem[] = [];
    DoKitManager.getKitGroups().forEach((group: KitGroup) => {
      group.kits.filter((item: KitItem) => item.checked).forEach((item: KitItem) => {
        result.push(item);
      });
    });
    return result;
  }

  /** 按 innerKitId 在全部分组里找一项。没有则返回空。 */
  static findKitById(innerKitId: string): KitItem | undefined {
    for (const group of DoKitManager.getKitGroups()) {
      const found = group.kits.find((item: KitItem) => item.innerKitId === innerKitId);
      if (found) {
        return found;
      }
    }
    return undefined;
  }

  /** 按分组名登记业务工具。同名分组覆盖，并立刻调用这些工具的 onAppInit。 */
  static addCustomKits(groupName: string, kits: AbstractKit[]): void {
    DoKitManager.customKitGroups.set(groupName, KitItemHelper.fromAbstractKits(kits));
    const context = DoKitManager.context;
    if (context !== null) {
      for (let i = 0; i < kits.length; i++) {
        kits[i].onAppInit(context);
      }
    }
  }
}
