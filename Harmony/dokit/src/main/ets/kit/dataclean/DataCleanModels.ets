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

export enum DataCleanResult {
  NONE_SELECTED = 0,
  SUCCESS = 1,
  PARTIAL_FAILED = 2
}

export class DataCleanOutcome {
  result: DataCleanResult = DataCleanResult.SUCCESS;
  failedNames: string[] = [];
}
