package com.didichuxing.doraemonkit.kit.layoutborder

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.DokitViewInterface
import java.util.*

/**
 * Created by wanglikun on 2019/1/12
 */
class ViewBorderFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    private var open = true

    init {
        id = R.id.dokit_view_border_id
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (open) {
            traverseChild(this)
        } else {
            clearChild(this)
        }
    }

    private fun traverseChild(view: View) {
        //过滤掉dokitView
        replaceDrawable(view)
        if (view is ViewGroup && view !is DokitViewInterface) {
            val childCount = view.childCount
            if (childCount != 0) {
                for (index in 0 until childCount) {
                    traverseChild(view.getChildAt(index))
                }
            }
        }
    }

    private fun replaceDrawable(view: View) {
        if (view is TextureView) {
            // 过滤TextureView
            return
        }
        val newDrawable: LayerDrawable
        newDrawable = if (view.background != null) {
            val oldDrawable = view.background
            if (oldDrawable is LayerDrawable) {
                for (i in 0 until oldDrawable.numberOfLayers) {
                    if (oldDrawable.getDrawable(i) is ViewBorderDrawable) {
                        // already replace
                        return
                    }
                }
            }
            LayerDrawable(arrayOf(
                    oldDrawable,
                    ViewBorderDrawable(view)
            ))
        } else {
            LayerDrawable(arrayOf<Drawable>(
                    ViewBorderDrawable(view)
            ))
        }
        try {
            view.background = newDrawable
        } catch (e: UnsupportedOperationException) {
            e.printStackTrace()
        }
    }

    fun close() {
        open = false
        requestLayout()
    }

    private fun clearChild(view: View) {
        clearDrawable(view)
        if (view is ViewGroup) {
            val childCount = view.childCount
            if (childCount != 0) {
                for (index in 0 until childCount) {
                    clearChild(view.getChildAt(index))
                }
            }
        }
    }

    private fun clearDrawable(view: View) {
        if (view.background == null) {
            return
        }
        val oldDrawable = view.background as? LayerDrawable ?: return
        val drawables: MutableList<Drawable> = ArrayList()
        for (i in 0 until oldDrawable.numberOfLayers) {
            if (oldDrawable.getDrawable(i) is ViewBorderDrawable) {
                continue
            }
            drawables.add(oldDrawable.getDrawable(i))
        }
        val newDrawable = LayerDrawable(drawables.toTypedArray())
        view.background = newDrawable
    }

    companion object {
        private const val TAG = "ViewBorderFrameLayout"
    }
}