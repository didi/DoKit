package com.didichuxing.doraemonkit.kit.largepicture

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig.getLargeImgFileThreshold
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig.getLargeImgMemoryThreshold
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar
import java.util.*

/**
 * @author:  maple
 * @time:  2020/6/10 - 16:22
 * @desc: 大图检测列表
 */
class LargeImageListFragment : BaseFragment() {
    private lateinit var mLargeImageList: RecyclerView
    private lateinit var mLargeImageListAdapter: LargeImageListAdapter
    private lateinit var mTitleBar: TitleBar
    override fun onRequestLayout(): Int {
       return R.layout.dk_fragment_large_img_list
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        load()
    }
    private fun initView() {
        mLargeImageList = findViewById(R.id.block_list)
        val layoutManager = LinearLayoutManager(context)
        mLargeImageList.layoutManager = layoutManager
        mLargeImageListAdapter = LargeImageListAdapter()
        mLargeImageList.adapter = mLargeImageListAdapter
        val decoration = DividerItemDecoration(context,DividerItemDecoration.VERTICAL)
        decoration.setDrawable(resources.getDrawable(R.drawable.dk_divider))
        mLargeImageList.addItemDecoration(decoration)
        mTitleBar = findViewById(R.id.title_bar)
        mTitleBar.onTitleBarClickListener = object : TitleBar.OnTitleBarClickListener {
            override fun onLeftClick() {
                activity!!.onBackPressed()
            }

            override fun onRightClick() {}
        }
    }
    private val fileThreshold = getLargeImgFileThreshold(LargePictureManager.FILE_DEFAULT_THRESHOLD)
    private val memoryThreshold = getLargeImgMemoryThreshold(LargePictureManager.MEMORY_DEFAULT_THRESHOLD)


    private fun load() {
        val imageInfos: MutableList<LargeImageInfo> = ArrayList()
        for (largeImageInfo in LargePictureManager.LARGE_IMAGE_INFO_MAP.values) {
            if (largeImageInfo.getFileSize() < fileThreshold && largeImageInfo.getMemorySize() < memoryThreshold) {
                continue
            }
            imageInfos.add(largeImageInfo)
        }
        mLargeImageListAdapter.setList(imageInfos)
    }
}