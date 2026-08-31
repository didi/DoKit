/** 三方库列表项 */
export class ThirdLibListItem {
  name: string = '';
  sizeBytes: number = 0;

  constructor(name: string, sizeBytes: number) {
    this.name = name;
    this.sizeBytes = sizeBytes;
  }
}

/** 排序方式，对标 Android sort_name / sort_size */
export enum ThirdLibSortMode {
  NAME = 0,
  SIZE = 1
}
