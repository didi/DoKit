/**
 * dokit_system_kits.json 里的一条工具，对标 Android KitBean。
 */
export class KitBean {
  /** Android 上的类名。创建工具时按 innerKitId 查找。 */
  allClassName: string = '';
  /** 是否默认出现在面板上。 */
  checked: boolean = true;
  /** 工具 id。 */
  innerKitId: string = '';

  constructor(innerKitId: string, checked: boolean, allClassName: string = '') {
    this.innerKitId = innerKitId;
    this.checked = checked;
    this.allClassName = allClassName;
  }
}

/** dokit_system_kits.json 里的一个分组，对标 Android KitGroupBean。 */
export class KitGroupBean {
  /** 分组 id。 */
  groupId: string = '';
  /** 这个分组下的工具。 */
  kits: KitBean[] = [];

  constructor(groupId: string, kits: KitBean[]) {
    this.groupId = groupId;
    this.kits = kits;
  }
}
