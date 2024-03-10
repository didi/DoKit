package com.didichuxing.doraemonkit.constant;

/**
 * Created by wanglikun on 2018/9/14.
 */

public interface SharedPrefsKey {
    String FRAME_INFO_FPS_OPEN = "frame_info_fps_open";
    String FRAME_INFO_CPU_OPEN = "frame_info_cpu_open";
    String FRAME_INFO_MEMORY_OPEN = "frame_info_memory_open";
    String FRAME_INFO_TRAFFIC_OPEN = "frame_info_traffic_open";
    String FRAME_INFO_UI_OPEN = "frame_info_ui_open";

    String GPS_MOCK_OPEN = "gps_mock_open";
    String POS_MOCK_OPEN = "pos_mock_open";
    String ROUTE_MOCK_OPEN = "route_mock_open";
    String ROUTE_MOCK_SPEED = "route_mock_speed";
    String ROUTE_MOCK_ACCURACY = "route_mock_accuracy";
    String ROUTE_DRIFT_MOCK_OPEN = "route_drift_mock_open";
    String ROUTE_DRIFT_MOCK_LOST_LOC_OPEN = "route_drift_mock_lost_loc_open";
    String ROUTE_DRIFT_MODE= "route_drift_mode";
    String ROUTE_DRIFT_TYPE = "route_drift_type";
    String ROUTE_DRIFT_SEEKBAR_PROGRESS_LOW = "route_drift_seekbar_progress_low";
    String ROUTE_DRIFT_SEEKBAR_PROGRESS_HIGH = "route_drift_seekbar_progress_high";
    String ROUTE_DRIFT_LOST_LOC_SEEKBAR_PROGRESS_LOW = "route_drift_lost_loc_seekbar_progress_low";
    String ROUTE_DRIFT_LOST_LOC_SEEKBAR_PROGRESS_HIGH = "route_drift_lost_loc_seekbar_progress_high";

    String CRASH_CAPTURE_OPEN = "crash_capture_open";
    String FLOAT_ICON_POS_X = "float_icon_pos_x";
    String FLOAT_ICON_POS_Y = "float_icon_pos_y";
    String LOG_INFO_OPEN = "log_info_open";
    String COLOR_PICK_OPEN = "color_pick_open";
    String ALIGN_RULER_OPEN = "align_ruler_open";
    String VIEW_CHECK_OPEN = "view_check_open";
    String LAYOUT_BORDER_OPEN = "layout_border_open";
    String LAYOUT_LEVEL_OPEN = "layout_level_open";
    String TOP_ACTIVITY_OPEN = "top_activity_open";
    /**
     * 悬浮窗启动模式
     */
    String FLOAT_START_MODE = "float_start_mode";
    /**
     * 大图检测开关
     */
    String LARGE_IMG_OPEN = "large_img_open";
    /**
     * 大图内存检测阈值
     */
    String LARGE_IMG_MEMORY_THRESHOLD = "large_img_memory_threshold";

    /**
     * 大图文件检测阈值
     */
    String LARGE_IMG_FILE_THRESHOLD = "large_img_file_threshold";

    /**
     * 是否处于健康体检状态的key
     */

    String APP_HEALTH = "APP_HEALTH";

}
