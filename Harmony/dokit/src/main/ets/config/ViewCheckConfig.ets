import { SharedPrefsKey } from '../constant/SharedPrefsKey';
import { DoKitSPUtil } from '../util/DoKitSPUtil';

/**
 * 控件检查开关。Android 的 ViewCheckerKit 不落盘，这里只在本次打开期间记一笔，启动时清掉。
 */
export class ViewCheckConfig {
  static isViewCheckOpen(): boolean {
    return DoKitSPUtil.getBoolean(SharedPrefsKey.VIEW_CHECK_OPEN, false);
  }

  static setViewCheckOpen(open: boolean): void {
    DoKitSPUtil.putBoolean(SharedPrefsKey.VIEW_CHECK_OPEN, open);
  }
}
