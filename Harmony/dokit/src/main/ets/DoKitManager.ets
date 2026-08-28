import { common } from '@kit.AbilityKit';
import { KitGroup, KitItem } from './model/KitModels';
import { AbstractKit } from './kit/AbstractKit';
import { KitItemHelper } from './kit/KitItemHelper';
import { getSystemKitGroups } from './config/DokitSystemKits';
import { DoKitCallBack } from './callback/DoKitCallBack';

/**
 * 全局 Kit 注册表，对标 Android DoKitManager
 */
export class DoKitManager {
  private static context: common.UIAbilityContext | null = null;
  private static kitGroups: KitGroup[] = [];
  private static customKitGroups: Map<string, KitItem[]> = new Map();
  private static callBack: DoKitCallBack | null = null;

  static init(context: common.UIAbilityContext): void {
    DoKitManager.context = context;
    DoKitManager.kitGroups = getSystemKitGroups();
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
    const groups: KitGroup[] = [...DoKitManager.kitGroups];
    DoKitManager.customKitGroups.forEach((kits: KitItem[], groupName: string) => {
      groups.push({
        groupId: `custom_${groupName}`,
        groupName,
        groupColor: '#666666',
        kits
      });
    });
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
