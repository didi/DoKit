import { preferences } from '@kit.ArkData';
import { DoKitManager } from '../core/DoKitManager';

/**
 * Preferences 工具，对标 Android DoKitSPUtil
 */
export class DoKitSPUtil {
  private static readonly PREF_NAME: string = 'dokit_sp';

  static getBoolean(key: string, defaultValue: boolean): boolean {
    const pref = DoKitSPUtil.getPreferences();
    if (pref === null) {
      return defaultValue;
    }
    try {
      return pref.getSync(key, defaultValue) as boolean;
    } catch (_err) {
      return defaultValue;
    }
  }

  static putBoolean(key: string, value: boolean): void {
    const pref = DoKitSPUtil.getPreferences();
    if (pref === null) {
      return;
    }
    try {
      pref.putSync(key, value);
      pref.flushSync();
    } catch (_err) {
      // ignore
    }
  }

  private static getPreferences(): preferences.Preferences | null {
    const context = DoKitManager.getContext();
    if (context === null) {
      return null;
    }
    try {
      return preferences.getPreferencesSync(context, { name: DoKitSPUtil.PREF_NAME });
    } catch (_err) {
      return null;
    }
  }
}
