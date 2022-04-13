package com.didichuxing.doraemonkit.kit.test.event


/**
 * didi Create on 2022/4/13 .
 *
 * Copyright (c) 2022/4/13 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/13 3:07 下午
 * @Description 用一句话说明文件功能
 */

enum class ActionType(private val id: Int, private val nameText: String) {
    UNKNOWN(0, "未知"),
    ON_CLICK(1, "点击"),
    ON_LONG_CLICK(2, "长按"),
    ON_SCROLL(3, "滑动"),
    ON_FOCUS_CHANGE(4, "焦点改变"),
    ON_INPUT_CHANGE(5, "输入"),
    ON_TOUCH(6, "TOUCH"),
    ON_TOUCH_START(7, "TOUCH START"),
    ON_TOUCH_END(8, "TOUCH END"),
    ON_TOUCH_MOVE(9, "TOUCH MOCVE"),
    ON_DBL_CLICK(10, "双击"),
    ON_CUSTOM_EVENT(30, "自定义");


    fun getID(): Int {
        return id
    }

    fun getDesc(): String {
        return nameText
    }
}
