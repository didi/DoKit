package com.didichuxing.doraemonkit.widget.recyclerview

import android.annotation.TargetApi
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import kotlin.math.roundToInt

/**
 * Created by jinliang on 2017/9/29.
 */
class DividerItemDecoration(orientation: Int) : ItemDecoration() {
    private var mDivider: Drawable? = null
    private var mOrientation = 0
    private val mBounds = Rect()
    private var mShowHeaderDivider = false
    private var mShowFooterDivider = true


    fun setOrientation(orientation: Int) {
        require(!(orientation != 0 && orientation != 1)) { "Invalid orientation. It should be either HORIZONTAL or VERTICAL" }
        mOrientation = orientation
    }

    fun setDrawable(drawable: Drawable?): DividerItemDecoration {
        mDivider = drawable
        return this
    }

    override fun onDrawOver(
            c: Canvas,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        if (parent.layoutManager != null && mDivider != null) {
            if (mOrientation == 1) {
                drawVertical(c, parent)
            } else {
                drawHorizontal(c, parent)
            }
        }
    }

    fun showHeaderDivider(show: Boolean) {
        mShowHeaderDivider = show
    }

    fun showFooterDivider(show: Boolean) {
        mShowFooterDivider = show
    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            val top = parent.paddingTop
            val bottom = parent.height - parent.paddingBottom
            canvas.clipRect(left, top, right, bottom)
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.getDecoratedBoundsWithMargins(child, mBounds)
            var top: Int
            var bottom: Int
            if (i == 0 && mShowHeaderDivider) {
                top = 0
                mDivider!!.setBounds(left, top, right, top + mDivider!!.intrinsicHeight)
                mDivider!!.draw(canvas)
            }
            if (i != childCount - 1 || mShowFooterDivider) {
                bottom = mBounds.bottom + Math.round(child.translationY)
                top = bottom - mDivider!!.intrinsicHeight
                mDivider!!.setBounds(left, top, right, bottom)
                mDivider!!.draw(canvas)
            }
        }
        canvas.restore()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int
        if (parent.clipToPadding) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                    parent.paddingLeft,
                    top,
                    parent.width - parent.paddingRight,
                    bottom
            )
        } else {
            top = 0
            bottom = parent.height
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            parent.layoutManager!!.getDecoratedBoundsWithMargins(child, mBounds)
            val right = mBounds.right + child.translationX.roundToInt()
            val left = right - mDivider!!.intrinsicWidth
            mDivider!!.setBounds(left, top, right, bottom)
            mDivider!!.draw(canvas)
        }
        canvas.restore()
    }

    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        if (mDivider == null) {
            outRect[0, 0, 0] = 0
        } else {
            if (mOrientation == 1) {
                outRect[0, 0, 0] = mDivider!!.intrinsicHeight
            } else {
                outRect[0, 0, mDivider!!.intrinsicWidth] = 0
            }
        }
    }

    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
        private const val TAG = "TitleItem"
    }

    init {
        setOrientation(orientation)
    }
}