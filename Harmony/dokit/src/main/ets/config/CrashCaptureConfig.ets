import { SharedPrefsKey } from '../constant/SharedPrefsKey';
import { DoKitSPUtil } from '../util/DoKitSPUtil';

/**
 * Crash 捕获配置，对标 Android CrashCaptureConfig
 */
export class CrashCaptureConfig {
  static isCrashCaptureOpen(): boolean {
    return DoKitSPUtil.getBoolean(SharedPrefsKey.CRASH_CAPTURE_OPEN, false);
  }

  static setCrashCaptureOpen(open: boolean): void {
    DoKitSPUtil.putBoolean(SharedPrefsKey.CRASH_CAPTURE_OPEN, open);
  }
}
