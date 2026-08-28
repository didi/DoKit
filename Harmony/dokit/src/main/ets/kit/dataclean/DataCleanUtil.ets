import { common } from '@kit.AbilityKit';
import { fileIo as fs } from '@kit.CoreFileKit';
import { DoKitManager } from '../../DoKitManager';
import { DataCleanDirItem } from './DataCleanModels';

/**
 * 清理缓存工具，对标 Android DataCleanUtil + DoKitFileUtil
 */
export class DataCleanUtil {
  private static readonly CHECK_ALL_LABEL: string = '全选';

  /** 对标 PathUtils.getInternalAppDataPath() => 应用沙盒根目录 */
  static getInternalAppDataPath(context: common.UIAbilityContext): string {
    const filesDir = context.filesDir;
    const index = filesDir.lastIndexOf('/');
    if (index <= 0) {
      return filesDir;
    }
    return filesDir.substring(0, index);
  }

  static getApplicationDataSize(context: common.UIAbilityContext): number {
    const root = DataCleanUtil.getInternalAppDataPath(context);
    return DataCleanUtil.getDirectorySize(root);
  }

  static getApplicationDataSizeStr(context: common.UIAbilityContext): string {
    return DataCleanUtil.formatFileSize(DataCleanUtil.getApplicationDataSize(context));
  }

  static buildDirItems(context: common.UIAbilityContext): DataCleanDirItem[] {
    const items: DataCleanDirItem[] = [];
    items.push(new DataCleanDirItem(DataCleanUtil.CHECK_ALL_LABEL, false, true));
    const root = DataCleanUtil.getInternalAppDataPath(context);
    if (!DataCleanUtil.pathExists(root)) {
      return items;
    }
    let names: string[] = [];
    try {
      names = fs.listFileSync(root);
    } catch (_err) {
      return items;
    }
    for (let i = 0; i < names.length; i++) {
      const name = names[i];
      const fullPath = DataCleanUtil.joinPath(root, name);
      try {
        if (fs.statSync(fullPath).isDirectory()) {
          items.push(new DataCleanDirItem(name, false, false));
        }
      } catch (_err) {
        // skip unreadable entry
      }
    }
    return items;
  }

  static cleanSelectedDirs(context: common.UIAbilityContext, dirItems: DataCleanDirItem[]): void {
    const root = DataCleanUtil.getInternalAppDataPath(context);
    for (let i = 1; i < dirItems.length; i++) {
      const item = dirItems[i];
      if (!item.checked) {
        continue;
      }
      const targetPath = DataCleanUtil.joinPath(root, item.name);
      DataCleanUtil.deleteDirectory(targetPath);
    }
  }


  static getCheckAllLabel(): string {
    return DataCleanUtil.CHECK_ALL_LABEL;
  }

  static getDirectorySize(directoryPath: string): number {
    if (!DataCleanUtil.pathExists(directoryPath)) {
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
        size += DataCleanUtil.getDirectorySize(DataCleanUtil.joinPath(directoryPath, names[i]));
      }
    } catch (_err) {
      return size;
    }
    return size;
  }

  /** 对标 DoKitFileUtil.deleteDirectory */
  static deleteDirectory(path: string): void {
    if (!DataCleanUtil.pathExists(path)) {
      return;
    }
    try {
      const stat = fs.statSync(path);
      if (stat.isDirectory()) {
        const names = fs.listFileSync(path);
        for (let i = 0; i < names.length; i++) {
          DataCleanUtil.deleteDirectory(DataCleanUtil.joinPath(path, names[i]));
        }
        fs.rmdirSync(path);
      } else {
        fs.unlinkSync(path);
      }
    } catch (_err) {
      // 部分系统目录可能不可删，忽略
    }
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

  static getContextOrNull(): common.UIAbilityContext | null {
    return DoKitManager.getContext();
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
