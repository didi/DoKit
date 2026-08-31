import { fileIo as fs } from '@kit.CoreFileKit';

/**
 * 对象/文本缓存工具，对标 Android DoKitCacheUtils
 */
export class DoKitCacheUtils {
  static saveString(content: string, filePath: string): boolean {
    try {
      DoKitCacheUtils.ensureParentDir(filePath);
      const file = fs.openSync(filePath, fs.OpenMode.CREATE | fs.OpenMode.TRUNC | fs.OpenMode.WRITE_ONLY);
      fs.writeSync(file.fd, content);
      fs.closeSync(file);
      return true;
    } catch (_err) {
      return false;
    }
  }

  private static ensureParentDir(filePath: string): void {
    const index = filePath.lastIndexOf('/');
    if (index <= 0) {
      return;
    }
    const dir = filePath.substring(0, index);
    try {
      fs.accessSync(dir);
    } catch (_err) {
      try {
        fs.mkdirSync(dir, true);
      } catch (_mkdirErr) {
        // ignore
      }
    }
  }
}
