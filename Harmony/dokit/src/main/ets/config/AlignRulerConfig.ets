import { SharedPrefsKey } from '../constant/SharedPrefsKey';
import { DoKitSPUtil } from '../util/DoKitSPUtil';

/**
 * 对齐标尺开关，对标 Android AlignRulerConfig
 */
export class AlignRulerConfig {
  static isAlignRulerOpen(): boolean {
    return DoKitSPUtil.getBoolean(SharedPrefsKey.ALIGN_RULER_OPEN, false);
  }

  static setAlignRulerOpen(open: boolean): void {
    DoKitSPUtil.putBoolean(SharedPrefsKey.ALIGN_RULER_OPEN, open);
  }
}
