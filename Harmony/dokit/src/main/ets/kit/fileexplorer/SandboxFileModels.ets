/** 沙盒文件类型，对标 Android DoKitFileUtil 分类 */
export enum SandboxFileType {
  DIRECTORY = 0,
  TEXT = 1,
  IMAGE = 2,
  DATABASE = 3,
  OTHER = 4
}

/** 沙盒文件条目 */
export class SandboxFileItem {
  name: string = '';
  path: string = '';
  isDirectory: boolean = false;
  fileType: SandboxFileType = SandboxFileType.OTHER;
  size: number = 0;
  mtime: number = 0;
  /** 根目录展示标签，如 files / cache */
  rootLabel: string = '';
}
