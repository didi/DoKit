package com.didichuxing.doraemonkit.kit.toolpanel

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.toolpanel.decoration.HorizontalDividerItemDecoration
import com.didichuxing.doraemonkit.kit.toolpanel.decoration.VerticalDividerItemDecoration
import com.didichuxing.doraemonkit.util.*
import com.didichuxing.doraemonkit.widget.brvah.listener.OnItemDragListener
import com.didichuxing.doraemonkit.widget.brvah.viewholder.BaseViewHolder
import com.didichuxing.doraemonkit.widget.dialog.DialogListener
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider

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
    private val mKits: MutableList<KitWrapItem> = mutableListOf()
    private val mBakKits: MutableList<KitWrapItem> = mutableListOf()
    private var mDragStartPos = -1
    private val mBakGlobalKits: LinkedHashMap<String, MutableList<KitWrapItem>> = LinkedHashMap()

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
        //更新备份数据 需要深度拷贝
        for (group in mBakGlobalKits.keys) {
            when (group) {
                DoKitManager.GROUP_ID_PLATFORM,
                DoKitManager.GROUP_ID_COMM,
                DoKitManager.GROUP_ID_WEEX,
                DoKitManager.GROUP_ID_PERFORMANCE,
                DoKitManager.GROUP_ID_LBS,
                DoKitManager.GROUP_ID_UI -> {
                    mBakGlobalKits[group]?.clear()
                }
            }
        }

        for (group in DoKitManager.GLOBAL_KITS.keys) {
            mBakGlobalKits[group] = mutableListOf()
            DoKitManager.GLOBAL_KITS[group]?.forEach { it ->
                mBakGlobalKits[group]?.add(it.clone())
            }
        }

    }

    private fun generateData() {
        updateGlobalBakKits()
        mKits.clear()
        DoKitManager.GLOBAL_KITS.forEach { group ->
            when (group.key) {
                DoKitManager.GROUP_ID_PLATFORM,
                DoKitManager.GROUP_ID_COMM,
                DoKitManager.GROUP_ID_WEEX,
                DoKitManager.GROUP_ID_PERFORMANCE,
                DoKitManager.GROUP_ID_LBS,
                DoKitManager.GROUP_ID_UI -> {
                    if (group.value.size != 0) {
                        mKits.add(
                            KitWrapItem(
                                KitWrapItem.TYPE_TITLE,
                                name = DoKitCommUtil.getString(
                                    DoKitCommUtil.getStringId(group.key)
                                ),
                                kit = null
                            )
                        )
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

    /**
     * 重置kits 数据
     */
    private fun reSetKits(isEdit: Boolean) {
        mKits.clear()
        if (isEdit) {
            //全显示
            DoKitManager.GLOBAL_KITS.forEach { group ->
                when (group.key) {
                    DoKitManager.GROUP_ID_PLATFORM,
                    DoKitManager.GROUP_ID_COMM,
                    DoKitManager.GROUP_ID_WEEX,
                    DoKitManager.GROUP_ID_PERFORMANCE,
                    DoKitManager.GROUP_ID_LBS,
                    DoKitManager.GROUP_ID_UI -> {
                        if (group.value.size != 0) {
                            mKits.add(
                                KitWrapItem(
                                    KitWrapItem.TYPE_TITLE,
                                    name = DoKitCommUtil.getString(
                                        DoKitCommUtil.getStringId(group.key)
                                    ),
                                    kit = null
                                )
                            )
                            group.value.forEach { kitWrap ->
                                mKits.add(kitWrap)
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
        DoKitManager.GLOBAL_KITS.forEach { group ->
            when (group.key) {
                DoKitManager.GROUP_ID_PLATFORM,
                DoKitManager.GROUP_ID_COMM,
                DoKitManager.GROUP_ID_WEEX,
                DoKitManager.GROUP_ID_PERFORMANCE,
                DoKitManager.GROUP_ID_LBS,
                DoKitManager.GROUP_ID_UI -> {
                    val groupBean = KitGroupBean(group.key, mutableListOf())
                    localKits.add(groupBean)
                    group.value.forEach {
                        groupBean.kits.add(
                            KitBean(
                                it.kit!!.javaClass.canonicalName!!,
                                it.checked,
                                it.kit.innerKitId()
                            )
                        )
                    }
                }
            }
        }

        val json = GsonUtils.toJson(localKits)
        FileIOUtils.writeFileFromString(DoKitManager.SYSTEM_KITS_BAK_PATH, json, false)
    }

    /**
     * 重新进行分组
     */
    private fun reGroupForKit() {
        //先清空分组内的数据
        for (group: String in DoKitManager.GLOBAL_KITS.keys) {
            when (group) {
                DoKitManager.GROUP_ID_PLATFORM,
                DoKitManager.GROUP_ID_COMM,
                DoKitManager.GROUP_ID_WEEX,
                DoKitManager.GROUP_ID_PERFORMANCE,
                DoKitManager.GROUP_ID_LBS,
                DoKitManager.GROUP_ID_UI ->
                    DoKitManager.GLOBAL_KITS[group]?.clear()
            }
        }

        mKits.forEach {
            if (it.itemType == KitWrapItem.TYPE_KIT) {
                DoKitManager.GLOBAL_KITS[it.groupName]?.add(it)
            }
        }
    }

    private fun dealBack() {
        if (IS_EDIT) {
            showDialog(
                ConfirmDialogProvider(
                    DoKitCommUtil.getString(R.string.dk_toolpanel_dialog_edit_tip),
                    object : DialogListener {
                        override fun onPositive(dialogProvider: DialogProvider<*>): Boolean {
                            //需要将数据保存在本地备份
                            saveSystemKits()
                            finish()
                            return true
                        }


                        override fun onNegative(dialogProvider: DialogProvider<*>): Boolean {
                            DoKitManager.GLOBAL_KITS.putAll(mBakGlobalKits)
                            finish()
                            return true
                        }

                    })
            )
        } else {
            finish()
        }

        IS_EDIT = false
    }

    private fun dealTitleBar() {
        findViewById<View>(R.id.tv_reset).visibility = View.GONE
        findViewById<View>(R.id.ll_back).setOnClickListener {
            dealBack()
        }

        findViewById<View>(R.id.tv_edit).setOnClickListener {
            val textView = it as TextView
            if (DoKitCommUtil.getString(R.string.dk_edit) == textView.text.toString()) {
                findViewById<View>(R.id.tv_reset).visibility = View.VISIBLE
                IS_EDIT = true
                textView.text = DoKitCommUtil.getString(R.string.dk_complete)
                textView.setTextColor(
                    ContextCompat.getColor(
                        DoKit.APPLICATION!!,
                        R.color.dk_color_337CC4
                    )
                )
                mAdapter.draggableModule.isDragEnabled = true
                //需要重新过滤数据
                reSetKits(true)
            } else if (DoKitCommUtil.getString(R.string.dk_complete) == textView.text.toString()) {
                findViewById<View>(R.id.tv_reset).visibility = View.GONE
                IS_EDIT = false
                textView.text = DoKitCommUtil.getString(R.string.dk_edit)
                textView.setTextColor(
                    ContextCompat.getColor(
                        DoKit.APPLICATION!!,
                        R.color.dk_color_333333
                    )
                )
                mAdapter.draggableModule.isDragEnabled = false
                //需要重新过滤数据
                reSetKits(false)
                //需要将数据保存在本地备份
                saveSystemKits()
                //弹框
                showDialog(
                    TipDialogProvider(
                        DoKitCommUtil.getString(R.string.dk_toolpanel_save_complete),
                        null
                    )
                )
            }

            mAdapter.notifyDataSetChanged()
        }
        //还原
        findViewById<View>(R.id.tv_reset).setOnClickListener {
            showDialog(
                ConfirmDialogProvider(
                    DoKitCommUtil.getString(R.string.dk_toolpanel_dialog_reset_tip),
                    object : DialogListener {
                        override fun onPositive(dialogProvider: DialogProvider<*>): Boolean {
                            val open =
                                DoKit.APPLICATION.assets?.open("dokit_system_kits.json")
                            val json = ConvertUtils.inputStream2String(open, "UTF-8")
                            //设置成默认的系统控件排序
                            ToolPanelUtil.jsonConfig2InnerKits(json)
                            generateData()
                            mAdapter.notifyDataSetChanged()
                            saveSystemKits()

                            findViewById<View>(R.id.tv_reset).visibility = View.GONE
                            IS_EDIT = false

                            findViewById<TextView>(R.id.tv_edit).apply {
                                text = DoKitCommUtil.getString(R.string.dk_edit)
                                setTextColor(
                                    ContextCompat.getColor(
                                        DoKit.APPLICATION,
                                        R.color.dk_color_333333
                                    )
                                )
                            }

                            mAdapter.draggableModule.isDragEnabled = false
                            showDialog(
                                TipDialogProvider(
                                    DoKitCommUtil.getString(R.string.dk_toolpanel_reset_complete),
                                    null
                                )
                            )
                            return true
                        }

                        override fun onNegative(dialogProvider: DialogProvider<*>): Boolean {
                            return true
                        }

                    })
            )


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
            }

            /**
             * 针对drag状态，当前target对应的item是否允许移动
             * 我们一般用drag来做一些换位置的操作，就是当前对应的target对应的Item可以移动
             */
            override fun canDropOver(
                recyclerView: RecyclerView,
                current: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                //如果当前分组只存在一个item 不允许移动
                val groupName = mKits[current.adapterPosition].groupName
                if (DoKitManager.GLOBAL_KITS[groupName]?.size == 1) {
                    ToastUtils.showShort("分组中必须存在一个元素")
                    return false
                }
                return true
            }

            override fun onItemDragMoving(
                source: RecyclerView.ViewHolder?,
                from: Int,
                target: RecyclerView.ViewHolder?,
                to: Int
            ) {

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
                    //设置当前item的新分组名称
                    val originItem = mBakKits[pos]
                    val currentItem = mKits[pos]
                    if (originItem.itemType == currentItem.itemType) {
                        currentItem.groupName = originItem.groupName
                    } else {
                        currentItem.groupName = mBakKits[pos - 1].groupName
                    }
                    //原来的
                    reGroupForKit()
                }
            }
        })


        val gridLayoutManager = GridLayoutManager(activity, 4)
        mAdapter.setGridSpanSizeLookup { _, viewType, _ ->
            if (viewType == KitWrapItem.TYPE_TITLE) {
                4
            } else {
                1
            }
        }


        mAdapter.setOnItemClickListener { _, _, position ->
            if (IS_EDIT) {
                val multiKitItem = mKits[position]
                if (multiKitItem.itemType == KitWrapItem.TYPE_KIT) {
                    multiKitItem.checked = !multiKitItem.checked
                    mAdapter.notifyDataSetChanged()
                    DoKitManager.GLOBAL_KITS[multiKitItem.groupName]?.forEach {
                        if (it.kit?.innerKitId() == multiKitItem.kit?.innerKitId()) {
                            it.checked = multiKitItem.checked
                        }
                    }
                }
            }
        }

        val horizontalDividerItemDecoration =
            HorizontalDividerItemDecoration.Builder(requireActivity())
                .color(ContextCompat.getColor(requireActivity(), R.color.dk_color_E5E5E5))
                .size(1)
                .showLastDivider()
                .build()
        val verticalDividerItemDecoration = VerticalDividerItemDecoration.Builder(activity)
            .color(ContextCompat.getColor(requireActivity(), R.color.dk_color_E5E5E5))
            .size(1)
            .showLastDivider()
            .build()
        findViewById<RecyclerView>(R.id.rv_kits)
            .apply {
                addItemDecoration(horizontalDividerItemDecoration)
                addItemDecoration(verticalDividerItemDecoration)
                layoutManager = gridLayoutManager
                adapter = mAdapter
            }

    }

    override fun onBackPressed(): Boolean {
        dealBack()
        return true
    }


    companion object {
        var IS_EDIT: Boolean = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAdapter.context = null
    }


}
