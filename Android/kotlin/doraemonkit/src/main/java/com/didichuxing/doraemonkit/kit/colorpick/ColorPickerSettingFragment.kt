package com.didichuxing.doraemonkit.kit.colorpick

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager

/**
 * 屏幕拾色器fragment
 * @author Donald Yan
 * @date 2020/6/5
 */
class ColorPickerSettingFragment: BaseFragment() {

    companion object {
        const val CAPTURE_SCREEN = 10001
    }

    override fun onRequestLayout(): Int { return R.layout.dk_fragment_color_picker_setting }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return
        }
        val mediaProjectionManager = context?.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_SCREEN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_SCREEN && resultCode == Activity.RESULT_OK) {
            if (!DokitConstant.IS_NORMAL_FLOAT_MODE) {
                finish()
            }
            showColorPicker(data)
        }else {
            showToast("start color pick fail")
            finish()
        }
    }


    /**
     * 显示颜色拾取器
     *
     * @param data
     */
    private fun showColorPicker(data: Intent?) {
        Log.e("ColorPickerSettingFrag", "showColorPicker: ${data?.extras}")
        DokitViewManager.instance.detachToolPanel()
//        var pageIntent = DokitIntent(ColorPickerInfoDokitView::class.java)
//        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
//        DokitViewManager.instance.attach(pageIntent)

        var pageIntent = DokitIntent(ColorPickerDokitView::class.java)
        val bundle = Bundle()
        bundle.putParcelable("data", data)
        pageIntent.bundle = bundle
        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
        DokitViewManager.instance.attach(pageIntent)
    }
}