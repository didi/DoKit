package com.didichuxing.doraemonkit.kit.mc.all.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.mc.ability.McHttpManager
import com.didichuxing.doraemonkit.kit.mc.all.DoKitMcManager
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.SPUtils
import com.didichuxing.doraemonkit.util.ToastUtils
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration
import kotlinx.coroutines.launch

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/10-10:52
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DoKitMcDatasFragment : BaseFragment() {
    lateinit var mRv: RecyclerView
    lateinit var mEmpty: TextView
    lateinit var mAdapter: McCaseListAdapter

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_mc_datas
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRv = findViewById(R.id.rv)
        mEmpty = findViewById(R.id.tv_empty)
        mRv.layoutManager = LinearLayoutManager(requireActivity())
        val decoration = DividerItemDecoration(DividerItemDecoration.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.drawable.dk_divider))
        mRv.addItemDecoration(decoration)
        mAdapter = McCaseListAdapter(mutableListOf<McCaseInfo>())
        mAdapter.setOnItemClickListener { adapter, _, pos ->
            for (i in adapter.data) {
                (i as McCaseInfo).isChecked = false
            }
            val item = adapter.data[pos] as McCaseInfo
            item.isChecked = true
            adapter.notifyDataSetChanged()
            ToastUtils.showShort("用例${item.caseName}已被选中")
            saveCaseId(item.caseId)
        }
        mRv.adapter = mAdapter
        lifecycleScope.launch {
            val data = McHttpManager.caseList<McCaseInfo>().data

            data?.let {
                if (it.isEmpty()) {
                    mEmpty.visibility = View.VISIBLE
                } else {
                    val caseId = loadCaseId()
                    it.forEach { info ->
                        info.isChecked = caseId == info.caseId
                    }
                    mAdapter.setList(it)
                }

            }
        }

    }

    private fun saveCaseId(caseId: String) {
        DoKitMcManager.MC_CASE_ID = caseId
        SPUtils.getInstance().put(DoKitMcManager.MC_CASE_ID_KEY, caseId)
    }

    private fun loadCaseId(): String {
        return if (DoKitMcManager.MC_CASE_ID.isEmpty()) {
            val caseId = SPUtils.getInstance().getString(DoKitMcManager.MC_CASE_ID_KEY, "")
            DoKitMcManager.MC_CASE_ID = caseId
            DoKitMcManager.MC_CASE_ID
        } else {
            DoKitMcManager.MC_CASE_ID
        }
    }


}