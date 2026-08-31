import { common } from '@kit.AbilityKit';

/**
 * H5任意门跳转回调，对标 Android WebDoorManager.WebDoorCallback
 */
export interface WebDoorCallback {
  overrideUrlLoading(context: common.UIAbilityContext, url: string): void;
}
