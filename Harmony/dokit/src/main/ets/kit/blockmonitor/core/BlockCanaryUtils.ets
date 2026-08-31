import { BlockInfo } from '../bean/BlockInfo';

/**
 * 堆栈过滤工具，对标 Android BlockCanaryUtils
 */
export class BlockCanaryUtils {
  static concernStackString(blockInfo: BlockInfo): string {
    for (let i = 0; i < blockInfo.threadStackEntries.length; i++) {
      const stackEntry = blockInfo.threadStackEntries[i];
      if (stackEntry.length === 0) {
        continue;
      }
      const lines = stackEntry.split(BlockInfo.SEPARATOR);
      for (let j = 0; j < lines.length; j++) {
        const line = lines[j].trim();
        if (line.length === 0) {
          continue;
        }
        const keyStack = BlockCanaryUtils.classSimpleName(line);
        if (keyStack.length > 0) {
          return keyStack;
        }
      }
    }
    return '';
  }

  static isBlockInfoValid(blockInfo: BlockInfo): boolean {
    return blockInfo.timeStart.length > 0 && blockInfo.timeCost >= 0;
  }

  private static classSimpleName(stackLine: string): string {
    const index1 = stackLine.indexOf('(');
    const index2 = stackLine.indexOf(')');
    if (index1 >= 0 && index2 > index1) {
      return stackLine.substring(index1 + 1, index2);
    }
    return stackLine;
  }
}
