package com.didichuxing.doraemonkit.kit.toolpanel

import android.content.Context
import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.datapick.DataPickManager
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.core.UniversalActivity
import com.didichuxing.doraemonkit.util.DokitUtil
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar

/**
 * @author jintai
 * Created by jintai on 2019/09/26.
 * 新的工具面板弹窗
 */
class ToolPanelDokitView : AbsDokitView() {
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
        DoKitConstant.GLOBAL_KITS.forEach { group ->
            when (group.key) {
                DokitUtil.getString(R.string.dk_category_mode) -> {
                    mKits.add(KitWrapItem(KitWrapItem.TYPE_MODE, name = group.key, kit = null))
                }
                DokitUtil.getString(R.string.dk_category_exit) -> {
                    mKits.add(KitWrapItem(KitWrapItem.TYPE_EXIT, name = group.key, kit = null))
                }
                DokitUtil.getString(R.string.dk_category_version) -> {
                    mKits.add(KitWrapItem(KitWrapItem.TYPE_VERSION, name = group.key, kit = null))
                }
                DoKitConstant.GROUP_ID_PLATFORM,
                DoKitConstant.GROUP_ID_COMM,
                DoKitConstant.GROUP_ID_WEEX,
                DoKitConstant.GROUP_ID_PERFORMANCE,
                DoKitConstant.GROUP_ID_UI -> {
                    if (group.value.size != 0) {
                        mKits.add(KitWrapItem(KitWrapItem.TYPE_TITLE, name = DokitUtil.getString(DokitUtil.getStringId(group.key)), kit = null))
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
        titleBar.setOnTitleBarClickListener(object : TitleBar.OnTitleBarClickListener {
            override fun onLeftClick() {
                detach()
            }

            override fun onRightClick() {
                if (!isNormalMode) {
                    DoraemonKit.hideToolPanel()
                }
                if (activity != null) {
                    val intent = Intent(activity, UniversalActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_DOKIT_MORE)
                    activity.startActivity(intent)
                }
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
            val multiKitItem = mKits[position]
            if (multiKitItem.itemType == KitWrapItem.TYPE_KIT) {
                //常规模式下点击常用工具不隐藏工具面板
                DokitViewManager.getInstance().detachToolPanel()
                multiKitItem.kit?.onClick(ActivityUtils.getTopActivity())
                try {
                    //添加埋点
                    if (multiKitItem.kit?.isInnerKit!! && !TextUtils.isEmpty(multiKitItem.kit.innerKitId())) {
                        DataPickManager.getInstance().addData(multiKitItem.kit.innerKitId())
                    } else {
                        DataPickManager.getInstance().addData("dokit_sdk_business_ck")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        val rvKits = findViewById<RecyclerView>(R.id.rv_kits)
        rvKits.layoutManager = gridLayoutManager
        rvKits.adapter = mAdapter

    }


    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams) {
        params.x = 0
        params.y = 0
        params.width = DokitViewLayoutParams.MATCH_PARENT
        params.height = DokitViewLayoutParams.MATCH_PARENT
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

}