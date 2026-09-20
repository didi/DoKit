import { fileIo as fs } from '@kit.CoreFileKit';

/**
 * 文件工具，对标 Android DoKitFileUtil
 */
export class DoKitFileUtil {
  static readonly TXT: string = 'txt';
  static readonly JPG: string = 'jpg';
  static readonly DB: string = 'db';
  static readonly SHARED_PREFS: string = 'shared_prefs';
  static readonly XML: string = '.xml';

  static getSuffix(fileName: string): string {
    const index = fileName.lastIndexOf('.');
    if (index < 0) {
      return '';
    }
    return fileName.substring(index + 1).toLowerCase();
  }

  static isImage(fileName: string): boolean {
    const suffix = DoKitFileUtil.getSuffix(fileName);
    return suffix === 'jpg' || suffix === 'jpeg' || suffix === 'png' || suffix === 'bmp';
  }

  static isVideo(fileName: string): boolean {
    const suffix = DoKitFileUtil.getSuffix(fileName);
    return suffix === '3gp' || suffix === 'mp4' || suffix === 'mkv' || suffix === 'webm';
  }

  static isDB(fileName: string): boolean {
    return DoKitFileUtil.getSuffix(fileName) === 'db';
  }

  static isSp(fileName: string, parentDirName: string): boolean {
    return parentDirName === DoKitFileUtil.SHARED_PREFS && fileName.includes(DoKitFileUtil.XML);
  }

  static deleteDirectory(path: string): boolean {
    if (!DoKitFileUtil.pathExists(path)) {
      return true;
    }
    try {
      const stat = fs.statSync(path);
      if (stat.isDirectory()) {
        const names = fs.listFileSync(path);
        let success = true;
        for (let i = 0; i < names.length; i++) {
          if (!DoKitFileUtil.deleteDirectory(DoKitFileUtil.joinPath(path, names[i]))) {
            success = false;
          }
        }
        if (!success) {
          return false;
        }
        try {
          fs.rmdirSync(path);
          return true;
        } catch (_rmdirErr) {
          const left = fs.listFileSync(path);
          return left.length === 0;
        }
      }
      fs.unlinkSync(path);
      return true;
    } catch (_err) {
      return false;
    }
  }

  static getDirectorySize(directoryPath: string): number {
    if (!DoKitFileUtil.pathExists(directoryPath)) {
      return 0;
    }
    let size = 0;
    try {
      const stat = fs.statSync(directoryPath);
      if (!stat.isDirectory()) {
        return stat.size;
      }
      const names = fs.listFileSync(directoryPath);
      for (let i = 0; i < names.length; i++) {
        size += DoKitFileUtil.getDirectorySize(DoKitFileUtil.joinPath(directoryPath, names[i]));
      }
    } catch (_err) {
      return size;
    }
    return size;
  }

  static formatFileSize(bytes: number): string {
    if (bytes <= 0) {
      return '0 B';
    }
    const units: string[] = ['B', 'KB', 'MB', 'GB', 'TB'];
    let value = bytes;
    let unitIndex = 0;
    while (value >= 1024 && unitIndex < units.length - 1) {
      value /= 1024;
      unitIndex++;
    }
    const fractionDigits = unitIndex === 0 ? 0 : 2;
    return `${value.toFixed(fractionDigits)} ${units[unitIndex]}`;
  }

  private static pathExists(path: string): boolean {
    try {
      fs.accessSync(path);
      return true;
    } catch (_err) {
      return false;
    }
  }

  private static joinPath(base: string, name: string): string {
    if (base.endsWith('/')) {
      return `${base}${name}`;
    }
    return `${base}/${name}`;
  }
}
