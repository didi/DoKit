import { common } from '@kit.AbilityKit';
import { WebDoorNavigator } from './WebDoorNavigator';
import { WebDoorCallback } from './WebDoorCallback';

/** 默认打开内置 WebView 页 */
export class DefaultWebDoorCallback implements WebDoorCallback {
  overrideUrlLoading(_context: common.UIAbilityContext, url: string): void {
    WebDoorNavigator.openWebView(url);
  }
}
