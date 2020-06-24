package com.didichuxing.doraemonkit.kit.layoutborder

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View

/**
 * Created by wanglikun on 2019/1/11
 */
class ViewBorderDrawable(view: View) : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect: Rect = Rect(0, 0, view.width, view.height)
    private val context: Context = view.context
    override fun draw(canvas: Canvas) {
        canvas.drawRect(rect, paint)
    }

    override fun setAlpha(alpha: Int) {}
    override fun setColorFilter(colorFilter: ColorFilter?) {}
    override fun getOpacity(): Int {
        // to be safe
        return PixelFormat.TRANSLUCENT
    }

    init {
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 4f
        paint.pathEffect = DashPathEffect(floatArrayOf(4f, 4f), 0f)
    }
}