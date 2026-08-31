import { common, errorManager } from '@kit.AbilityKit';
import { fileIo as fs } from '@kit.CoreFileKit';
import promptAction from '@ohos.promptAction';
import { CachesKey } from '../../constant/CachesKey';
import { DoKitCacheUtils } from '../../util/DoKitCacheUtils';
import { DoKitFileUtil } from '../../util/DoKitFileUtil';

/**
 * 系统崩溃异常捕获，对标 Android CrashCaptureManager
 */
export class CrashCaptureManager {
  private static instance: CrashCaptureManager | null = null;

  private context: common.Context | null = null;
  private filenamePrefix: string = '';
  private isListening: boolean = false;
  private observerId: number = -1;
  private readonly errorObserver: errorManager.ErrorObserver = {
    onUnhandledException: (errMsg: string) => {
      CrashCaptureManager.getInstance().handleCrash(errMsg);
    }
  };

  static getInstance(): CrashCaptureManager {
    if (CrashCaptureManager.instance === null) {
      CrashCaptureManager.instance = new CrashCaptureManager();
    }
    return CrashCaptureManager.instance;
  }

  init(context: common.Context): void {
    this.context = context.getApplicationContext();
    this.generateFilenamePrefix();
  }

  start(): void {
    if (this.isListening) {
      return;
    }
    try {
      this.observerId = errorManager.on('error', this.errorObserver);
      this.isListening = true;
    } catch (_err) {
      // 部分系统版本可能不支持 errorManager
    }
  }

  stop(): void {
    if (!this.isListening || this.observerId < 0) {
      return;
    }
    try {
      errorManager.off('error', this.observerId);
    } catch (_err) {
      // ignore
    }
    this.observerId = -1;
    this.isListening = false;
  }

  getCrashCacheDir(): string {
    if (this.context === null) {
      return '';
    }
    const dir = `${this.context.cacheDir}/${CachesKey.CRASH_HISTORY}`;
    try {
      fs.accessSync(dir);
    } catch (_err) {
      try {
        fs.mkdirSync(dir, true);
      } catch (_mkdirErr) {
        // ignore
      }
    }
    return dir;
  }

  clearCacheHistory(): void {
    const dir = this.getCrashCacheDir();
    if (dir.length > 0) {
      DoKitFileUtil.deleteDirectory(dir);
    }
  }

  getCrashCacheSize(): number {
    const dir = this.getCrashCacheDir();
    if (dir.length === 0) {
      return 0;
    }
    return DoKitFileUtil.getDirectorySize(dir);
  }

  /**
   * 对标 Android asyncSaveCrashScreenshot，鸿蒙 MVP 暂不实现截图
   */
  asyncSaveCrashScreenshot(): void {
  }

  private handleCrash(stackTrace: string): void {
    this.generateFilenamePrefix();
    this.asyncSaveCrashScreenshot();
    const filePath = this.getCrashCacheFile();
    DoKitCacheUtils.saveString(stackTrace, filePath);
    promptAction.showToast({
      message: '捕获到崩溃异常，请到 Crash 工具查看',
      duration: 2000
    });
  }

  private getCrashCacheFile(): string {
    return `${this.getCrashCacheDir()}/${this.filenamePrefix}.log`;
  }

  /** 对标 Android SimpleDateFormat("yyyyMMdd_HHmmdd") */
  private generateFilenamePrefix(): void {
    const now = new Date();
    const pad2 = (value: number): string => {
      return value < 10 ? `0${value}` : `${value}`;
    };
    this.filenamePrefix = `${now.getFullYear()}${pad2(now.getMonth() + 1)}${pad2(now.getDate())}_` +
      `${pad2(now.getHours())}${pad2(now.getMinutes())}${pad2(now.getDate())}`;
  }
}
