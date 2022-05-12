package com.didichuxing.doraemondemo.test.screen

import android.content.Context
import android.graphics.Bitmap
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.didichuxing.doraemondemo.R
import com.didichuxing.doraemondemo.test.ScreenRecordingService
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams
import com.didichuxing.doraemonkit.kit.test.widget.FlashImageView
import com.didichuxing.doraemonkit.util.ConvertUtils
import com.didichuxing.doraemonkit.util.ToastUtils
import com.didichuxing.doraemonkit.util.UIUtils
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus


/**
 * didi Create on 2022/4/14 .
 *
 * Copyright (c) 2022/4/14 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/14 4:40 下午
 * @Description 用一句话说明文件功能
 */

class ScreenRecordingDoKitView : AbsDoKitView() {

    companion object {
        private val mainScope = MainScope() + CoroutineName(this.toString())
        private val myDoKitViews: MutableSet<ScreenRecordingDoKitView> = mutableSetOf()

        fun updateScreen(bitmap: Bitmap) {
            mainScope.launch {
                myDoKitViews.forEach {
                    it.updateScreen(bitmap)
                }
            }
        }
    }

    private var mRedDot: FlashImageView? = null
    private var mScreenImageView: ImageView? = null


    override fun onCreate(context: Context?) {

    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_screen_show_view, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {

        myDoKitViews.add(this)

        mRedDot = findViewById(R.id.dot)
        mScreenImageView = findViewById(R.id.screenAll)

        rootView?.setOnClickListener {

        }
        rootView?.findViewById<ImageView>(R.id.close)?.setOnClickListener {
            ScreenRecordingService.stopService()
            ToastUtils.showShort("已停止录屏")
        }

        mRedDot?.startFlash()

    }

    private fun updateScreen(bitmap: Bitmap) {
        mScreenImageView?.let {
            it.setImageBitmap(bitmap)
        }
    }


    override fun initDokitViewLayoutParams(params: DoKitViewLayoutParams) {
        params.width = UIUtils.getWidthPixels() / 2
        params.height = UIUtils.getRealHeightPixels() / 2
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = ConvertUtils.dp2px(25f)
        params.y = ConvertUtils.dp2px(25f)
    }

    override fun onDestroy() {
        myDoKitViews.remove(this)
        mRedDot?.cancelFlash()
        super.onDestroy()
    }
}
