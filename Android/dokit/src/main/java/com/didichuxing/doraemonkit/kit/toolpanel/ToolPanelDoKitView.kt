package com.didichuxing.doraemonkit.kit.toolpanel

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.datapick.DataPickManager
import com.didichuxing.doraemonkit.datapick.DataPickUtils
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DoKitViewManager
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar

/**
 * @author jintai
 * Created by jintai on 2019/09/26.
 * 新的工具面板弹窗
 */
class ToolPanelDoKitView : AbsDoKitView() {
    private lateinit var mAdapter: ToolPanelAdapter
    private var mKits: MutableList<KitWrapItem> = mutableListOf()

    override fun onCreate(context: Context) {

    }

    override fun onCreateView(context: Context, view: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_tool_panel, view, false)
    }

    override fun onViewCreated(view: FrameLayout) {
        generateKits()
        initView()
    }

    private fun generateKits() {
        DoKitManager.GLOBAL_KITS.forEach { group ->
            when (group.key) {
                DoKitCommUtil.getString(R.string.dk_category_mode) -> {
                    mKits.add(KitWrapItem(KitWrapItem.TYPE_MODE, name = group.key, kit = null))
                }
                DoKitCommUtil.getString(R.string.dk_category_exit) -> {
                    mKits.add(KitWrapItem(KitWrapItem.TYPE_EXIT, name = group.key, kit = null))
                }
                DoKitCommUtil.getString(R.string.dk_category_version) -> {
                    mKits.add(KitWrapItem(KitWrapItem.TYPE_VERSION, name = group.key, kit = null))
                }
                DoKitManager.GROUP_ID_PLATFORM,
                DoKitManager.GROUP_ID_COMM,
                DoKitManager.GROUP_ID_WEEX,
                DoKitManager.GROUP_ID_PERFORMANCE,
                DoKitManager.GROUP_ID_LBS,
                DoKitManager.GROUP_ID_UI -> {
                    if (group.value.size != 0) {
                        mKits.add(
                            KitWrapItem(
                                KitWrapItem.TYPE_TITLE, name = DoKitCommUtil.getString(
                                    DoKitCommUtil.getStringId(group.key)
                                ), kit = null
                            )
                        )
                        group.value.forEach { kitWrap ->
                            if (kitWrap.checked) {
                                mKits.add(kitWrap)
                            }
                        }
                    }
                }
                else -> {
                    if (group.value.size != 0) {
                        mKits.add(KitWrapItem(KitWrapItem.TYPE_TITLE, name = group.key, kit = null))
                        group.value.forEach { kitWrap ->
                            if (kitWrap.checked) {
                                mKits.add(kitWrap)
                            }
                        }
                    }
                }
            }

        }
    }


    private fun initView() {
        val titleBar = findViewById<TitleBar>(R.id.title_bar)
        titleBar?.setOnTitleBarClickListener(object : TitleBar.OnTitleBarClickListener {
            override fun onLeftClick() {
                detach()
            }

            override fun onRightClick() {
                if (!isNormalMode) {
                    DoKit.hideToolPanel()
                }
                DoKit.launchFullScreen(
                    DokitMoreFragment::class.java,
                    activity,
                    isSystemFragment = true
                )
            }
        })
        mAdapter = ToolPanelAdapter(mKits)
        val gridLayoutManager = GridLayoutManager(activity, 4)
        mAdapter.setGridSpanSizeLookup { _, viewType, _ ->
            if (viewType == KitWrapItem.TYPE_KIT) {
                1
            } else {
                4
            }
        }
        mAdapter.setOnItemClickListener { _, _, position ->
            try {
                val multiKitItem = mKits[position]
                if (multiKitItem.itemType == KitWrapItem.TYPE_KIT) {
                    multiKitItem.kit?.let {
                        //常规模式下点击常用工具不隐藏工具面板
                        it.onClick(ActivityUtils.getTopActivity())
                        if (it.onClickWithReturn(ActivityUtils.getTopActivity())) {
                            DoKitViewManager.INSTANCE.detachToolPanel()
                        }

                        //添加埋点
                        if (it.isInnerKit && !TextUtils.isEmpty(it.innerKitId())) {
                            DataPickManager.getInstance().addData(it.innerKitId(), DataPickUtils.getDoKitHomeClickPage(), multiKitItem.name)
                        } else {
                            DataPickManager.getInstance().addData("dokit_sdk_business_ck", DataPickUtils.getDoKitHomeClickPage(), multiKitItem.name)
                        }

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val rvKits = findViewById<RecyclerView>(R.id.rv_kits)
        rvKits?.layoutManager = gridLayoutManager
        rvKits?.adapter = mAdapter

    }


    override fun initDokitViewLayoutParams(params: DoKitViewLayoutParams) {
        params.x = 0
        params.y = 0
        params.width = DoKitViewLayoutParams.MATCH_PARENT
        params.height = DoKitViewLayoutParams.MATCH_PARENT
    }

    override fun onBackPressed(): Boolean {
        detach()
        return true
    }

    override fun shouldDealBackKey(): Boolean {
        return true
    }

    override fun onHomeKeyPress() {
        detach()
    }

    override fun onRecentAppKeyPress() {
        detach()
    }

    override fun onResume() {
        super.onResume()
        resumeData()
        mAdapter.notifyDataSetChanged()
    }

    private fun resumeData() {
        mKits.clear()
        generateKits()
    }

    override fun restrictBorderline(): Boolean {
        return false
    }
}
