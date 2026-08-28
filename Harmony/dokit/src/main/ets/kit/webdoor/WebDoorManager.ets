import { GlobalConfig } from '../../config/GlobalConfig';
import { DoKitManager } from '../core/DoKitManager';
import { DefaultWebDoorCallback } from './DefaultWebDoorCallback';
import { WebDoorCallback } from './WebDoorCallback';
import { WebDoorHistoryStore } from './WebDoorHistoryStore';
import { WebDoorUrlHelper } from './WebDoorUrlHelper';

/**
 * H5任意门管理，对标 Android WebDoorManager
 */
export class WebDoorManager {
  static saveHistory(url: string): void {
    WebDoorHistoryStore.save(url);
  }

  static getHistory(): string[] {
    return WebDoorHistoryStore.load();
  }

  static clearHistory(): void {
    WebDoorHistoryStore.clear();
  }

  static openUrl(rawUrl: string): boolean {
    const trimmed = rawUrl.trim();
    if (!WebDoorUrlHelper.isValid(trimmed)) {
      return false;
    }
    const context = DoKitManager.getContext();
    if (context === null) {
      return false;
    }
    WebDoorManager.saveHistory(trimmed);
    const finalUrl = WebDoorUrlHelper.correct(trimmed);
    WebDoorManager.getWebDoorCallback().overrideUrlLoading(context, finalUrl);
    return true;
  }

  static getWebDoorCallback(): WebDoorCallback {
    if (GlobalConfig.webDoorCallback !== null) {
      return GlobalConfig.webDoorCallback;
    }
    return WebDoorManager.defaultCallback;
  }

  private static readonly defaultCallback: WebDoorCallback = new DefaultWebDoorCallback();
}
