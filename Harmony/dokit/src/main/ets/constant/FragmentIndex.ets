/**
 * UniversalActivity 路由索引，对标 Android constant/FragmentIndex
 */
export class FragmentIndex {
  static readonly FRAGMENT_SYS_INFO: number = 1;
  static readonly FRAGMENT_FILE_EXPLORER: number = 2;
  static readonly FRAGMENT_LOG_INFO_SETTING: number = 3;
  static readonly FRAGMENT_COLOR_PICKER_SETTING: number = 4;
  static readonly FRAGMENT_FRAME_INFO: number = 5;
  static readonly FRAGMENT_GPS_MOCK: number = 6;
  static readonly FRAGMENT_ALIGN_RULER_SETTING: number = 7;
  static readonly FRAGMENT_BLOCK_MONITOR: number = 8;
  static readonly FRAGMENT_WEB_DOOR: number = 9;
  static readonly FRAGMENT_DATA_CLEAN: number = 10;
  static readonly FRAGMENT_CRASH: number = 11;
  static readonly FRAGMENT_NETWORK_MONITOR: number = 13;
  static readonly FRAGMENT_CPU: number = 14;
  static readonly FRAGMENT_RAM: number = 15;
  static readonly FRAGMENT_TIME_COUNTER: number = 17;
  static readonly FRAGMENT_WEB_DOOR_DEFAULT: number = 18;
  static readonly FRAGMENT_TOP_ACTIVITY: number = 20;
  static readonly FRAGMENT_WEAK_NETWORK: number = 21;
  /** 大图检测 */
  static readonly FRAGMENT_LARGE_PICTURE: number = 22;
  /** 数据库远程调试 */
  static readonly FRAGMENT_DB_DEBUG: number = 24;
  /** 数据 Mock */
  static readonly FRAGMENT_NETWORK_MOCK: number = 25;
  /** 数据模板预览 */
  static readonly FRAGMENT_MOCK_TEMPLATE_PREVIEW: number = 26;
  /** 健康体检 */
  static readonly FRAGMENT_HEALTH: number = 27;
  /** APP 启动耗时 */
  static readonly FRAGMENT_APP_START: number = 28;
  /** DoKit 更多页面 */
  static readonly FRAGMENT_DOKIT_MORE: number = 29;
  /** Dokit 功能管理页面 */
  static readonly FRAGMENT_DOKIT_MANAGER: number = 30;
  /** Dokit 文件互传 */
  static readonly FRAGMENT_FILE_TRANSFER: number = 31;
  /** Dokit 通用 webview 页面 */
  static readonly FRAGMENT_WEB: number = 32;
  /** Dokit 三方库信息 */
  static readonly FRAGMENT_THIRD_LIBRARY_INFO: number = 33;
  /** dokit 内置 Fragment 索引 */
  static readonly FRAGMENT_SYSTEM: number = 100;
  /** 用户自定义 Fragment 索引 */
  static readonly FRAGMENT_CUSTOM: number = 101;
}
