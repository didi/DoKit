package com.didichuxing.doraemonkit.kit.mc.oldui.record

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams
import com.didichuxing.doraemonkit.kit.mc.oldui.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.ui.DoKitMcActivity
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.ConvertUtils
import com.didichuxing.doraemonkit.util.SPUtils
import com.didichuxing.doraemonkit.util.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/17-14:37
 * 描    述：
 * 修订历史：
 * ================================================
 */
class RecordingDoKitView : AbsDoKitView() {
    private var mRedDot: View? = null
    private var mExtend: TextView? = null
    private lateinit var mDotFlow: Flow<Int>
    private lateinit var mEllipsisFlow: Flow<Int>
    override fun onCreate(context: Context?) {
        mDotFlow = flow {
            while (true) {
                emit(0)
                delay(500)
                emit(1)
                delay(500)
            }
        }

        mEllipsisFlow = flow {
            while (true) {
                (0..3).forEach {
                    emit(it)
                    delay(500)
                }
            }
        }
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return LayoutInflater.from(context)
            .inflate(R.layout.dk_dokitview_recording, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        rootView?.setOnClickListener {
            if (ActivityUtils.getTopActivity() is DoKitMcActivity) {
                return@setOnClickListener
            }
            val intent = Intent(activity, DoKitMcActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
        }
        rootView?.findViewById<ImageView>(R.id.iv_close)?.setOnClickListener {
            //TODO 需要补充取消录制接口
            DoKit.removeFloating(RecordingDoKitView::class)
            SPUtils.getInstance().put(DoKitMcManager.MC_CASE_RECODING_KEY, false)
            DoKitTestManager.closeTest()
            DoKitMcManager.IS_MC_RECODING = false
            ToastUtils.showShort("用例取消录制")
        }

        mRedDot = findViewById(R.id.red_dot)
        mExtend = findViewById(R.id.tv_extend)
        doKitViewScope.launch {
            mDotFlow.flowOn(Dispatchers.IO)
                .collect {
                    when (it) {
                        0 -> mRedDot?.visibility = View.VISIBLE
                        1 -> mRedDot?.visibility = View.INVISIBLE
                        else -> {
                        }
                    }
                }
        }


        doKitViewScope.launch {
            mEllipsisFlow.flowOn(Dispatchers.IO)
                .collect {
                    when (it) {
                        0 -> mExtend?.text = ""
                        1 -> mExtend?.text = "."
                        2 -> mExtend?.text = ".."
                        3 -> mExtend?.text = "..."
                        else -> {
                        }
                    }

                }
        }

    }

    override fun initDokitViewLayoutParams(params: DoKitViewLayoutParams) {
        params.width = DoKitViewLayoutParams.WRAP_CONTENT
        params.height = DoKitViewLayoutParams.WRAP_CONTENT
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = ConvertUtils.dp2px(25f)
        params.y = ConvertUtils.dp2px(25f)
    }

}
