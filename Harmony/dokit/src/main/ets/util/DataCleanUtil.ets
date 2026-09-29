import { common } from '@kit.AbilityKit';
import { fileIo as fs } from '@kit.CoreFileKit';
import { DoKitManager } from '../core/DoKitManager';
import { DoKitFileUtil } from './DoKitFileUtil';
import { DataCleanDirItem, DataCleanOutcome, DataCleanResult } from '../kit/dataclean/DataCleanModels';

/**
 * 清理缓存工具，对标 Android util/DataCleanUtil
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
    return DoKitFileUtil.getDirectorySize(root);
  }

  static getApplicationDataSizeStr(context: common.UIAbilityContext): string {
    return DoKitFileUtil.formatFileSize(DataCleanUtil.getApplicationDataSize(context));
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

  static hasCheckedDir(dirItems: DataCleanDirItem[]): boolean {
    for (let i = 0; i < dirItems.length; i++) {
      const item = dirItems[i];
      if (!item.isCheckAll && item.checked) {
        return true;
      }
    }
    return false;
  }

  static cleanSelectedDirs(context: common.UIAbilityContext, dirItems: DataCleanDirItem[]): DataCleanOutcome {
    const outcome = new DataCleanOutcome();
    const root = DataCleanUtil.getInternalAppDataPath(context);
    let selectedCount = 0;
    for (let i = 1; i < dirItems.length; i++) {
      const item = dirItems[i];
      if (!item.checked) {
        continue;
      }
      selectedCount++;
      const targetPath = DataCleanUtil.joinPath(root, item.name);
      if (!DoKitFileUtil.deleteDirectory(targetPath)) {
        outcome.failedNames.push(item.name);
      }
    }
    if (selectedCount === 0) {
      outcome.result = DataCleanResult.NONE_SELECTED;
      return outcome;
    }
    if (outcome.failedNames.length > 0) {
      outcome.result = DataCleanResult.PARTIAL_FAILED;
      return outcome;
    }
    outcome.result = DataCleanResult.SUCCESS;
    return outcome;
  }

  static getCheckAllLabel(): string {
    return DataCleanUtil.CHECK_ALL_LABEL;
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
