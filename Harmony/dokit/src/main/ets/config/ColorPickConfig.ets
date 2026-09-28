import { SharedPrefsKey } from '../constant/SharedPrefsKey';
import { DoKitSPUtil } from '../util/DoKitSPUtil';

/**
 * 取色器开关配置，对标 Android ColorPickConfig
 */
export class ColorPickConfig {
  static isColorPickOpen(): boolean {
    return DoKitSPUtil.getBoolean(SharedPrefsKey.COLOR_PICK_OPEN, false);
  }

  static setColorPickOpen(open: boolean): void {
    DoKitSPUtil.putBoolean(SharedPrefsKey.COLOR_PICK_OPEN, open);
  }
}
