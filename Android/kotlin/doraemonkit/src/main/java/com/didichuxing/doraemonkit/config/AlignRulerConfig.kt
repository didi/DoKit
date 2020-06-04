package com.didichuxing.doraemonkit.config

import com.didichuxing.doraemonkit.constant.SharedPrefsKey
import com.didichuxing.doraemonkit.util.SharedPrefsUtil

/**
 * @author: xuchun
 * @time: 2020/6/4 - 10:47
 * @desc: 对齐标尺配置
 */
object AlignRulerConfig {
    var isAlignRulerOpen: Boolean
        get() = SharedPrefsUtil.getBoolean(key = SharedPrefsKey.ALIGN_RULER_OPEN, defVal = false)
        set(open) {
            SharedPrefsUtil.putBoolean(SharedPrefsKey.ALIGN_RULER_OPEN, open)
        }

}