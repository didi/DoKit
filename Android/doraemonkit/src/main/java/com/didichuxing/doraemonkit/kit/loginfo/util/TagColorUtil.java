package com.didichuxing.doraemonkit.kit.loginfo.util;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.util.SparseIntArray;
import com.didichuxing.doraemonkit.R;

/**
 * @author: linjizong
 *  2019/4/30
 * @desc:
 */
public class TagColorUtil {
    private static final SparseIntArray TEXT_COLOR = new SparseIntArray(6);
    private static final SparseIntArray TEXT_COLOR_EXPAND = new SparseIntArray(6);
    private static final SparseIntArray LEVEL_COLOR = new SparseIntArray(6);
    private static final SparseIntArray LEVEL_BG_COLOR = new SparseIntArray(6);

    static {
        TEXT_COLOR.put(Log.DEBUG, R.color.dk_color_000000);
        TEXT_COLOR.put(Log.INFO, R.color.dk_color_000000);
        TEXT_COLOR.put(Log.VERBOSE, R.color.dk_color_000000);
        TEXT_COLOR.put(Log.ASSERT, R.color.dk_color_8F0005);
        TEXT_COLOR.put(Log.ERROR, R.color.dk_color_FF0006);
        TEXT_COLOR.put(Log.WARN, R.color.dk_color_0099dd);

        TEXT_COLOR_EXPAND.put(Log.DEBUG, R.color.dk_color_FFFFFF);
        TEXT_COLOR_EXPAND.put(Log.INFO, R.color.dk_color_FFFFFF);
        TEXT_COLOR_EXPAND.put(Log.VERBOSE, R.color.dk_color_FFFFFF);
        TEXT_COLOR_EXPAND.put(Log.ASSERT, R.color.dk_color_8F0005);
        TEXT_COLOR_EXPAND.put(Log.ERROR, R.color.dk_color_FF0006);
        TEXT_COLOR_EXPAND.put(Log.WARN, R.color.dk_color_0099dd);

        LEVEL_BG_COLOR.put(Log.DEBUG, R.color.background_debug);
        LEVEL_BG_COLOR.put(Log.ERROR, R.color.background_error);
        LEVEL_BG_COLOR.put(Log.INFO, R.color.background_info);
        LEVEL_BG_COLOR.put(Log.VERBOSE, R.color.background_verbose);
        LEVEL_BG_COLOR.put(Log.WARN, R.color.background_warn);
        LEVEL_BG_COLOR.put(Log.ASSERT, R.color.background_wtf);

        LEVEL_COLOR.put(Log.DEBUG, R.color.foreground_debug);
        LEVEL_COLOR.put(Log.ERROR, R.color.foreground_error);
        LEVEL_COLOR.put(Log.INFO, R.color.foreground_info);
        LEVEL_COLOR.put(Log.VERBOSE, R.color.foreground_verbose);
        LEVEL_COLOR.put(Log.WARN, R.color.foreground_warn);
        LEVEL_COLOR.put(Log.ASSERT, R.color.foreground_wtf);
    }

    public static int getTextColor(Context context, int level, boolean expand) {
        SparseIntArray map = expand ? TEXT_COLOR_EXPAND : TEXT_COLOR;
        Integer result = map.get(level);
        if (result == null) {
            result = map.get(Log.VERBOSE);
        }
        return ContextCompat.getColor(context, result);
    }

    public static int getLevelBgColor(Context context, int level) {
        Integer result = LEVEL_BG_COLOR.get(level);
        if (result == null) {
            result = LEVEL_BG_COLOR.get(Log.VERBOSE);
        }
        return ContextCompat.getColor(context, result);
    }

    public static int getLevelColor(Context context, int level) {
        Integer result = LEVEL_COLOR.get(level);
        if (result == null) {
            result = LEVEL_COLOR.get(Log.VERBOSE);
        }
        return ContextCompat.getColor(context, result);
    }
}
