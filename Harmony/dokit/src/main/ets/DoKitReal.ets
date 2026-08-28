import { common } from '@kit.AbilityKit';
import { AbstractKit } from './kit/AbstractKit';
import { DoKitManager } from './kit/core/DoKitManager';
import { DEFAULT_BIZ_GROUP_NAME } from './kit/KitItemHelper';
import { DoKitOverlayState } from './model/DoKitOverlayState';

/**
 * DoKit 真正执行的类，对标 Android DoKitReal（不建议外部直接调用）
 */
export class DoKitReal {
  static isInit: boolean = false;

  static install(
    context: common.UIAbilityContext,
    customKits: Map<string, AbstractKit[]>,
    customKitList: AbstractKit[]
  ): void {
    if (DoKitReal.isInit) {
      return;
    }
    DoKitManager.init(context);
    if (customKits.size > 0) {
      customKits.forEach((kits: AbstractKit[], groupName: string) => {
        DoKitManager.addCustomKits(groupName, kits);
      });
    } else if (customKitList.length > 0) {
      DoKitManager.addCustomKits(DEFAULT_BIZ_GROUP_NAME, customKitList);
    }
    DoKitOverlayState.init();
    DoKitReal.isInit = true;
  }
}
