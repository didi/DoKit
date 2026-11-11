import { common } from '@kit.AbilityKit';
import { util } from '@kit.ArkTS';
import { getKitDisplayMeta, getKitDisplayName, getKitIconText } from '../../config/KitDisplayMeta';
import { KitGroup, KitGroupColors, KitGroupId, KitGroupNames, KitItem } from '../../model/KitModels';
import { AbstractKit } from '../../kit/AbstractKit';
import { SystemKitRegistry } from '../SystemKitRegistry';
import { KitBean, KitGroupBean } from './KitBeans';
import { KitWrapItem } from './KitWrapItem';

/** 系统工具配置的 rawfile 文件名。 */
const SYSTEM_KITS_JSON: string = 'dokit_system_kits.json';

/**
 * 工具面板配置解析，对标 Android ToolPanelUtil。
 * 读 dokit_system_kits.json，按 innerKitId 绑上已实现的工具。
 */
export class ToolPanelUtil {
  /** 从 rawfile 读取系统工具。读失败时用内置兜底列表。 */
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

  /** 把配置 JSON 解析成分组表。键是分组 id。 */
  static jsonConfig2InnerKits(json: string): Map<string, KitWrapItem[]> {
    const localConfigs: KitGroupBean[] = ToolPanelUtil.parseJson(json);
    return ToolPanelUtil.buildSystemKits(localConfigs);
  }

  /** 按 innerKitId 绑上已登记的工具。代码里还没有的项仍保留占位。 */
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

  /** 把分组表追加进面板分组。名称和颜色查 KitGroupNames、KitGroupColors。 */
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

  /** 转成面板上的一项。内置工具不挂 customKit，点击时再按 id 去系统表取。 */
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

  /** 解析分组 id，以及每项的 innerKitId、checked、allClassName。 */
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

  /** rawfile 读失败时的兜底：常用工具六项和取色器。 */
  private static loadFallbackKits(): Map<string, KitWrapItem[]> {
    const kits: KitBean[] = [];
    kits.push(new KitBean('dokit_sdk_comm_ck_appinfo', true));
    kits.push(new KitBean('dokit_sdk_comm_ck_thirdlibinfo', true));
    kits.push(new KitBean('dokit_sdk_comm_ck_devpage', true));
    kits.push(new KitBean('dokit_sdk_comm_ck_sandbox', true));
    kits.push(new KitBean('dokit_sdk_comm_ck_h5', true));
    kits.push(new KitBean('dokit_sdk_comm_ck_cache', true));
    const uiKits: KitBean[] = [];
    uiKits.push(new KitBean('dokit_sdk_ui_ck_color_pick', true));
    const groups: KitGroupBean[] = [];
    groups.push(new KitGroupBean(KitGroupId.COMMS, kits));
    groups.push(new KitGroupBean(KitGroupId.UI, uiKits));
    return ToolPanelUtil.buildSystemKits(groups);
  }
}
