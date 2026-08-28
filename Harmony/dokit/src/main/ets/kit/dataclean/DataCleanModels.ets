/** 清理缓存目录项，对标 Android DataCleanFragment 动态列表 */
export class DataCleanDirItem {
  name: string = '';
  checked: boolean = false;
  isCheckAll: boolean = false;

  constructor(name: string, checked: boolean = false, isCheckAll: boolean = false) {
    this.name = name;
    this.checked = checked;
    this.isCheckAll = isCheckAll;
  }
}
