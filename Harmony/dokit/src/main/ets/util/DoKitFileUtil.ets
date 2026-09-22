import { common, Want, wantConstant } from '@kit.AbilityKit';
import { fileIo as fs, fileUri } from '@kit.CoreFileKit';
import { hilog } from '@kit.PerformanceAnalysisKit';

const DOMAIN = 0x0000;
const TAG = 'DoKitH5';

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

  /** 对标 Android DoKitFileUtil.systemShare */
  static systemShare(context: common.UIAbilityContext, path: string): void {
    if (!DoKitFileUtil.pathExists(path)) {
      hilog.warn(DOMAIN, TAG, 'DoKitH5|FileExplorer share skip missing path=%{public}s', path);
      return;
    }
    const uri = fileUri.getUriFromPath(path);
    hilog.info(DOMAIN, TAG, 'DoKitH5|FileExplorer share start uri=%{public}s', uri);
    const want: Want = {
      action: 'ohos.want.action.sendData',
      uri: uri,
      type: '*/*',
      flags: wantConstant.Flags.FLAG_AUTH_READ_URI_PERMISSION
    };
    context.startAbility(want).then(() => {
      hilog.info(DOMAIN, TAG, 'DoKitH5|FileExplorer share ok uri=%{public}s', uri);
    }).catch((err: Error) => {
      hilog.error(DOMAIN, TAG, 'DoKitH5|FileExplorer share fail uri=%{public}s err=%{public}s',
        uri, JSON.stringify(err));
    });
  }

  static deleteDirectory(path: string, logResult: boolean = true): void {
    if (!DoKitFileUtil.pathExists(path)) {
      if (logResult) {
        hilog.warn(DOMAIN, TAG, 'DoKitH5|FileExplorer delete skip missing path=%{public}s', path);
      }
      return;
    }
    try {
      const stat = fs.statSync(path);
      if (stat.isDirectory()) {
        const names = fs.listFileSync(path);
        for (let i = 0; i < names.length; i++) {
          DoKitFileUtil.deleteDirectory(DoKitFileUtil.joinPath(path, names[i]), false);
        }
        fs.rmdirSync(path);
      } else {
        fs.unlinkSync(path);
      }
      if (logResult) {
        hilog.info(DOMAIN, TAG, 'DoKitH5|FileExplorer delete ok dir=%{public}s path=%{public}s',
          String(stat.isDirectory()), path);
      }
    } catch (err) {
      hilog.error(DOMAIN, TAG, 'DoKitH5|FileExplorer delete fail path=%{public}s err=%{public}s',
        path, JSON.stringify(err));
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
