import { common } from '@kit.AbilityKit';
import { KitGroup, KitItem } from '../../model/KitModels';
import { AbstractKit } from '../AbstractKit';
import { KitItemHelper } from '../KitItemHelper';
import { DoKitCallBack } from '../../DoKitCallBack';
import { InnerKitRegistry } from './InnerKitRegistry';
import { UniversalActivity } from './UniversalActivity';
import { KitWrapItem } from '../toolpanel/KitWrapItem';
import { ToolPanelUtil } from '../toolpanel/ToolPanelUtil';

/**
 * 全局 Kit 注册表，对标 Android kit/core/DoKitManager
 */
export class DoKitManager {
  static readonly TAG: string = 'DoKitConstant';
  static readonly GROUP_ID_PLATFORM: string = 'dk_category_platform';
  static readonly GROUP_ID_COMM: string = 'dk_category_comms';
  static readonly GROUP_ID_WEEX: string = 'dk_category_weex';
  static readonly GROUP_ID_PERFORMANCE: string = 'dk_category_performance';
  static readonly GROUP_ID_UI: string = 'dk_category_ui';
  static readonly GROUP_ID_LBS: string = 'dk_category_lbs';

  /** 全局系统内置 kit，对标 GLOBAL_SYSTEM_KITS */
  static GLOBAL_SYSTEM_KITS: Map<string, KitWrapItem[]> = new Map();
  /** 全局 Kits（含系统 + 后续扩展），对标 GLOBAL_KITS */
  static GLOBAL_KITS: Map<string, KitWrapItem[]> = new Map();
  /**
   * 业务注册自定义工具kit
   */
  private static customKitGroups: Map<string, KitItem[]> = new Map();

  private static context: common.UIAbilityContext | null = null;
  private static callBack: DoKitCallBack | null = null;

  static init(context: common.UIAbilityContext): void {
    DoKitManager.context = context;
    UniversalActivity.initDefaultFragments();
    InnerKitRegistry.init();
    DoKitManager.GLOBAL_SYSTEM_KITS = ToolPanelUtil.loadSystemKitsFromRawFile(context);
    DoKitManager.syncGlobalKits();
    InnerKitRegistry.onAppInit(context);
  }

  static syncGlobalKits(): void {
    DoKitManager.GLOBAL_KITS.clear();
    DoKitManager.GLOBAL_SYSTEM_KITS.forEach((items: KitWrapItem[], groupId: string) => {
      DoKitManager.GLOBAL_KITS.set(groupId, items);
    });
  }

  static setCallBack(callback: DoKitCallBack | null): void {
    DoKitManager.callBack = callback;
  }

  static getCallBack(): DoKitCallBack | null {
    return DoKitManager.callBack;
  }

  static getContext(): common.UIAbilityContext | null {
    return DoKitManager.context;
  }

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

  static getAllKits(): KitItem[] {
    const result: KitItem[] = [];
    DoKitManager.getKitGroups().forEach((group: KitGroup) => {
      group.kits.filter((item: KitItem) => item.checked).forEach((item: KitItem) => {
        result.push(item);
      });
    });
    return result;
  }

  static findKitById(innerKitId: string): KitItem | undefined {
    for (const group of DoKitManager.getKitGroups()) {
      const found = group.kits.find((item: KitItem) => item.innerKitId === innerKitId);
      if (found) {
        return found;
      }
    }
    return undefined;
  }

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
