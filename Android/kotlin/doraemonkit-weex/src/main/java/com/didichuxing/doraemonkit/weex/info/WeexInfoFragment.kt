package com.didichuxing.doraemonkit.weex.info

import android.os.Bundle
import android.view.View
import android.widget.ListView
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.weex.R
import com.didichuxing.doraemonkit.weex.info.adapter.WeexInfoAdapter
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar

/**
 * Transformed by alvince on 2020/6/30
 *
 * @author haojianglong
 * @date 2019-06-18
 */
class WeexInfoFragment : BaseFragment() {

    private var adapter: WeexInfoAdapter? = null

    override fun onRequestLayout(): Int = R.layout.dk_fragment_info

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViewById<HomeTitleBar>(R.id.title_bar)
            .setListener(object : HomeTitleBar.OnTitleBarClickListener {
                override fun onRightClick() {
                    activity?.finish()
                }
            })
        findViewById<ListView>(R.id.info_list).apply {
            adapter = WeexInfoAdapter(requireContext()).also {
                it.setWeexInfos(WeexInfoHacker.getWeexInfos())
            }
        }
    }

}