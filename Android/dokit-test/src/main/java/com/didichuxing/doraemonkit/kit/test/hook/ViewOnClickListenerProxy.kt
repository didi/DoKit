package com.didichuxing.doraemonkit.kit.test.hook

import android.view.View
import com.didichuxing.doraemonkit.extension.doKitGlobalScope
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/8/9-11:41
 * 描    述： View#performClick()
 * 方法内部会优先调用 mOnClickListener.onClick
 * 再发送 sendAccessibilityEvent 如果此时view被销毁
 * AccessibilityEvent 会被中断导致捕获不到对应的手势
 *
 * 修订历史：
 * ================================================
 * 延迟事件处理，解决中断问题
 */
class ViewOnClickListenerProxy(private val listener: View.OnClickListener?) : View.OnClickListener {
    companion object {
        const val TAG = "ProxyOnClickListener"
    }

    override fun onClick(v: View?) {
        //人为的制造延迟解决AccessibilityEvent被中断的问题
        if (DoKitTestManager.isHostMode()) {
            doKitGlobalScope.launch {
                withContext(Dispatchers.IO) {
                    delay(100)
                }
                listener?.onClick(v)
            }
        } else {
            listener?.onClick(v)
        }

    }
}
