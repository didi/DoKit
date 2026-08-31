/**
 * Kit 分组 ID，与 Android DoKitManager 及 dokit_system_kits.json 保持一致
 */
import { AbstractKit } from '../kit/AbstractKit';

export enum KitGroupId {
  PLATFORM = 'dk_category_platform',
  COMMS = 'dk_category_comms',
  LBS = 'dk_category_lbs',
  WEEX = 'dk_category_weex',
  PERFORMANCE = 'dk_category_performance',
  UI = 'dk_category_ui'
}

/** 分组显示名称 */
export const KitGroupNames: Record<KitGroupId, string> = {
  [KitGroupId.PLATFORM]: '平台工具',
  [KitGroupId.COMMS]: '常用工具',
  [KitGroupId.LBS]: 'LBS',
  [KitGroupId.WEEX]: 'Weex',
  [KitGroupId.PERFORMANCE]: '性能监控',
  [KitGroupId.UI]: '视觉工具'
};

/** 分组主题色，用于图标占位 */
export const KitGroupColors: Record<KitGroupId, string> = {
  [KitGroupId.PLATFORM]: '#5B8FF9',
  [KitGroupId.COMMS]: '#5AD8A6',
  [KitGroupId.LBS]: '#F6BD16',
  [KitGroupId.WEEX]: '#E86452',
  [KitGroupId.PERFORMANCE]: '#9270CA',
  [KitGroupId.UI]: '#FF9845'
};

export interface KitItem {
  /** 与 Android innerKitId 一致；业务 Kit 可自动生成 */
  innerKitId: string;
  /** 工具显示名称 */
  name: string;
  /** 图标占位文字（内置 Kit 使用） */
  iconText: string;
  /** 是否在面板默认展示 */
  checked: boolean;
  /** 业务 Kit 自定义图标，对标 Android AbstractKit.icon */
  iconResource?: Resource;
  /** 业务自定义 Kit 实例，对标 Android KitWrapItem.kit */
  customKit?: AbstractKit;
}

export interface KitGroup {
  /** 与 Android dokit_system_kits.json groupId 一致 */
  groupId: string;
  groupName: string;
  groupColor: string;
  kits: KitItem[];
}
