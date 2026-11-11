import { AbstractKit } from './AbstractKit';
import { KitItem } from '../model/KitModels';

/**
 * 未指定分组名时使用的业务分组，对标 Android R.string.dk_category_biz。
 * customKitList 走这条默认分组。
 */
export const DEFAULT_BIZ_GROUP_NAME: string = '业务专区';

/** 把业务 Kit 转成工具面板上的 KitItem。 */
export class KitItemHelper {
  /**
   * 把一个 AbstractKit 转成 KitItem。
   * innerKitId 为空时，用序号和名称生成，避免同一组里撞名。
   * 图标文字取名称首字，名称为空时用 K。checked 取 canShow。
   */
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

  /** 按顺序转换一组 Kit，序号传给 fromAbstractKit 用来生成 id。 */
  static fromAbstractKits(kits: AbstractKit[]): KitItem[] {
    const items: KitItem[] = [];
    for (let i = 0; i < kits.length; i++) {
      items.push(KitItemHelper.fromAbstractKit(kits[i], i));
    }
    return items;
  }
}
