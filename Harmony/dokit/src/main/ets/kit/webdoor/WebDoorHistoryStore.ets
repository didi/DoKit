import { preferences } from '@kit.ArkData';
import { DoKitManager } from '../../core/DoKitManager';

/**
 * H5任意门历史记录，对标 Android WebDoorManager + harmonydriver H5DoorHistoryStore
 */
export class WebDoorHistoryStore {
  private static readonly HISTORY_KEY: string = 'web_door_history';
  private static readonly PREF_NAME: string = 'dokit_web_door';
  /** Android 最多保留 5 条 */
  private static readonly MAX_SIZE: number = 5;

  static load(): string[] {
    const context = DoKitManager.getContext();
    if (context === null) {
      return [];
    }
    try {
      const pref = preferences.getPreferencesSync(context, { name: WebDoorHistoryStore.PREF_NAME });
      const raw = pref.getSync(WebDoorHistoryStore.HISTORY_KEY, '[]') as string;
      if (raw.length === 0) {
        return [];
      }
      const parsed = JSON.parse(raw) as string[];
      const result: string[] = [];
      for (let i = 0; i < parsed.length; i++) {
        const item = parsed[i];
        if (typeof item === 'string' && item.trim().length > 0) {
          result.push(item);
        }
      }
      return result;
    } catch (_err) {
      return [];
    }
  }

  static save(url: string): void {
    const trimmedUrl = url.trim();
    if (trimmedUrl.length === 0) {
      return;
    }
    const existing = WebDoorHistoryStore.load();
    if (existing.indexOf(trimmedUrl) >= 0) {
      return;
    }
    const next: string[] = [];
    next.push(trimmedUrl);
    for (let i = 0; i < existing.length; i++) {
      if (next.length >= WebDoorHistoryStore.MAX_SIZE) {
        break;
      }
      next.push(existing[i]);
    }
    WebDoorHistoryStore.persist(next);
  }

  static clear(): void {
    WebDoorHistoryStore.persist([]);
  }

  private static persist(items: string[]): void {
    const context = DoKitManager.getContext();
    if (context === null) {
      return;
    }
    try {
      const pref = preferences.getPreferencesSync(context, { name: WebDoorHistoryStore.PREF_NAME });
      pref.putSync(WebDoorHistoryStore.HISTORY_KEY, JSON.stringify(items));
      pref.flushSync();
    } catch (_err) {
      // ignore
    }
  }
}
