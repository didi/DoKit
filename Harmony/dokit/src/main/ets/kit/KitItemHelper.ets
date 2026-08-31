import { AbstractKit } from './AbstractKit';
import { KitItem } from '../model/KitModels';

/** 默认业务分组名，对标 Android R.string.dk_category_biz */
export const DEFAULT_BIZ_GROUP_NAME: string = '业务专区';

export class KitItemHelper {
  static fromAbstractKit(kit: AbstractKit, index: number = 0): KitItem {
    const kitId = kit.innerKitId();
    const innerKitId = kitId.length > 0 ? kitId : `dokit_sdk_business_ck_${index}_${kit.getName()}`;
    return {
      innerKitId,
      name: kit.getName(),
      iconText: kit.getName().length > 0 ? kit.getName().substring(0, 1) : 'K',
      checked: kit.canShow,
      iconResource: kit.getIcon(),
      customKit: kit
    };
  }

  static fromAbstractKits(kits: AbstractKit[]): KitItem[] {
    const items: KitItem[] = [];
    for (let i = 0; i < kits.length; i++) {
      items.push(KitItemHelper.fromAbstractKit(kits[i], i));
    }
    return items;
  }
}
