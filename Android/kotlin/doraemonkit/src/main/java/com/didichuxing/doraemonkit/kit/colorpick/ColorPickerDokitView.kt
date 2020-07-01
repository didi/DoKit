package com.didichuxing.doraemonkit.kit.colorpick

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.util.ImageUtil
import com.didichuxing.doraemonkit.util.UIUtils

/**
 * 捕获屏幕并设置给取色器的view
 * @author Donald Yan
 * @date 2020/6/5
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class ColorPickerDokitView : AbsDokitView() {

    private var mImageCapturer: ImageCapturer? = null
    private lateinit var mPickerView: ColorPickerView
    private lateinit var mInfoDokitView: ColorPickerInfoDokitView

    private var width: Int = 0
    private var height: Int = 0
    private var statusBarHeight = 0

    private lateinit var coordinate: Coordinate

    override fun onCreate(context: Context) {
        mInfoDokitView = DokitViewManager.instance.getDokitView(ActivityUtils.getTopActivity(), ColorPickerInfoDokitView::class.java.simpleName) as ColorPickerInfoDokitView
        if (bundle != null) {
            mImageCapturer = ImageCapturerImpl(context, bundle!!)
        }
    }

    override fun onCreateView(context: Context, rootView: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_color_picker, null)
    }

    override fun onViewCreated(rootView: FrameLayout) {
        mPickerView = findViewById(R.id.cpv_color_picker_view)
        val params = mPickerView.layoutParams
        //大小必须是2的倍数
        params.width = ColorPickConstants.PICK_VIEW_SIZE
        params.height = ColorPickConstants.PICK_VIEW_SIZE
        mPickerView.layoutParams = params

        width = UIUtils.widthPixels
        height = UIUtils.heightPixels
        statusBarHeight = UIUtils.statusBarHeight
        coordinate = Coordinate()
        captureInfo(500L)
    }

    /**
     * 捕捉截图信息
     */
    private fun captureInfo(delay: Long) {
        //先隐藏拾色器控件 否则会把拾色器也截图进去
        mPickerView.visibility = View.INVISIBLE
        rootView.postDelayed({
            mImageCapturer?.capture()
            //截图完成以后恢复
            mPickerView.visibility = View.VISIBLE
            showInfo()
        }, delay)
    }

    private fun showInfo() {
        val x: Int
        val y: Int
        if (isNormalMode) {
            x = normalLayoutParams.leftMargin
            y = normalLayoutParams.topMargin
        } else {
            x = systemLayoutParams.x
            y = systemLayoutParams.y
        }

        val pickAreaSize = ColorPickConstants.PICK_AREA_SIZE
        //取色器边框宽度
        val ringWidth = (ColorPickConstants.PICK_VIEW_SIZE shr 1) - (pickAreaSize shr 1)
        val startX = x + ringWidth
        val startY = y + ringWidth + statusBarHeight
        val bitmap: Bitmap = mImageCapturer?.getPartBitmap(startX, startY, pickAreaSize, pickAreaSize) ?: return
        val colorInt: Int = ImageUtil.getPixel(bitmap, bitmap.width shr 1, bitmap.height shr 1)
        mPickerView.setBitmap(bitmap, colorInt, startX, startY)
        mInfoDokitView.showInfo(colorInt, startX, startY)

    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params!!.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        params.height = DokitViewLayoutParams.WRAP_CONTENT
        params.width = DokitViewLayoutParams.WRAP_CONTENT
    }

    override fun onEnterBackground() {
        //进入后台时，不调用父方法进行隐藏
    }

    override fun onEnterForeground() {
        //进入前台时，不调用父方法进行显示
    }

    override fun restrictBorderline(): Boolean {
        return false
    }

    override fun onDown(x: Int, y: Int) {
        super.onDown(x, y)
        captureInfo(100)
    }

    override fun onMove(x: Int, y: Int, dx: Int, dy: Int) {
        super.onMove(x, y, dx, dy)
        if (isNormalMode) {
            redressBound(normalLayoutParams.leftMargin, normalLayoutParams.topMargin)
            normalLayoutParams.leftMargin = coordinate.x
            normalLayoutParams.topMargin = coordinate.y
            normalLayoutParams.width = ColorPickConstants.PICK_VIEW_SIZE
            normalLayoutParams.height = ColorPickConstants.PICK_VIEW_SIZE
            invalidate()
        } else {
            redressBound(systemLayoutParams.x, systemLayoutParams.y)
            systemLayoutParams.x = coordinate.x
            systemLayoutParams.y = coordinate.y
        }
        showInfo()
    }

    /**
     * 矫正边界
     */
    private fun redressBound(x: Int, y: Int) {
        val centerX = mPickerView.width shr 1
        var boundX: Int = x
        if (x < -centerX) {
            boundX = -centerX
        }
        val maxX = width - centerX - ColorPickConstants.PIX_INTERVAL
        if (boundX > maxX) {
            boundX = maxX
        }
        coordinate.x = boundX

        val centerY = mPickerView.height shr 1
        var boundY: Int = y
        if (y < -centerY - statusBarHeight) {
            boundY = -centerY - statusBarHeight
        }
        val maxY = height - centerY - ColorPickConstants.PIX_INTERVAL
        if (boundY > maxY) {
            boundY = maxY
        }
        coordinate.y = boundY
    }

    override fun onDestroy() {
        super.onDestroy()
        mImageCapturer?.destroy()
        mImageCapturer = null
    }

    private class Coordinate {
        var x : Int = 0
        var y : Int = 0
    }
}