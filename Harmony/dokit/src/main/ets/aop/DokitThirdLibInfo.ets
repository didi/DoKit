import { common } from '@kit.AbilityKit';
import { util } from '@kit.ArkTS';

/**
 * 三方库信息注册表，对标 Android DokitThirdLibInfo
 * key: groupId:artifactId:version 或 @scope/pkg:version
 * value: 体积（字节）字符串
 */
export class DokitThirdLibInfo {
  static thirdLibInfos: Map<string, string> = new Map();
  /** key => groupId:artifactId（Maven 三段式时） */
  static thirdLibInfosSimple: Map<string, string> = new Map();

  static inject(config: Map<string, string>): void {
    DokitThirdLibInfo.thirdLibInfos = new Map(config);
    DokitThirdLibInfo.rebuildSimple();
  }

  static merge(config: Map<string, string>): void {
    config.forEach((value: string, key: string) => {
      DokitThirdLibInfo.thirdLibInfos.set(key, value);
    });
    DokitThirdLibInfo.rebuildSimple();
  }

  static register(name: string, sizeBytes: number): void {
    DokitThirdLibInfo.thirdLibInfos.set(name, `${sizeBytes}`);
    DokitThirdLibInfo.rebuildSimple();
  }

  static isEmpty(): boolean {
    return DokitThirdLibInfo.thirdLibInfos.size === 0;
  }

  /**
   * 从宿主 rawfile 加载 JSON
   * 支持 {"lib:1.0.0": "12345"} 或 [{"name":"lib:1.0.0","size":12345}]
   */
  static loadFromRawFile(context: common.UIAbilityContext, fileName: string): void {
    try {
      const buffer = context.resourceManager.getRawFileContentSync(fileName);
      const decoder = util.TextDecoder.create('utf-8', { ignoreBOM: true });
      const text = decoder.decodeToString(new Uint8Array(buffer));
      DokitThirdLibInfo.parseJson(text);
    } catch (_err) {
      // rawfile 不存在时忽略
    }
  }

  static parseJson(text: string): void {
    const parsed: Object = JSON.parse(text);
    if (Array.isArray(parsed)) {
      parsed.forEach((entry: Object) => {
        const record = entry as Record<string, Object>;
        const name = record['name'];
        const size = record['size'];
        if (typeof name === 'string' && (typeof size === 'number' || typeof size === 'string')) {
          DokitThirdLibInfo.thirdLibInfos.set(name, `${size}`);
        }
      });
    } else {
      const record = parsed as Record<string, Object>;
      const keys = Object.keys(record);
      keys.forEach((key: string) => {
        const value = record[key];
        if (typeof value === 'number' || typeof value === 'string') {
          DokitThirdLibInfo.thirdLibInfos.set(key, `${value}`);
        }
      });
    }
    DokitThirdLibInfo.rebuildSimple();
  }

  private static rebuildSimple(): void {
    DokitThirdLibInfo.thirdLibInfosSimple.clear();
    DokitThirdLibInfo.thirdLibInfos.forEach((value: string, key: string) => {
      const parts = key.split(':');
      if (parts.length === 3) {
        DokitThirdLibInfo.thirdLibInfosSimple.set(`${parts[0]}:${parts[1]}`, value);
      }
    });
  }
}
