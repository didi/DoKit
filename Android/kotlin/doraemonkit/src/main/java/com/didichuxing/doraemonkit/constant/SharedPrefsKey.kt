package com.didichuxing.doraemonkit.constant

/**
 * Created by wanglikun on 2018/9/14.
 */
interface SharedPrefsKey {
    companion object {
        const val FRAME_INFO_FPS_OPEN = "frame_info_fps_open"
        const val FRAME_INFO_CPU_OPEN = "frame_info_cpu_open"
        const val FRAME_INFO_MEMORY_OPEN = "frame_info_memory_open"
        const val FRAME_INFO_TRAFFIC_OPEN = "frame_info_traffic_open"
        const val FRAME_INFO_UI_OPEN = "frame_info_ui_open"
        const val GPS_MOCK_OPEN = "gps_mock_open"
        const val CRASH_CAPTURE_OPEN = "crash_capture_open"
        const val FLOAT_ICON_POS_X = "float_icon_pos_x"
        const val FLOAT_ICON_POS_Y = "float_icon_pos_y"
        const val LOG_INFO_OPEN = "log_info_open"
        const val COLOR_PICK_OPEN = "color_pick_open"
        const val ALIGN_RULER_OPEN = "align_ruler_open"
        const val VIEW_CHECK_OPEN = "view_check_open"
        const val LAYOUT_BORDER_OPEN = "layout_border_open"
        const val LAYOUT_LEVEL_OPEN = "layout_level_open"
        const val TOP_ACTIVITY_OPEN = "top_activity_open"

        /**
         * 悬浮窗启动模式
         */
        const val FLOAT_START_MODE = "float_start_mode"

        /**
         * 大图检测开关
         */
        const val LARGE_IMG_OPEN = "large_img_open"

        /**
         * 大图内存检测阈值
         */
        const val LARGE_IMG_MEMORY_THRESHOLD = "large_img_memory_threshold"

        /**
         * 大图文件检测阈值
         */
        const val LARGE_IMG_FILE_THRESHOLD = "large_img_file_threshold"

        /**
         * 是否处于健康体检状态的key
         */
        const val APP_HEALTH = "APP_HEALTH"
    }
}