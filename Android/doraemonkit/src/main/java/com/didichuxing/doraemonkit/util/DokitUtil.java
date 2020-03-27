package com.didichuxing.doraemonkit.util;

import android.support.annotation.StringRes;

import com.didichuxing.doraemonkit.DoraemonKit;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/27-15:08
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitUtil {

    public static String getString(@StringRes int stringId) {
        return DoraemonKit.APPLICATION.getString(stringId);
    }
}
