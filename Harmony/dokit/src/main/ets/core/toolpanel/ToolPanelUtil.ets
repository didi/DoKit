import { common } from '@kit.AbilityKit';
import { util } from '@kit.ArkTS';
import { getKitDisplayMeta, getKitDisplayName, getKitIconText } from '../../config/KitDisplayMeta';
import { KitGroup, KitGroupColors, KitGroupId, KitGroupNames, KitItem } from '../../model/KitModels';
import { AbstractKit } from '../../kit/AbstractKit';
import { SystemKitRegistry } from '../SystemKitRegistry';
import { KitBean, KitGroupBean } from './KitBeans';
import { KitWrapItem } from './KitWrapItem';

const SYSTEM_KITS_JSON: string = 'dokit_system_kits.json';

/**
 * 工具面板配置解析，对标 Android ToolPanelUtil
 */
export class ToolPanelUtil {
  static loadSystemKitsFromRawFile(context: common.UIAbilityContext): Map<string, KitWrapItem[]> {
    try {
      const buffer = context.resourceManager.getRawFileContentSync(SYSTEM_KITS_JSON);
      const decoder = util.TextDecoder.create('utf-8', { ignoreBOM: true });
      const json = decoder.decodeToString(new Uint8Array(buffer));
      return ToolPanelUtil.jsonConfig2InnerKits(json);
    } catch (_err) {
      return ToolPanelUtil.loadFallbackKits();
    }
  }

  static jsonConfig2InnerKits(json: string): Map<string, KitWrapItem[]> {
    const localConfigs: KitGroupBean[] = ToolPanelUtil.parseJson(json);
    return ToolPanelUtil.buildSystemKits(localConfigs);
  }

  private static buildSystemKits(localConfigs: KitGroupBean[]): Map<string, KitWrapItem[]> {
    const systemKits: Map<string, KitWrapItem[]> = new Map();
    localConfigs.forEach((group: KitGroupBean) => {
      const wrapItems: KitWrapItem[] = [];
      group.kits.forEach((kitBean: KitBean) => {
        const kit: AbstractKit | undefined = SystemKitRegistry.get(kitBean.innerKitId);
        const name = kit !== undefined ? kit.getName() : getKitDisplayName(kitBean.innerKitId);
        wrapItems.push(new KitWrapItem(
          KitWrapItem.TYPE_KIT,
          name,
          kitBean.checked,
          group.groupId,
          kit ?? null,
          kitBean.innerKitId
        ));
        if (getKitDisplayMeta(kitBean.innerKitId) === undefined && kit === undefined) {
          // keep placeholder even without meta
        }
      });
      systemKits.set(group.groupId, wrapItems);
    });
    return systemKits;
  }

  static toKitGroups( groups: KitGroup[] ,globalKits: Map<string, KitWrapItem[]>): KitGroup[] {
    globalKits.forEach((wrapItems: KitWrapItem[], groupId: string) => {
      const groupEnum = groupId as KitGroupId;
      const groupName = KitGroupNames[groupEnum] ?? groupId;
      const groupColor = KitGroupColors[groupEnum] ?? '#666666';
      const kits: KitItem[] = wrapItems.map((wrap: KitWrapItem) => ToolPanelUtil.toKitItem(wrap));
      groups.push({ groupId, groupName, groupColor, kits });
    });
    return groups;
  }

  private static toKitItem(wrap: KitWrapItem): KitItem {
    const item: KitItem = {
      innerKitId: wrap.innerKitId,
      name: wrap.name,
      iconText: getKitIconText(wrap.innerKitId),
      checked: wrap.checked
    };
    if (wrap.kit !== null) {
      item.iconResource = wrap.kit.getIcon();
      if (!wrap.kit.isInnerKit) {
        item.customKit = wrap.kit;
      }
    }
    return item;
  }

  private static parseJson(json: string): KitGroupBean[] {
    const parsed = JSON.parse(json) as Array<Record<string, Object>>;
    const result: KitGroupBean[] = [];
    for (let i = 0; i < parsed.length; i++) {
      const groupObj = parsed[i];
      const groupId = `${groupObj['groupId'] ?? ''}`;
      const kitArray = groupObj['kits'] as Array<Record<string, Object>>;
      const kits: KitBean[] = [];
      for (let j = 0; j < kitArray.length; j++) {
        const kitObj = kitArray[j];
        kits.push(new KitBean(
          `${kitObj['innerKitId'] ?? ''}`,
          kitObj['checked'] === true,
          `${kitObj['allClassName'] ?? ''}`
        ));
      }
      result.push(new KitGroupBean(groupId, kits));
    }
    return result;
  }

  private static loadFallbackKits(): Map<string, KitWrapItem[]> {
    const kits: KitBean[] = [];
    kits.push(new KitBean('dokit_sdk_comm_ck_appinfo', true));
    kits.push(new KitBean('dokit_sdk_comm_ck_thirdlibinfo', true));
    kits.push(new KitBean('dokit_sdk_comm_ck_devpage', true));
    kits.push(new KitBean('dokit_sdk_comm_ck_sandbox', true));
    kits.push(new KitBean('dokit_sdk_comm_ck_h5', true));
    kits.push(new KitBean('dokit_sdk_comm_ck_cache', true));
    const groups: KitGroupBean[] = [];
    groups.push(new KitGroupBean(KitGroupId.COMMS, kits));
    return ToolPanelUtil.buildSystemKits(groups);
  }
}
