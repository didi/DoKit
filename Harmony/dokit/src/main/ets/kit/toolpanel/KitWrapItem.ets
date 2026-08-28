import { AbstractKit } from '../AbstractKit';

/**
 * 工具面板列表项，对标 Android KitWrapItem
 */
export class KitWrapItem {
  static readonly TYPE_TITLE: number = 999;
  static readonly TYPE_KIT: number = 201;
  static readonly TYPE_MODE: number = 202;
  static readonly TYPE_EXIT: number = 203;
  static readonly TYPE_VERSION: number = 204;

  itemType: number;
  name: string;
  checked: boolean;
  groupName: string;
  kit: AbstractKit | null;
  innerKitId: string;

  constructor(
    itemType: number,
    name: string,
    checked: boolean,
    groupName: string,
    kit: AbstractKit | null,
    innerKitId: string
  ) {
    this.itemType = itemType;
    this.name = name;
    this.checked = checked;
    this.groupName = groupName;
    this.kit = kit;
    this.innerKitId = innerKitId;
  }
}
