package com.didichuxing.doraemonkit.kit.blockmonitor

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.blockmonitor.bean.BlockInfo
import com.didichuxing.doraemonkit.kit.blockmonitor.core.BlockMonitorManager.Companion.instance
import com.didichuxing.doraemonkit.kit.blockmonitor.core.OnBlockInfoUpdateListener
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.util.LogHelper.i
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar
import kotlinx.android.synthetic.main.dk_fragment_block_list.*
import java.util.*

/**
 * @desc: 卡顿检测日志列表
 */
class BlockListFragment : BaseFragment(), OnBlockInfoUpdateListener {
    private var mBlockListAdapter: BlockListAdapter? = null
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_block_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        load()
        instance.setOnBlockInfoUpdateListener(this)
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(context)
        block_list.layoutManager = layoutManager
        mBlockListAdapter = BlockListAdapter(context)
        block_list.adapter = mBlockListAdapter
        val decoration = DividerItemDecoration(DividerItemDecoration.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.drawable.dk_divider))
        block_list.addItemDecoration(decoration)
        mBlockListAdapter?.setOnItemClickListener(object :BlockListAdapter.OnItemClickListener{
            override fun onClick(info: BlockInfo?) {
            tx_block_detail.text = info.toString()
            tx_block_detail.visibility = View.VISIBLE
            block_list.visibility = View.GONE
            title_bar.setTitle(resources.getString(R.string.dk_kit_block_monitor_detail), false)
            }
        })

        title_bar.onTitleBarClickListener = object : TitleBar.OnTitleBarClickListener {
            override fun onLeftClick() {
                activity?.onBackPressed()
            }

            override fun onRightClick() {}
        }
    }

    override fun onBackPressed(): Boolean {
        if (tx_block_detail.visibility == View.VISIBLE) {
            tx_block_detail.visibility = View.GONE
            block_list.visibility = View.VISIBLE
            title_bar.setTitle(R.string.dk_kit_block_monitor_list)
            return true
        }
        return super.onBackPressed()
    }

    private fun load() {
        val infos: List<BlockInfo> = ArrayList(instance.blockInfoList)
        Collections.sort(infos) { lhs, rhs ->
            java.lang.Long.valueOf(rhs.time)
                    .compareTo(lhs.time)
        }
        mBlockListAdapter?.data = infos
    }

    override fun onDestroyView() {
        super.onDestroyView()
        instance.setOnBlockInfoUpdateListener(null)
    }

    override fun onBlockInfoUpdate(blockInfo: BlockInfo?) {
        mBlockListAdapter?.append(blockInfo, 0)
    }

    companion object {
        private const val TAG = "BlockMonitorIndexFragment"
    }
}