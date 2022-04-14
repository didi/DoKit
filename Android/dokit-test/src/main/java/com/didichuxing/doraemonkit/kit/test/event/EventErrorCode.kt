package com.didichuxing.doraemonkit.kit.test.event

object EventErrorCode {

    /**
     * 当前页面 Activity 不匹配
     */
    const val ACTIVITY_NOT_MATCH = 1

    /**
     * View 找不到
     */
    const val VIEW_NOT_FIND = 2

    /**
     * Window 找不到
     */
    const val WINDOW_NOT_FIND = 3

    /**
     * 动作无法模拟执行
     */
    const val ACTION_NOT_MOCK = 4

    /**
     * 动作被忽略
     */
    const val ACTION_IGNORE = 6

    /**
     * 信息缺失，不完整
     */
    const val EVENT_ACTION_LOSE = 7

    /**
     * 其他错误
     */
    const val OTHER = 0

}
