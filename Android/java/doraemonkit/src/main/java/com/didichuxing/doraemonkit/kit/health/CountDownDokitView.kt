package com.didichuxing.doraemonkit.kit.health

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.util.ConvertUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-27-17:59
 * 描    述：页面倒计时浮标
 * 修订历史：
 * ================================================
 */
class CountDownDokitView : AbsDokitView() {
    private var mNum: TextView? = null
    private lateinit var countDownFlow: Flow<Int>
    private var countDownJob: Job? = null
    override fun onCreate(context: Context?) {
        countDownFlow = flow {
            (10 downTo 0).forEach {
                emit(it)
                delay(1500)
            }
        }.flowOn(Dispatchers.IO)
            .onCompletion {
                withContext(Dispatchers.Main) {
                    if (isNormalMode) {
                        DokitViewManager.getInstance().detach(activity, this@CountDownDokitView)
                    } else {
                        DokitViewManager.getInstance().detach(this@CountDownDokitView)
                    }
                }
            }

    }


    override fun onCreateView(context: Context, rootView: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_count_down, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout) {
        mNum = findViewById(R.id.tv_number)
        startCountDown()
    }

    private fun startCountDown() {
        countDownJob?.let {
            if (it.isActive) {
                it.cancel()
            }
        }

        countDownJob = dokitScope.launch {
            countDownFlow.collect {
                withContext(Dispatchers.Main) {
                    mNum?.text = it.toString()
                }

            }

        }
    }

    /**
     * 重置倒计时 系统倒计时需要
     */
    fun resetTime() {
        startCountDown()
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams) {
        params.height = DokitViewLayoutParams.WRAP_CONTENT
        params.width = DokitViewLayoutParams.WRAP_CONTENT
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = ConvertUtils.dp2px(280f)
        params.y = ConvertUtils.dp2px(25f)
    }

}