/**
 * 内置 Kit 展示文案，与 Android strings 及 dokit_system_kits.json 对应
 * 未实现 SPI 注册的 Kit 仍可在面板占位展示
 */
export class KitDisplayMeta {
  name: string;
  iconText: string;

  constructor(name: string, iconText: string) {
    this.name = name;
    this.iconText = iconText;
  }
}

function meta(name: string, iconText: string): KitDisplayMeta {
  return new KitDisplayMeta(name, iconText);
}

function buildKitMetaMap(): Map<string, KitDisplayMeta> {
  const kitMeta: Map<string, KitDisplayMeta> = new Map();
  kitMeta.set('dokit_sdk_platform_ck_mock', meta('数据Mock', 'Mock'));
  kitMeta.set('dokit_sdk_platform_ck_health', meta('健康体检', '体检'));
  kitMeta.set('dokit_sdk_platform_ck_filetransfer', meta('文件同步助手', '同步'));
  kitMeta.set('dokit_sdk_platform_ck_mc_test', meta('一机多控 *', 'MC*'));
  kitMeta.set('dokit_sdk_platform_ck_dokit_connect', meta('DoKit Studio', 'Studio'));
  kitMeta.set('dokit_sdk_platform_ck_dokit_for_web', meta('DoKit Web', 'Web'));
  kitMeta.set('dokit_sdk_platform_ck_mc', meta('一机多控', '多控'));
  kitMeta.set('dokit_sdk_platform_ck_autotest', meta('自动化测试', 'Auto'));
  kitMeta.set('dokit_sdk_platform_thread_hook', meta('pthread hook', 'Hook'));
  kitMeta.set('dokit_sdk_comm_ck_appinfo', meta('App信息', 'App'));
  kitMeta.set('dokit_sdk_comm_ck_thirdlibinfo', meta('三方库信息', '三方'));
  kitMeta.set('dokit_sdk_comm_ck_devpage', meta('开发者选项', '开发'));
  kitMeta.set('dokit_sdk_comm_ck_local_lang', meta('本地语言', '语言'));
  kitMeta.set('dokit_sdk_comm_ck_sandbox', meta('沙盒浏览', '沙盒'));
  kitMeta.set('dokit_sdk_comm_ck_h5', meta('H5任意门', 'H5'));
  kitMeta.set('dokit_sdk_comm_ck_cache', meta('清理缓存', '缓存'));
  kitMeta.set('dokit_sdk_comm_ck_log', meta('日志', '日志'));
  kitMeta.set('dokit_sdk_comm_ck_h5kit', meta('H5助手', '助手'));
  kitMeta.set('dokit_sdk_comm_ck_gps', meta('位置模拟', 'GPS'));
  kitMeta.set('dokit_sdk_lbs_ck_pos_adjust', meta('位置微调', '微调'));
  kitMeta.set('dokit_sdk_lbs_ck_nav', meta('实时导航', '导航'));
  kitMeta.set('dokit_sdk_weex_ck_log', meta('日志', 'WxLog'));
  kitMeta.set('dokit_sdk_weex_ck_storage', meta('缓存', 'WxCache'));
  kitMeta.set('dokit_sdk_weex_ck_info', meta('信息', 'WxInfo'));
  kitMeta.set('dokit_sdk_weex_ck_devtool', meta('DevTool', 'Dev'));
  kitMeta.set('dokit_sdk_performance_ck_fps', meta('帧率', 'FPS'));
  kitMeta.set('dokit_sdk_performance_ck_cpu', meta('CPU', 'CPU'));
  kitMeta.set('dokit_sdk_performance_ck_arm', meta('内存', 'RAM'));
  kitMeta.set('dokit_sdk_performance_ck_network', meta('网络', '网络'));
  kitMeta.set('dokit_sdk_comm_ck_crash', meta('Crash', 'Crash'));
  kitMeta.set('dokit_sdk_performance_ck_block', meta('卡顿', '卡顿'));
  kitMeta.set('dokit_sdk_performance_ck_img', meta('大图检测', '大图'));
  kitMeta.set('dokit_sdk_comm_ck_weaknetwork', meta('模拟弱网', '弱网'));
  kitMeta.set('dokit_sdk_performance_ck_open_coast', meta('启动耗时', '启动'));
  kitMeta.set('dokit_sdk_performance_ck_hierarchy', meta('UI层级', '层级'));
  kitMeta.set('dokit_sdk_performance_ck_method_coast', meta('函数耗时', '耗时'));
  kitMeta.set('dokit_sdk_performance_ck_leak', meta('LeakCanary', 'Leak'));
  kitMeta.set('dokit_sdk_ui_ck_color_pick', meta('取色器', '取色'));
  kitMeta.set('dokit_sdk_ui_ck_aligin_scaleplate', meta('对齐标尺', '标尺'));
  kitMeta.set('dokit_sdk_ui_ck_widget', meta('控件检查', '控件'));
  kitMeta.set('dokit_sdk_ui_ck_border', meta('布局边框', '边框'));
  return kitMeta;
}

const KIT_META: Map<string, KitDisplayMeta> = buildKitMetaMap();

export function getKitDisplayMeta(innerKitId: string): KitDisplayMeta | undefined {
  return KIT_META.get(innerKitId);
}

export function getKitDisplayName(innerKitId: string): string {
  const displayMeta = KIT_META.get(innerKitId);
  if (displayMeta !== undefined) {
    return displayMeta.name;
  }
  return innerKitId;
}

export function getKitIconText(innerKitId: string): string {
  const displayMeta = KIT_META.get(innerKitId);
  if (displayMeta !== undefined) {
    return displayMeta.iconText;
  }
  return innerKitId.substring(0, 4);
}
