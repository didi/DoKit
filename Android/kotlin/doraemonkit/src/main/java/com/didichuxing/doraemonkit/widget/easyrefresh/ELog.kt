package com.didichuxing.doraemonkit.widget.easyrefresh

import android.text.TextUtils
import android.util.Log

object ELog {
    /**
     * Priority constant for the println method; use Log.v.
     */
    const val VERBOSE = 2

    /**
     * Priority constant for the println method; use Log.d.
     */
    const val DEBUG = 3

    /**
     * Priority constant for the println method; use Log.i.
     */
    const val INFO = 4

    /**
     * Priority constant for the println method; use Log.w.
     */
    const val WARN = 5

    /**
     * Priority constant for the println method; use Log.e.
     */
    const val ERROR = 6

    /**
     * Priority constant for the println method.
     */
    const val ASSERT = 7

    /**
     * 检查log是否可用
     * @return boolean isEnabled
     */
    /**
     * 设置log是否可用
     * @param enableBoolean
     */
    /**
     * log是否可用
     */
    var isEnable = true

    private fun log(level: LEVEL, tag: String, msg: String, thr: Throwable?) {
        if (!isEnable) {
            return
        }
        log2console(level, tag, msg, thr)
    }

    private fun log2console(level: LEVEL, tag: String, msg: String, thr: Throwable?) {
        val isFilter = false
        if (!isFilter) {
            when (level) {
                LEVEL.VERBOSE -> if (thr == null) {
                    Log.v(tag, msg)
                } else {
                    Log.v(tag, msg, thr)
                }
                LEVEL.DEBUG -> if (thr == null) {
                    Log.d(tag, msg)
                } else {
                    Log.d(tag, msg, thr)
                }
                LEVEL.INFO -> if (thr == null) {
                    Log.i(tag, msg)
                } else {
                    Log.i(tag, msg, thr)
                }
                LEVEL.WARN -> if (thr == null) {
                    Log.w(tag, msg)
                } else if (TextUtils.isEmpty(msg)) {
                    Log.w(tag, thr)
                } else {
                    Log.w(tag, msg, thr)
                }
                LEVEL.ERROR -> if (thr == null) {
                    Log.e(tag, msg)
                } else {
                    Log.e(tag, msg, thr)
                }
                LEVEL.ASSERT -> if (thr == null) {
                    Log.wtf(tag, msg)
                } else if (TextUtils.isEmpty(msg)) {
                    Log.wtf(tag, thr)
                } else {
                    Log.wtf(tag, msg, thr)
                }
                else -> {
                }
            }
        } else {
            return
        }
    }

    fun v(tag: String, msg: String) {
        log(LEVEL.VERBOSE, tag, msg, null)
    }

    fun v(tag: String, msg: String, thr: Throwable?) {
        log(LEVEL.VERBOSE, tag, msg, thr)
    }

    fun d(tag: String, msg: String) {
        log(LEVEL.DEBUG, tag, msg, null)
    }

    fun d(tag: String, msg: String, thr: Throwable?) {
        log(LEVEL.DEBUG, tag, msg, thr)
    }

    fun i(tag: String, msg: String) {
        log(LEVEL.INFO, tag, msg, null)
    }

    fun i(tag: String, msg: String, thr: Throwable?) {
        log(LEVEL.INFO, tag, msg, thr)
    }

    fun w(tag: String, msg: String) {
        log(LEVEL.WARN, tag, msg, null)
    }

    fun w(tag: String, msg: String, thr: Throwable?) {
        log(LEVEL.WARN, tag, msg, thr)
    }

    fun w(tag: String, thr: Throwable?) {
        log(LEVEL.WARN, tag, "", thr)
    }

    fun e(tag: String, msg: String) {
        log(LEVEL.ERROR, tag, msg, null)
    }

    fun e(tag: String, msg: String, thr: Throwable?) {
        log(LEVEL.ERROR, tag, msg, thr)
    }

    /**
     * 等级枚举，对应android原生log的等级
     * @author jjzheng
     */
    enum class LEVEL {
        VERBOSE(2, "V"), DEBUG(3, "D"), INFO(4, "I"), WARN(5, "W"), ERROR(6, "E"), ASSERT(7, "A");

        var levelString: String?=null
        var level: Int?=null

        //Supress default constructor for noninstantiability
        constructor() {
            throw AssertionError()
        }

        constructor(level: Int, levelString: String) {
            this.level = level
            this.levelString = levelString
        }

    }
}