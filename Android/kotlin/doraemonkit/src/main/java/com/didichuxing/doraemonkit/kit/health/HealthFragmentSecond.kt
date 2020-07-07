package com.didichuxing.doraemonkit.kit.health

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment

/**
 * 健康体检 第二页
 * @author pengyushan
 */
class HealthFragmentSecond : BaseFragment() {
    var mLlBackTop: LinearLayout? = null
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_health_child_two
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLlBackTop = findViewById(R.id.ll_back_top)
        mLlBackTop!!.setOnClickListener {
            val parentFragment = parentFragment
            if (parentFragment is HealthFragment) {
                parentFragment.scroll2theTop()
            }
        }
    }
}