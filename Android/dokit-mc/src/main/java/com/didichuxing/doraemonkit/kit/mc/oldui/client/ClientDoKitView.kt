package com.didichuxing.doraemonkit.kit.mc.oldui.client

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams
import com.didichuxing.doraemonkit.kit.mc.ui.McPages
import com.didichuxing.doraemonkit.kit.mc.utils.McPageUtils
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.ConvertUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/17-14:37
 * 描    述：
 * 修订历史：
 * ================================================
 */
class ClientDoKitView : AbsDoKitView() {
    override fun onCreate(context: Context?) {
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.dk_dokitview_client, rootView, false)
        return view
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        rootView?.setOnClickListener {
            McPageUtils.startFragment(McPages.CLIENT)
        }
    }

    override fun initDokitViewLayoutParams(params: DoKitViewLayoutParams) {
        params.width = ConvertUtils.dp2px(70.0f)
        params.height = ConvertUtils.dp2px(70.0f)
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = ConvertUtils.dp2px(280f)
        params.y = ConvertUtils.dp2px(25f)
    }


}
