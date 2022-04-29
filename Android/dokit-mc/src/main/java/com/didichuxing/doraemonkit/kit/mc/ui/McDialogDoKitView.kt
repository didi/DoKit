package com.didichuxing.doraemonkit.kit.mc.ui

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams
import com.didichuxing.doraemonkit.mc.R

/**
 * Created by jintai on 2019/09/26.
 * 在相应的界面上弹出提示框
 */
class McDialogDoKitView : AbsDoKitView() {

    lateinit var mTvExceptionType: TextView
    lateinit var mTvOk: TextView
    lateinit var exception: String

    override fun onCreate(context: Context) {
        exception = bundle?.getString("text", "未知同步异常类型")!!
    }

    override fun onCreateView(context: Context, view: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_dokitview_dialog, null)
    }

    override fun initDokitViewLayoutParams(params: DoKitViewLayoutParams) {
        params.flags = DoKitViewLayoutParams.FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE
        params.gravity = Gravity.CENTER
        params.width = DoKitViewLayoutParams.MATCH_PARENT
        params.height = DoKitViewLayoutParams.MATCH_PARENT
    }

    override fun onViewCreated(view: FrameLayout) {
        mTvExceptionType = findViewById<TextView>(R.id.exception_type)!!
        mTvExceptionType.text = exception
        mTvOk = findViewById<TextView>(R.id.tv_ok)!!
        mTvOk.setOnClickListener {
            DoKit.removeFloating(McDialogDoKitView::class.java)
        }
    }


    override fun canDrag(): Boolean {
        return false
    }
}
