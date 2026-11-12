import { AbstractKit } from '../../kit/AbstractKit';

/**
 * 工具面板里的一条记录，对标 Android KitWrapItem。
 * 配置解析时先放进这个结构，再转成面板上的 KitItem。
 */
export class KitWrapItem {
  /** 分组标题。 */
  static readonly TYPE_TITLE: number = 999;
  /** 一个工具。 */
  static readonly TYPE_KIT: number = 201;
  /** 浮标模式。 */
  static readonly TYPE_MODE: number = 202;
  /** 退出。 */
  static readonly TYPE_EXIT: number = 203;
  /** 版本号。 */
  static readonly TYPE_VERSION: number = 204;

  /** 这条记录的类型，取上面的 TYPE_ 常量。 */
  itemType: number;
  /** 显示名称。 */
  name: string;
  /** 是否出现在面板上。 */
  checked: boolean;
  /** 所属分组 id。 */
  groupName: string;
  /** 已实现的工具。配置里有、代码里还没有时为 null。 */
  kit: AbstractKit | null;
  /** 工具 id，与 dokit_system_kits.json 的 innerKitId 一致。 */
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
