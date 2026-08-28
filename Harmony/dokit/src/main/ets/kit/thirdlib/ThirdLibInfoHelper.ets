import { DokitThirdLibInfo } from '../../aop/DokitThirdLibInfo';
import { ThirdLibListItem, ThirdLibSortMode } from './ThirdLibInfoModels';

export class ThirdLibInfoHelper {
  static collect(keyword: string, sortMode: ThirdLibSortMode): ThirdLibListItem[] {
    const items: ThirdLibListItem[] = [];
    const trimmed = keyword.trim();
    const lowerKeyword = trimmed.toLowerCase();

    DokitThirdLibInfo.thirdLibInfos.forEach((value: string, key: string) => {
      if (trimmed.length > 0) {
        const lowerKey = key.toLowerCase();
        if (!lowerKey.startsWith(lowerKeyword) && !lowerKey.includes(lowerKeyword)) {
          return;
        }
      }
      const sizeBytes = parseInt(value, 10);
      items.push(new ThirdLibListItem(key, Number.isNaN(sizeBytes) ? 0 : sizeBytes));
    });

    return ThirdLibInfoHelper.sort(items, sortMode);
  }

  static sort(items: ThirdLibListItem[], sortMode: ThirdLibSortMode): ThirdLibListItem[] {
    const result = [...items];
    if (sortMode === ThirdLibSortMode.SIZE) {
      result.sort((a: ThirdLibListItem, b: ThirdLibListItem) => b.sizeBytes - a.sizeBytes);
    } else {
      result.sort((a: ThirdLibListItem, b: ThirdLibListItem) => a.name.localeCompare(b.name));
    }
    return result;
  }

  /** 对标 Android Formatter.formatFileSize */
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
}
