package com.didichuxing.doraemonkit.kit.blockmonitor

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.blockmonitor.BlockListFragment
import com.didichuxing.doraemonkit.kit.blockmonitor.core.BlockMonitorManager.Companion.instance
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.core.SettingItem
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter.OnSettingItemClickListener
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter.OnSettingItemSwitchListener
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar
import kotlinx.android.synthetic.main.dk_fragment_block_monitor_index.*

/**
 * @desc: 卡顿检测首页
 */
class BlockMonitorFragment : BaseFragment() {
    companion object {
        private const val TAG = "BlockMonitorIndexFragment"
        const val KEY_JUMP_TO_LIST = "KEY_JUMP_TO_LIST"
    }

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_block_monitor_index
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        title_bar.setListener(object : HomeTitleBar.OnTitleBarClickListener {
            override fun onRightClick() {
                activity?.finish()
            }
        })
        val mSettingList = findViewById<RecyclerView>(R.id.setting_list)
        mSettingList.layoutManager = LinearLayoutManager(context)
        val settingItemAdapter = SettingItemAdapter(context)
        mSettingList.adapter = settingItemAdapter
        settingItemAdapter.append(SettingItem(R.string.dk_item_block_switch, instance.isRunning))
        settingItemAdapter.append(SettingItem(R.string.dk_item_block_goto_list))
        settingItemAdapter.append(SettingItem(R.string.dk_item_block_mock))
        settingItemAdapter.setOnSettingItemSwitchListener(object : OnSettingItemSwitchListener {
            override fun onSettingItemSwitch(view: View, data: SettingItem, on: Boolean) {
                if (data.desc == R.string.dk_item_block_switch) {
                    if (on) {
                        instance.start()
                    } else {
                        instance.stop()
                    }
                }
            }
        })
        settingItemAdapter.setOnSettingItemClickListener(object : OnSettingItemClickListener {
            override fun onSettingItemClick(view: View, data: SettingItem) {
                if (data.desc == R.string.dk_item_block_goto_list) {
                    showContent(BlockListFragment::class.java)
                } else if (data.desc == R.string.dk_item_block_mock) {
                    mockBlock()
                }
            }
        })
        if (arguments != null) {
            val jump = arguments!!.getBoolean(KEY_JUMP_TO_LIST, false)
            if (jump) {
                showContent(BlockListFragment::class.java)
            }
        }
    }

    private fun mockBlock() {
        try {
            view?.postDelayed({
                try {
                    Thread.sleep(2000)
                } catch (e: InterruptedException) {
                    //ignore
                }
            }, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}