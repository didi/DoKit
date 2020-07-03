package com.didichuxing.doraemonkit.constant

/**
 * Created by wanglikun on 2018/10/26.
 */
interface FragmentIndex {
    companion object {
        const val FRAGMENT_SYS_INFO = 1
        const val FRAGMENT_FILE_EXPLORER = 2
        const val FRAGMENT_LOG_INFO_SETTING = 3
        const val FRAGMENT_COLOR_PICKER_SETTING = 4
        const val FRAGMENT_FRAME_INFO = 5
        const val FRAGMENT_GPS_MOCK = 6
        const val FRAGMENT_ALIGN_RULER_SETTING = 7
        const val FRAGMENT_BLOCK_MONITOR = 8
        const val FRAGMENT_WEB_DOOR = 9
        const val FRAGMENT_DATA_CLEAN = 10
        const val FRAGMENT_CRASH = 11
        const val FRAGMENT_NETWORK_MONITOR = 13
        const val FRAGMENT_CPU = 14
        const val FRAGMENT_RAM = 15
        const val FRAGMENT_TIME_COUNTER = 17
        const val FRAGMENT_WEB_DOOR_DEFAULT = 18
        const val FRAGMENT_CUSTOM = 19

        //int FRAGMENT_TOP_ACTIVITY = 20;
        const val FRAGMENT_WEAK_NETWORK = 21

        /**
         * 大图检测
         */
        const val FRAGMENT_LARGE_PICTURE = 22

        /**
         * 函数耗时
         */
        const val FRAGMENT_METHOD_COST = 23

        /**
         * 数据库远程调试
         */
        const val FRAGMENT_DB_DEBUG = 24

        /**
         * 数据Mock
         */
        const val FRAGMENT_NETWORK_MOCK = 25

        /**
         * 数据模板预览
         */
        const val FRAGMENT_MOCK_TEMPLATE_PREVIEW = 26

        /**
         * 健康体检
         */
        const val FRAGMENT_HEALTH = 27

        /**
         * APP启动耗时
         */
        const val FRAGMENT_APP_RECORD_DETAIL = 28

        /**
         * DoKit 设置页面
         */
        const val FRAGMENT_DOKIT_SETTING = 29

        /**
         * Dokit 功能管理页面
         */
        const val FRAGMENT_DOKIT_MANAGER = 30
    }
}