/**
 * 工具面板 JSON 配置模型，对标 Android KitGroupBean / KitBean
 */
export class KitBean {
  allClassName: string = '';
  checked: boolean = true;
  innerKitId: string = '';

  constructor(innerKitId: string, checked: boolean, allClassName: string = '') {
    this.innerKitId = innerKitId;
    this.checked = checked;
    this.allClassName = allClassName;
  }
}

export class KitGroupBean {
  groupId: string = '';
  kits: KitBean[] = [];

  constructor(groupId: string, kits: KitBean[]) {
    this.groupId = groupId;
    this.kits = kits;
  }
}
