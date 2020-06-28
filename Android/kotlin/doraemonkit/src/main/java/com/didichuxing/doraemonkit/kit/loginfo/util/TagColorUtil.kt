package com.didichuxing.doraemonkit.kit.loginfo.util

import android.content.Context
import android.util.Log
import android.util.SparseIntArray
import androidx.core.content.ContextCompat
import com.didichuxing.doraemonkit.R

/**
 * @author: linjizong
 * 2019/4/30
 * @desc:
 */
object TagColorUtil {
    private val TEXT_COLOR = SparseIntArray(6)
    private val TEXT_COLOR_EXPAND = SparseIntArray(6)
    private val LEVEL_COLOR = SparseIntArray(6)
    private val LEVEL_BG_COLOR = SparseIntArray(6)

    fun getTextColor(context: Context, level: Int, expand: Boolean): Int {
        val map = if (expand) TEXT_COLOR_EXPAND else TEXT_COLOR
        val result = map.get(level, map[Log.VERBOSE])
        return ContextCompat.getColor(context, result)
    }

    fun getLevelBgColor(context: Context, level: Int): Int {
        val result = LEVEL_BG_COLOR.get(level, LEVEL_BG_COLOR[Log.VERBOSE])
        return ContextCompat.getColor(context, result)
    }

    fun getLevelColor(context: Context, level: Int): Int {
        val result = LEVEL_COLOR.get(level, LEVEL_COLOR[Log.VERBOSE])
        return ContextCompat.getColor(context, result)
    }

    init {
        TEXT_COLOR.put(Log.DEBUG, R.color.dk_color_000000)
        TEXT_COLOR.put(Log.INFO, R.color.dk_color_000000)
        TEXT_COLOR.put(Log.VERBOSE, R.color.dk_color_000000)
        TEXT_COLOR.put(Log.ASSERT, R.color.dk_color_8F0005)
        TEXT_COLOR.put(Log.ERROR, R.color.dk_color_FF0006)
        TEXT_COLOR.put(Log.WARN, R.color.dk_color_0099dd)
        TEXT_COLOR_EXPAND.put(Log.DEBUG, R.color.dk_color_FFFFFF)
        TEXT_COLOR_EXPAND.put(Log.INFO, R.color.dk_color_FFFFFF)
        TEXT_COLOR_EXPAND.put(Log.VERBOSE, R.color.dk_color_FFFFFF)
        TEXT_COLOR_EXPAND.put(Log.ASSERT, R.color.dk_color_8F0005)
        TEXT_COLOR_EXPAND.put(Log.ERROR, R.color.dk_color_FF0006)
        TEXT_COLOR_EXPAND.put(Log.WARN, R.color.dk_color_0099dd)
        LEVEL_BG_COLOR.put(Log.DEBUG, R.color.background_debug)
        LEVEL_BG_COLOR.put(Log.ERROR, R.color.background_error)
        LEVEL_BG_COLOR.put(Log.INFO, R.color.background_info)
        LEVEL_BG_COLOR.put(Log.VERBOSE, R.color.background_verbose)
        LEVEL_BG_COLOR.put(Log.WARN, R.color.background_warn)
        LEVEL_BG_COLOR.put(Log.ASSERT, R.color.background_wtf)
        LEVEL_COLOR.put(Log.DEBUG, R.color.foreground_debug)
        LEVEL_COLOR.put(Log.ERROR, R.color.foreground_error)
        LEVEL_COLOR.put(Log.INFO, R.color.foreground_info)
        LEVEL_COLOR.put(Log.VERBOSE, R.color.foreground_verbose)
        LEVEL_COLOR.put(Log.WARN, R.color.foreground_warn)
        LEVEL_COLOR.put(Log.ASSERT, R.color.foreground_wtf)
    }
}