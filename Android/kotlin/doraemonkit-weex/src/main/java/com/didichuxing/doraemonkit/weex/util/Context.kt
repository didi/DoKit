/*
 * Context ktx defined
 *
 * Created by alvince on 2020/7/1
 *
 * @author alvince.zy@gmail.com
 */

package com.didichuxing.doraemonkit.weex.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.Dimension.PX
import androidx.core.content.res.ResourcesCompat

/**
 * Get color with resource-ID by [Context] compatibly
 */
fun Context.getColorCompat(@ColorRes id: Int, theme: Resources.Theme? = null) =
    ResourcesCompat.getColor(resources, id, theme)

/**
 * Get dimension with resource-ID by [Context] directly
 */
fun Context.getDimension(@DimenRes id: Int): Float =
    resources.getDimension(id)

/**
 * Get dimension with resource-ID by [Context] directly
 */
@Dimension(unit = PX)
fun Context.getDimensionPixel(@DimenRes id: Int): Int =
    resources.getDimensionPixelSize(id)

/**
 * Start [Activity] with target check passed
 *
 * @param intent for start activity
 * @param requestCode `0` if no result needed
 * @param option Optional, start options
 */
fun Context.launchActivity(intent: Intent, requestCode: Int = 0, option: Bundle? = null): Boolean =
    packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        ?.let { _ ->
            intent.apply {
                if (this !is ContextThemeWrapper) {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }.also {
                if (requestCode != 0) {
                    (this as? Activity)?.apply {
                        startActivityForResult(it, requestCode, option)
                        return@also
                    }
                }
                startActivity(it, option)
            }
            true
        }
        ?: false
