package com.didichuxing.doraemonkit.weex.storage.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.weex.R
import com.didichuxing.doraemonkit.weex.storage.StorageHacker
import com.didichuxing.doraemonkit.weex.storage.StorageInfo
import com.didichuxing.doraemonkit.weex.storage.adapter.StorageAdapter
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar

/**
 * Transformed by alvince on 2020/7/1
 *
 * @author haojianglong
 * @date 2019-06-18
 */
class StorageFragment : BaseFragment() {

    private var storageHacker: StorageHacker? = null

    override fun onRequestLayout(): Int = R.layout.dk_fragment_storage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViewById<HomeTitleBar>(R.id.title_bar)
            .apply {
                setListener(object : HomeTitleBar.OnTitleBarClickListener {
                    override fun onRightClick() {
                        activity?.finish()
                    }
                })
            }
        findViewById<RecyclerView>(R.id.info_list)
            .apply {
                layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
                addItemDecoration(DividerItemDecoration(DividerItemDecoration.VERTICAL))
                adapter = StorageAdapter(context).apply {
                    onItemClickListener = object : StorageAdapter.OnItemClickListener {
                        override fun onItemClick(info: StorageInfo) {
                            StorageDialogFragment().also {
                                it.arguments = Bundle().apply {
                                    putSerializable(StorageDialogFragment.KEY_STORAGE_INFO, info)
                                }
                            }.also { dialog ->
                                fragmentManager?.also { fm ->
                                    dialog.show(fm, "dialog")
                                }
                            }
                        }
                    }
                }
                storageHacker = StorageHacker(context, true).also {
                    it.fetch(object : StorageHacker.OnLoadListener {
                        override fun onLoad(list: List<StorageInfo>) {
                            (adapter as? StorageAdapter)?.append(list)
                        }
                    })
                }
            }
    }

    override fun onDestroy() {
        storageHacker?.destroy()
        super.onDestroy()
    }

}
