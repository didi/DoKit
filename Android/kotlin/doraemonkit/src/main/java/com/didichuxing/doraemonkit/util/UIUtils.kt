package com.didichuxing.doraemonkit.util

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.AnyRes
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.layoutborder.ViewBorderFrameLayout
import java.math.BigDecimal

/**
 * Created by zhangweida on 2018/6/22.
 */
object UIUtils {
    private const val TAG = "UIUtils"

    @JvmStatic
    fun dp2px(dpValue: Float): Int {
        return ConvertUtils.dp2px(dpValue)
    }

    fun px2dp(px: Int): Int {
        return ConvertUtils.px2dp(px.toFloat())
    }

    val density: Float
        get() = ScreenUtils.getScreenDensity()

    val densityDpi: Int
        get() = ScreenUtils.getScreenDensityDpi()

    @JvmStatic
    val widthPixels: Int
        get() {
            val metrics = DisplayMetrics()
            val windowManager = DoraemonKit.APPLICATION!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    ?: return 0
            windowManager.defaultDisplay.getMetrics(metrics)
            return metrics.widthPixels
        }

    val heightPixels: Int
        get() = realHeightPixels - statusBarHeight

    //LogHelper.d(TAG, e.toString());
    @JvmStatic
    val realHeightPixels: Int
        get() {
            val windowManager = DoraemonKit.APPLICATION!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            var height = 0
            val display = windowManager.defaultDisplay
            val dm = DisplayMetrics()
            val c: Class<*>
            try {
                c = Class.forName("android.view.Display")
                val method = c.getMethod("getRealMetrics", DisplayMetrics::class.java)
                method.invoke(display, dm)
                height = dm.heightPixels
            } catch (e: Exception) {
                //LogHelper.d(TAG, e.toString());
            }
            return height
        }

    val statusBarHeight: Int
        get() {
            val resources = DoraemonKit.APPLICATION!!.resources
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }

    @JvmStatic
    fun getViewRect(view: View): Rect {
        val rect = Rect()
        val locations = IntArray(2)
        view.getLocationOnScreen(locations)
        rect.left = locations[0]
        rect.top = locations[1]
        if (!checkStatusBarVisible(view.context)) {
            rect.top -= statusBarHeight
        }
        rect.right = rect.left + view.width
        rect.bottom = rect.top + view.height
        return rect
    }

    fun checkStatusBarVisible(context: Context): Boolean {
        return checkFullScreenByTheme(context) || checkFullScreenByCode(context) || checkFullScreenByCode2(context)
    }

    fun checkFullScreenByTheme(context: Context): Boolean {
        val theme = context.theme
        if (theme != null) {
            val typedValue = TypedValue()
            val result = theme.resolveAttribute(android.R.attr.windowFullscreen, typedValue, false)
            if (result) {
                typedValue.coerceToString()
                if (typedValue.type == TypedValue.TYPE_INT_BOOLEAN) {
                    return typedValue.data != 0
                }
            }
        }
        return false
    }

    fun checkFullScreenByCode(context: Context?): Boolean {
        if (context is Activity) {
            val window = context.window
            if (window != null) {
                val decorView = window.decorView
                if (decorView != null) {
                    return decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_FULLSCREEN == View.SYSTEM_UI_FLAG_FULLSCREEN
                }
            }
        }
        return false
    }

    fun checkFullScreenByCode2(context: Context?): Boolean {
        return if (context is Activity) {
            context.window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN == WindowManager.LayoutParams.FLAG_FULLSCREEN
        } else false
    }

    /**
     * 要特别注意 返回的字段包含空格  做判断时一定要trim()
     *
     * @param view
     * @return
     */
    @JvmStatic
    fun getIdText(view: View): String {
        val id = view.id
        val out = StringBuilder()
        if (id != View.NO_ID) {
            val r = view.resources
            if (id > 0 && resourceHasPackage(id) && r != null) {
                try {
                    val pkgname: String
                    pkgname = when (id and -0x1000000) {
                        0x7f000000 -> "app"
                        0x01000000 -> "android"
                        else -> r.getResourcePackageName(id)
                    }
                    val typename = r.getResourceTypeName(id)
                    val entryname = r.getResourceEntryName(id)
                    out.append(" ")
                    out.append(pkgname)
                    out.append(":")
                    out.append(typename)
                    out.append("/")
                    out.append(entryname)
                } catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                }
            }
        }
        return if (TextUtils.isEmpty(out.toString())) "" else out.toString()
    }

    /**
     * ViewBorderFrameLayout 的str id
     */
    private const val STR_VIEW_BORDER_Id = "app:id/dokit_view_border_id"

    /**
     * 获得app的contentView
     *
     * @param activity
     * @return
     */
    fun getDokitAppContentView(activity: Activity?): View? {
        activity?.let {
            val decorView = it.window.decorView as FrameLayout
            if (decorView.getTag(R.id.dokit_app_contentview_id) == null) {
                var mAppContentView: View? = null
                for (index in 0 until decorView.childCount) {
                    val child = decorView.getChildAt(index)
                    //LogHelper.i(TAG, "childId=====>" + getIdText(child));
                    //解决与布局边框工具冲突的问题
                    if (child is LinearLayout && TextUtils.isEmpty(getIdText(child).trim { it <= ' ' }) || child is FrameLayout) {
                        //如果是DokitBorderView 则返回他下面的第一个子child
                        mAppContentView = if (getIdText(child).trim { it <= ' ' } == STR_VIEW_BORDER_Id) {
                            (child as ViewBorderFrameLayout).getChildAt(0)
                        } else {
                            child
                        }
                        mAppContentView!!.tag = R.id.dokit_app_contentview_id
                        break
                    }
                }
                return mAppContentView
            } else {
                return decorView.getTag(R.id.dokit_app_contentview_id) as View
            }
        }

        return null
    }

    private fun resourceHasPackage(@AnyRes resid: Int): Boolean {
        return resid ushr 24 != 0
    }

    /**
     * 获取屏幕尺寸
     *
     * @param context
     * @return
     */
    fun getScreenInch(context: Activity): Double {
        var inch = 0.0
        try {
            var realWidth = 0
            var realHeight = 0
            val display = context.windowManager.defaultDisplay
            val metrics = DisplayMetrics()
            display.getMetrics(metrics)
            if (Build.VERSION.SDK_INT >= 17) {
                val size = Point()
                display.getRealSize(size)
                realWidth = size.x
                realHeight = size.y
            } else if (Build.VERSION.SDK_INT < 17
                    && Build.VERSION.SDK_INT >= 14) {
                val mGetRawH = Display::class.java.getMethod("getRawHeight")
                val mGetRawW = Display::class.java.getMethod("getRawWidth")
                realWidth = mGetRawW.invoke(display) as Int
                realHeight = mGetRawH.invoke(display) as Int
            } else {
                realWidth = metrics.widthPixels
                realHeight = metrics.heightPixels
            }
            inch = formatDouble(Math.sqrt(realWidth / metrics.xdpi * (realWidth / metrics.xdpi) + realHeight / metrics.ydpi * (realHeight / metrics.ydpi).toDouble()), 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return inch
    }

    /**
     * Double类型保留指定位数的小数，返回double类型（四舍五入）
     * newScale 为指定的位数
     */
    private fun formatDouble(d: Double, newScale: Int): Double {
        val bd = BigDecimal(d)
        return bd.setScale(newScale, BigDecimal.ROUND_HALF_UP).toDouble()
    }
}