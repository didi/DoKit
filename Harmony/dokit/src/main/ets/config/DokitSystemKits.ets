import { DoKitManager } from '../core/DoKitManager';
import { ToolPanelUtil } from '../core/toolpanel/ToolPanelUtil';
import { KitGroup } from '../model/KitModels';

/**
 * 内置工具配置入口，对标 Android assets/dokit_system_kits.json + ToolPanelUtil
 * @deprecated 请使用 DoKitManager.getKitGroups()
 */
export function getSystemKitGroups(): KitGroup[] {
  return ToolPanelUtil.toKitGroups([],DoKitManager.GLOBAL_KITS);
}
