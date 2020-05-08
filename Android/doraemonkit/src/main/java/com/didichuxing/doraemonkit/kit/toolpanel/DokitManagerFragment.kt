package com.didichuxing.doraemonkit.kit.toolpanel

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.*
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.toolpanel.decoration.HorizontalDividerItemDecoration
import com.didichuxing.doraemonkit.kit.toolpanel.decoration.VerticalDividerItemDecoration
import com.didichuxing.doraemonkit.util.DokitUtil
import com.didichuxing.doraemonkit.util.LogHelper
import com.didichuxing.doraemonkit.widget.bravh.listener.OnItemDragListener
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder
import com.didichuxing.doraemonkit.widget.dialog.DialogListener
import com.didichuxing.doraemonkit.widget.dialog.SimpleDialogListener
import kotlinx.android.synthetic.main.dk_fragment_kit_manager.*
import java.io.File

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/29-15:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DokitManagerFragment : BaseFragment() {
    private lateinit var mAdapter: DokitManagerAdapter
    private val mKits: MutableList<MultiKitItem> = mutableListOf()
    private val mBakKits: MutableList<MultiKitItem> = mutableListOf()
    private var mDragStartPos = -1
    private val mBakGlobalKits: LinkedHashMap<String, MutableList<AbstractKit>> = LinkedHashMap()

    @LayoutRes
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_kit_manager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generateData()
        initView()
    }

    private fun updateGlobalBakKits() {
        //更新备份数据
        mBakGlobalKits.clear()
        mBakGlobalKits.putAll(DokitConstant.GLOBAL_KITS)
    }

    private fun generateData() {
        updateGlobalBakKits()
        mKits.clear()
        DokitConstant.GLOBAL_KITS.forEach { group ->
            when (group.key) {
                DokitConstant.GROUP_ID_PLATFORM,
                DokitConstant.GROUP_ID_COMM,
                DokitConstant.GROUP_ID_WEEX,
                DokitConstant.GROUP_ID_PERFORMANCE,
                DokitConstant.GROUP_ID_UI -> {
                    if (group.value.size != 0) {
                        mKits.add(MultiKitItem(MultiKitItem.TYPE_TITLE, name = DokitUtil.getString(DokitUtil.getStringId(group.key)), kit = null))
                        group.value.forEach { kit ->
                            if (kit.canShow) {
                                mKits.add(MultiKitItem(MultiKitItem.TYPE_KIT, name = DokitUtil.getString(kit.name), checked = kit.canShow, kit = kit, groupName = group.key))
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 重置kits 数据
     */
    private fun reSetKits(isEdit: Boolean) {
        mKits.clear()
        if (isEdit) {
            //全显示
            DokitConstant.GLOBAL_KITS.forEach { group ->
                when (group.key) {
                    DokitConstant.GROUP_ID_PLATFORM,
                    DokitConstant.GROUP_ID_COMM,
                    DokitConstant.GROUP_ID_WEEX,
                    DokitConstant.GROUP_ID_PERFORMANCE,
                    DokitConstant.GROUP_ID_UI -> {
                        if (group.value.size != 0) {
                            mKits.add(MultiKitItem(MultiKitItem.TYPE_TITLE, name = DokitUtil.getString(DokitUtil.getStringId(group.key)), kit = null))
                            group.value.forEach { kit ->
                                mKits.add(MultiKitItem(MultiKitItem.TYPE_KIT, name = DokitUtil.getString(kit.name), checked = kit.canShow, kit = kit, groupName = group.key))
                            }
                        }
                    }
                }
            }
        } else {
            //隐藏的不显示
            generateData()
        }

        mAdapter.notifyDataSetChanged()
    }

    /**
     * 保存数据到本地
     */
    private fun saveSystemKits() {
        val localKits: MutableList<KitGroupBean> = mutableListOf()
        DokitConstant.GLOBAL_KITS.forEach { group ->
            when (group.key) {
                DokitConstant.GROUP_ID_PLATFORM,
                DokitConstant.GROUP_ID_COMM,
                DokitConstant.GROUP_ID_WEEX,
                DokitConstant.GROUP_ID_PERFORMANCE,
                DokitConstant.GROUP_ID_UI -> {
                    val groupBean = KitGroupBean(group.key, mutableListOf())
                    localKits.add(groupBean)
                    group.value.forEach {
                        groupBean.kits.add(KitBean(it.javaClass.canonicalName!!, it.canShow, it.innerKitId()))
                    }
                }
            }
        }

        val json = GsonUtils.toJson(localKits)
        val systemKitPath = PathUtils.getInternalAppFilesPath() + File.separator + "system_kit_bak.json"
        FileIOUtils.writeFileFromString(systemKitPath, json, false)
    }

    /**
     * 在全局的数据结构中改变分组信息
     */
    private fun changeKitsPosInGlob(origin: MultiKitItem, current: MultiKitItem) {
        //先原先的分组中去掉kit
        val iterator = DokitConstant.GLOBAL_KITS[current.groupName]?.iterator()
        while (iterator!!.hasNext()) {
            val abstractKit = iterator.next()
            if (abstractKit.innerKitId().equals(current.kit?.innerKitId())) {
                iterator.remove()
            }
        }

        //在新的分组中插入数据
        val mutableList = DokitConstant.GLOBAL_KITS[origin.groupName]
        var originPos = -1
        mutableList?.forEachIndexed { index, abstractKit ->
            if (abstractKit.innerKitId() == origin.kit?.innerKitId()) {
                originPos = index
                return@forEachIndexed
            }
        }
        current.groupName = origin.groupName
        DokitConstant.GLOBAL_KITS[origin.groupName]?.add(originPos, current.kit!!)

        updateGlobalBakKits()
    }

    private fun dealTitleBar() {
        tv_reset.visibility = View.GONE
        ll_back.setOnClickListener {
            if (IS_EDIT) {
                showDialog(ConfirmDialogProvider(DokitUtil.getString(R.string.dk_toolpanel_dialog_edit_tip), object : SimpleDialogListener() {
                    override fun onPositive(): Boolean {
                        //需要将数据保存在本地备份
                        saveSystemKits()
                        finish()
                        return true
                    }


                    override fun onNegative(): Boolean {
                        DokitConstant.GLOBAL_KITS.clear()
                        DokitConstant.GLOBAL_KITS.putAll(mBakGlobalKits)
                        finish()
                        return true
                    }

                }))
            } else {
                finish()
            }


        }

        tv_edit.setOnClickListener {
            val textView = it as TextView
            if (DokitUtil.getString(R.string.dk_edit) == textView.text.toString()) {
                tv_reset.visibility = View.VISIBLE
                IS_EDIT = true
                textView.text = DokitUtil.getString(R.string.dk_complete)
                textView.setTextColor(ContextCompat.getColor(DoraemonKit.APPLICATION!!, R.color.dk_color_337CC4))
                mAdapter.draggableModule.isDragEnabled = true
                //需要重新过滤数据
                reSetKits(true)
            } else if (DokitUtil.getString(R.string.dk_complete) == textView.text.toString()) {
                tv_reset.visibility = View.GONE
                IS_EDIT = false
                textView.text = DokitUtil.getString(R.string.dk_edit)
                textView.setTextColor(ContextCompat.getColor(DoraemonKit.APPLICATION!!, R.color.dk_color_333333))
                mAdapter.draggableModule.isDragEnabled = false
                //需要重新过滤数据
                reSetKits(false)
                //需要将数据保存在本地备份
                saveSystemKits()
                //弹框
                showDialog(TipDialogProvider(DokitUtil.getString(R.string.dk_toolpanel_save_complete), null))
            }

            mAdapter.notifyDataSetChanged()
        }
        //还原
        tv_reset.setOnClickListener {
            showDialog(ConfirmDialogProvider(DokitUtil.getString(R.string.dk_toolpanel_dialog_reset_tip), object : SimpleDialogListener() {
                override fun onPositive(): Boolean {
                    val open = DoraemonKit.APPLICATION?.assets?.open("dokit_system_kits.json")
                    val json = ConvertUtils.inputStream2String(open, "UTF-8")
                    //设置成默认的系统控件排序
                    ToolPanelUtil.json2SystemKits(json)
                    generateData()
                    mAdapter.notifyDataSetChanged()
                    saveSystemKits()

                    tv_reset.visibility = View.GONE
                    IS_EDIT = false
                    tv_edit.text = DokitUtil.getString(R.string.dk_edit)
                    tv_edit.setTextColor(ContextCompat.getColor(DoraemonKit.APPLICATION!!, R.color.dk_color_333333))
                    mAdapter.draggableModule.isDragEnabled = false
                    showDialog(TipDialogProvider(DokitUtil.getString(R.string.dk_toolpanel_reset_complete), null))
                    return true
                }


                override fun onNegative(): Boolean {
                    return true
                }

            }))


        }

    }

    private fun initView() {
        dealTitleBar()
        mAdapter = DokitManagerAdapter(mKits)
        mAdapter.draggableModule.isDragEnabled = false
        mAdapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                val holder: BaseViewHolder = viewHolder as BaseViewHolder
                val startColor = Color.WHITE
                val endColor = Color.rgb(245, 245, 245)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { holder.itemView.setBackgroundColor(it.getAnimatedValue() as Int) }
                    v.duration = 300
                    v.start()
                }
                VibrateUtils.vibrate(50)
                mDragStartPos = pos
                //copy 一份数据用来做位置交换
                mBakKits.clear()
                mBakKits.addAll(mKits)
                LogHelper.i(TAG, "onItemDragStart===>$pos   ${mKits[pos].name}   ${mKits[pos].groupName}")
            }

            /**
             * 针对drag状态，当前target对应的item是否允许移动
             * 我们一般用drag来做一些换位置的操作，就是当前对应的target对应的Item可以移动
             */
            override fun canDropOver(recyclerView: RecyclerView, current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                //只有kit之间可以交换位置
                if (mKits[target.adapterPosition].itemType != MultiKitItem.TYPE_KIT) {
                    return false
                }

                //如果当前分组只存在一个item 不允许移动
                val groupName = mKits[current.adapterPosition].groupName
                if (DokitConstant.GLOBAL_KITS[groupName]?.size == 1) {
                    ToastUtils.showShort("分组中必须存在一个元素")
                    return false
                }
                return true
            }

            override fun onItemDragMoving(source: RecyclerView.ViewHolder?, from: Int, target: RecyclerView.ViewHolder?, to: Int) {

            }


            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                val holder = viewHolder as BaseViewHolder
                // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor = Color.rgb(245, 245, 245)
                val endColor = Color.WHITE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { holder.itemView.setBackgroundColor(it.animatedValue as Int) }
                    v.duration = 300
                    v.start()
                }
                //针对kits重新分组 交换位置
                if (mDragStartPos != pos) {
                    //原来的
                    changeKitsPosInGlob(mBakKits[pos], mKits[pos])
                    LogHelper.i(TAG, "onItemDragEnd==origin=>$pos   ${mBakKits[pos].name}   ${mBakKits[pos].groupName}")
                    LogHelper.i(TAG, "onItemDragEnd=current==>$pos   ${mKits[pos].name}   ${mKits[pos].groupName}")
                }
            }
        })


        val gridLayoutManager = GridLayoutManager(activity, 4)
        mAdapter.setGridSpanSizeLookup { _, viewType, _ ->
            if (viewType == MultiKitItem.TYPE_TITLE) {
                4
            } else {
                1
            }
        }


        mAdapter.setOnItemClickListener { adapter, view, position ->
            if (IS_EDIT) {
                val multiKitItem = mKits[position]
                if (multiKitItem.itemType == MultiKitItem.TYPE_KIT) {
                    multiKitItem.checked = !multiKitItem.checked
                    mAdapter.notifyDataSetChanged()
                    DokitConstant.GLOBAL_KITS[multiKitItem.groupName]?.forEach {
                        if (it.innerKitId() == multiKitItem.kit?.innerKitId()) {
                            it.canShow = multiKitItem.checked
                        }
                    }
                    updateGlobalBakKits()
                }
            }
        }

        val horizontalDividerItemDecoration = HorizontalDividerItemDecoration.Builder(activity)
                .color(ContextCompat.getColor(activity!!, R.color.dk_color_E5E5E5))
                .size(1)
                .showLastDivider()
                .build()
        val verticalDividerItemDecoration = VerticalDividerItemDecoration.Builder(activity)
                .color(ContextCompat.getColor(activity!!, R.color.dk_color_E5E5E5))
                .size(1)
                .showLastDivider()
                .build()
        rv_kits.addItemDecoration(horizontalDividerItemDecoration)
        rv_kits.addItemDecoration(verticalDividerItemDecoration)
        rv_kits.layoutManager = gridLayoutManager
        rv_kits.adapter = mAdapter
    }


    companion object {
        var IS_EDIT: Boolean = false
    }


}