package com.didichuxing.doraemonkit.kit.autotest.ui

import android.os.Bundle
import android.view.View
import com.didichuxing.doraemonkit.autotest.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/10-10:52
 * 描    述：一机多控main fragment
 * 修订历史：
 * ================================================
 */
class DoKitAutotestFragment : BaseFragment() {


    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_autotest_main
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


}
