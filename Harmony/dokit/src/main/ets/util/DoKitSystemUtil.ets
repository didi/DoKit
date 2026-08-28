import { common, Want } from '@kit.AbilityKit';
import promptAction from '@ohos.promptAction';

/**
 * 系统能力跳转，对标 Android DoKitSystemUtil
 */
export class DoKitSystemUtil {
  private static readonly SETTINGS_BUNDLE: string = 'com.huawei.hmos.settings';
  private static readonly SETTINGS_ABILITY: string = 'com.huawei.hmos.settings.MainAbility';

  /**
   * 打开系统开发者选项页，对标 Android startDevelopmentActivity
   * uri: developer_options_settings => 系统和更新 > 开发人员选项
   */
  static startDevelopmentActivity(context: common.UIAbilityContext): void {
    const uris: string[] = [
      'developer_options_settings',
      'system_and_update_settings'
    ];
    DoKitSystemUtil.tryOpenSettings(context, uris).then((opened: boolean) => {
      if (!opened) {
        promptAction.showToast({
          message: '无法打开开发者选项，请手动进入系统设置',
          duration: 2500
        });
      }
    });
  }

  private static async tryOpenSettings(context: common.UIAbilityContext, uris: string[]): Promise<boolean> {
    for (let index = 0; index < uris.length; index++) {
      const uri = uris[index];
      const want: Want = {
        bundleName: DoKitSystemUtil.SETTINGS_BUNDLE,
        abilityName: DoKitSystemUtil.SETTINGS_ABILITY,
        uri
      };
      try {
        await context.startAbility(want);
        return true;
      } catch (_err) {
        // 部分设备 uri 不同，尝试下一级 fallback
      }
    }

    const fallbackWant: Want = {
      bundleName: DoKitSystemUtil.SETTINGS_BUNDLE,
      abilityName: DoKitSystemUtil.SETTINGS_ABILITY
    };
    try {
      await context.startAbility(fallbackWant);
      return true;
    } catch (_err) {
      return false;
    }
  }
}
