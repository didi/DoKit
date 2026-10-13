import { SharedPrefsKey } from '../constant/SharedPrefsKey';
import { DoKitSPUtil } from '../util/DoKitSPUtil';

/**
 * 布局边框开关。Android 只放内存里，这里记本次打开，启动时清掉。
 */
export class LayoutBorderConfig {
  static isLayoutBorderOpen(): boolean {
    return DoKitSPUtil.getBoolean(SharedPrefsKey.LAYOUT_BORDER_OPEN, false);
  }

  static setLayoutBorderOpen(open: boolean): void {
    DoKitSPUtil.putBoolean(SharedPrefsKey.LAYOUT_BORDER_OPEN, open);
  }
}
