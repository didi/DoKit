package com.didichuxing.doraemonkit.kit.timecounter

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar
import java.util.*

class TimeCounterListFragment : BaseFragment() {

    private lateinit var mBlockList: RecyclerView
    private lateinit var mTitleBar: TitleBar

    override fun onRequestLayout(): Int = R.layout.dk_fragment_time_counter_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mTitleBar = findViewById(R.id.title_bar)
        mBlockList = findViewById(R.id.block_list)

        val layoutManager = LinearLayoutManager(context)
        mBlockList.layoutManager = layoutManager

        val adapter = TimeCounterListAdapter(context)
        mBlockList.adapter = adapter

        mBlockList.addItemDecoration(DividerItemDecoration(DividerItemDecoration.VERTICAL).apply {
            setDrawable(resources.getDrawable(R.drawable.dk_divider))
        })

        mTitleBar.onTitleBarClickListener = object : TitleBar.OnTitleBarClickListener {
            override fun onLeftClick() = activity!!.onBackPressed()
            override fun onRightClick() {}
        }
        val recordHistory = TimeCounterManager.getHistory()

        Collections.sort(recordHistory) { lhs, rhs ->
            java.lang.Long.valueOf(rhs.recordTime())
                    .compareTo(lhs.recordTime())
        }
        adapter.data = recordHistory
    }
}