/**
 * Preferences 存储 key，对标 Android constant/SharedPrefsKey
 */
export class SharedPrefsKey {
  static readonly FRAME_INFO_FPS_OPEN: string = 'frame_info_fps_open';
  static readonly FRAME_INFO_CPU_OPEN: string = 'frame_info_cpu_open';
  static readonly FRAME_INFO_MEMORY_OPEN: string = 'frame_info_memory_open';
  static readonly FRAME_INFO_TRAFFIC_OPEN: string = 'frame_info_traffic_open';
  static readonly FRAME_INFO_UI_OPEN: string = 'frame_info_ui_open';

  static readonly GPS_MOCK_OPEN: string = 'gps_mock_open';
  static readonly POS_MOCK_OPEN: string = 'pos_mock_open';
  static readonly ROUTE_MOCK_OPEN: string = 'route_mock_open';
  static readonly ROUTE_MOCK_SPEED: string = 'route_mock_speed';
  static readonly ROUTE_MOCK_ACCURACY: string = 'route_mock_accuracy';
  static readonly ROUTE_DRIFT_MOCK_OPEN: string = 'route_drift_mock_open';
  static readonly ROUTE_DRIFT_MOCK_LOST_LOC_OPEN: string = 'route_drift_mock_lost_loc_open';
  static readonly ROUTE_DRIFT_MODE: string = 'route_drift_mode';
  static readonly ROUTE_DRIFT_TYPE: string = 'route_drift_type';
  static readonly ROUTE_DRIFT_SEEKBAR_PROGRESS_LOW: string = 'route_drift_seekbar_progress_low';
  static readonly ROUTE_DRIFT_SEEKBAR_PROGRESS_HIGH: string = 'route_drift_seekbar_progress_high';
  static readonly ROUTE_DRIFT_LOST_LOC_SEEKBAR_PROGRESS_LOW: string = 'route_drift_lost_loc_seekbar_progress_low';
  static readonly ROUTE_DRIFT_LOST_LOC_SEEKBAR_PROGRESS_HIGH: string = 'route_drift_lost_loc_seekbar_progress_high';

  static readonly CRASH_CAPTURE_OPEN: string = 'crash_capture_open';
  static readonly FLOAT_ICON_POS_X: string = 'float_icon_pos_x';
  static readonly FLOAT_ICON_POS_Y: string = 'float_icon_pos_y';
  static readonly LOG_INFO_OPEN: string = 'log_info_open';
  static readonly COLOR_PICK_OPEN: string = 'color_pick_open';
  static readonly ALIGN_RULER_OPEN: string = 'align_ruler_open';
  static readonly VIEW_CHECK_OPEN: string = 'view_check_open';
  static readonly LAYOUT_BORDER_OPEN: string = 'layout_border_open';
  static readonly LAYOUT_LEVEL_OPEN: string = 'layout_level_open';
  static readonly TOP_ACTIVITY_OPEN: string = 'top_activity_open';
  /** 悬浮窗启动模式 */
  static readonly FLOAT_START_MODE: string = 'float_start_mode';
  /** 大图检测开关 */
  static readonly LARGE_IMG_OPEN: string = 'large_img_open';
  /** 大图内存检测阈值 */
  static readonly LARGE_IMG_MEMORY_THRESHOLD: string = 'large_img_memory_threshold';
  /** 大图文件检测阈值 */
  static readonly LARGE_IMG_FILE_THRESHOLD: string = 'large_img_file_threshold';
  /** 是否处于健康体检状态 */
  static readonly APP_HEALTH: string = 'APP_HEALTH';
}
