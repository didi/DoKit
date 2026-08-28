import { KitGroup, KitGroupColors, KitGroupId, KitGroupNames, KitItem } from '../model/KitModels';

/**
 * 内置工具配置，innerKitId 与 Android assets/dokit_system_kits.json 一一对应
 */
export function getSystemKitGroups(): KitGroup[] {
  return [
    buildGroup(KitGroupId.PLATFORM, [
      kit('dokit_sdk_platform_ck_mock', '数据Mock', 'Mock'),
      kit('dokit_sdk_platform_ck_health', '健康体检', '体检'),
      kit('dokit_sdk_platform_ck_filetransfer', '文件同步助手', '同步'),
      kit('dokit_sdk_platform_ck_mc_test', '一机多控 *', 'MC*'),
      kit('dokit_sdk_platform_ck_dokit_connect', 'DoKit Studio', 'Studio'),
      kit('dokit_sdk_platform_ck_dokit_for_web', 'DoKit Web', 'Web'),
      kit('dokit_sdk_platform_ck_mc', '一机多控', '多控'),
      kit('dokit_sdk_platform_ck_autotest', '自动化测试', 'Auto'),
      kit('dokit_sdk_platform_thread_hook', 'pthread hook', 'Hook')
    ]),
    buildGroup(KitGroupId.COMMS, [
      kit('dokit_sdk_comm_ck_appinfo', 'App信息', 'App'),
      kit('dokit_sdk_comm_ck_thirdlibinfo', '三方库信息', '三方'),
      kit('dokit_sdk_comm_ck_devpage', '开发者选项', '开发'),
      kit('dokit_sdk_comm_ck_local_lang', '本地语言', '语言'),
      kit('dokit_sdk_comm_ck_sandbox', '沙盒浏览', '沙盒'),
      kit('dokit_sdk_comm_ck_h5', 'H5任意门', 'H5'),
      kit('dokit_sdk_comm_ck_cache', '清理缓存', '缓存'),
      kit('dokit_sdk_comm_ck_log', '日志', '日志'),
      kit('dokit_sdk_comm_ck_h5kit', 'H5助手', '助手')
    ]),
    buildGroup(KitGroupId.LBS, [
      kit('dokit_sdk_comm_ck_gps', '位置模拟', 'GPS'),
      kit('dokit_sdk_lbs_ck_pos_adjust', '位置微调', '微调'),
      kit('dokit_sdk_lbs_ck_nav', '实时导航', '导航')
    ]),
    buildGroup(KitGroupId.WEEX, [
      kit('dokit_sdk_weex_ck_log', '日志', 'WxLog'),
      kit('dokit_sdk_weex_ck_storage', '缓存', 'WxCache'),
      kit('dokit_sdk_weex_ck_info', '信息', 'WxInfo'),
      kit('dokit_sdk_weex_ck_devtool', 'DevTool', 'Dev')
    ]),
    buildGroup(KitGroupId.PERFORMANCE, [
      kit('dokit_sdk_performance_ck_fps', '帧率', 'FPS'),
      kit('dokit_sdk_performance_ck_cpu', 'CPU', 'CPU'),
      kit('dokit_sdk_performance_ck_arm', '内存', 'RAM'),
      kit('dokit_sdk_performance_ck_network', '网络', '网络'),
      kit('dokit_sdk_comm_ck_crash', 'Crash', 'Crash'),
      kit('dokit_sdk_performance_ck_block', '卡顿', '卡顿'),
      kit('dokit_sdk_performance_ck_img', '大图检测', '大图'),
      kit('dokit_sdk_comm_ck_weaknetwork', '模拟弱网', '弱网'),
      kit('dokit_sdk_performance_ck_open_coast', '启动耗时', '启动'),
      kit('dokit_sdk_performance_ck_hierarchy', 'UI层级', '层级'),
      kit('dokit_sdk_performance_ck_method_coast', '函数耗时', '耗时'),
      kit('dokit_sdk_performance_ck_leak', 'LeakCanary', 'Leak')
    ]),
    buildGroup(KitGroupId.UI, [
      kit('dokit_sdk_ui_ck_color_pick', '取色器', '取色'),
      kit('dokit_sdk_ui_ck_aligin_scaleplate', '对齐标尺', '标尺'),
      kit('dokit_sdk_ui_ck_widget', '控件检查', '控件'),
      kit('dokit_sdk_ui_ck_border', '布局边框', '边框')
    ])
  ];
}

function buildGroup(groupId: KitGroupId, kits: KitItem[]): KitGroup {
  return {
    groupId: groupId as string,
    groupName: KitGroupNames[groupId],
    groupColor: KitGroupColors[groupId],
    kits
  };
}

function kit(innerKitId: string, name: string, iconText: string, checked: boolean = true): KitItem {
  return { innerKitId, name, iconText, checked };
}
