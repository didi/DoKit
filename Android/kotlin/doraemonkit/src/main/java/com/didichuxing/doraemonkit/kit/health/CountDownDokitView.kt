package com.didichuxing.doraemonkit.kit.health

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.blankj.utilcode.util.ConvertUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import kotlinx.coroutines.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-27-17:59
 * 描    述：页面倒计时浮标
 * 修订历史：update by pengyushan 2020-07-07
 * ================================================
 */
class CountDownDokitView : AbsDokitView() {
    private val TAG = "CountDownDokitView"
    private var mNum: TextView? = null
    private var countdownJob: Job? = null
    private val COUNT_DOWN_TOTAL = 10
    private val COUNT_DOWN_INTERVAL = 1700L
    private var countTime = COUNT_DOWN_TOTAL
    override fun onCreate(context: Context) {
    }

    override fun onCreateView(context: Context, rootView: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_count_down,rootView,false)
    }

    override fun onViewCreated(rootView: FrameLayout) {
        mNum = findViewById(R.id.tv_number)
        countdownJob?.cancel()
        countdownJob = GlobalScope.launch(Dispatchers.Main) {
            while (true){
                delay(COUNT_DOWN_INTERVAL)
                countTime--
                mNum!!.text = "$countTime"
                if (countTime ==0){
                    if (isNormalMode) {
                        DokitViewManager.instance.detach(activity, this@CountDownDokitView)
                    } else {
                        DokitViewManager.instance.detach(this@CountDownDokitView)
                    }
                }
            }
        }
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params?.height = DokitViewLayoutParams.WRAP_CONTENT
        params?.width = DokitViewLayoutParams.WRAP_CONTENT
        params?.gravity = Gravity.TOP or Gravity.LEFT
        params?.x = ConvertUtils.dp2px(280f)
        params?.y = ConvertUtils.dp2px(25f)
    }

    /**
     * 重置时间
     */
    fun resetTime() {

    }

    override fun onDestroy() {
        super.onDestroy()
        countdownJob?.cancel()
    }
}