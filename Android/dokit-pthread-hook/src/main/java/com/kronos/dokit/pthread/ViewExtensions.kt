package com.kronos.dokit.pthread

import android.view.View

/**
 *
 *  @Author LiABao
 *  @Since 2021/11/24
 *
 */

internal fun View.visible() {
    visibility = View.VISIBLE
}

internal fun View.gone() {
    visibility = View.GONE
}

internal fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}
