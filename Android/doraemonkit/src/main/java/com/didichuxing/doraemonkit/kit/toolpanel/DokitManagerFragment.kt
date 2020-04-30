package com.didichuxing.doraemonkit.kit.toolpanel

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.sysinfo.SysInfoKit
import com.didichuxing.doraemonkit.kit.toolpanel.decoration.HorizontalDividerItemDecoration
import com.didichuxing.doraemonkit.kit.toolpanel.decoration.VerticalDividerItemDecoration
import com.didichuxing.doraemonkit.widget.bravh.listener.OnItemDragListener
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar
import kotlinx.android.synthetic.main.dk_fragment_kit_manager.*
import kotlinx.android.synthetic.main.dk_tool_panel_new.title_bar

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
    lateinit var mAdapter: DokitManagerAdapter
    lateinit var datas: MutableList<MultiKitItem>

    @LayoutRes
    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_kit_manager
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generateData()
        initView()
    }

    private fun generateData() {
        datas = mutableListOf()
        datas.add(MultiKitItem(MultiKitItem.TYPE_TITLE, name = "业务工具", kit = null))
        datas.add(MultiKitItem(MultiKitItem.TYPE_KIT, name = "", kit = SysInfoKit()))
        datas.add(MultiKitItem(MultiKitItem.TYPE_KIT, name = "", kit = SysInfoKit()))
        datas.add(MultiKitItem(MultiKitItem.TYPE_KIT, name = "", kit = SysInfoKit()))
        datas.add(MultiKitItem(MultiKitItem.TYPE_KIT, name = "", kit = SysInfoKit()))

        datas.add(MultiKitItem(MultiKitItem.TYPE_TITLE, name = "常用工具", kit = null))
        datas.add(MultiKitItem(MultiKitItem.TYPE_KIT, name = "", kit = SysInfoKit()))
        datas.add(MultiKitItem(MultiKitItem.TYPE_KIT, name = "", kit = SysInfoKit()))
//        datas.add(MultiKitItem(MultiKitItem.TYPE_KIT, name = "", kit = SysInfoKit()))
//        datas.add(MultiKitItem(MultiKitItem.TYPE_KIT, name = "", kit = SysInfoKit()))
    }


    fun initView() {
        title_bar.setOnTitleBarClickListener(object : TitleBar.OnTitleBarClickListener {
            override fun onLeftClick() {
                finish()
            }

            override fun onRightClick() {
                ToastUtils.showShort("编辑")
            }

        })


        mAdapter = DokitManagerAdapter(datas)
        mAdapter.draggableModule.isDragEnabled = false
        mAdapter.draggableModule.setOnItemDragListener(object : OnItemDragListener {
            override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                val holder: BaseViewHolder = viewHolder as BaseViewHolder
                val startColor = Color.WHITE
                val endColor = Color.rgb(245, 245, 245)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { holder.itemView.setBackgroundColor(it.getAnimatedValue() as Int) }
                    v.duration = 300
                    v.start()
                }

            }

            /**
             * 针对drag状态，当前target对应的item是否允许移动
             * 我们一般用drag来做一些换位置的操作，就是当前对应的target对应的Item可以移动
             */
            override fun canDropOver(recyclerView: RecyclerView, current: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return true
            }

            override fun onItemDragMoving(source: RecyclerView.ViewHolder?, from: Int, target: RecyclerView.ViewHolder?, to: Int) {

            }


            override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
                val holder = viewHolder as BaseViewHolder
                // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                // 结束时，item背景色变化，demo这里使用了一个动画渐变，使得自然
                val startColor = Color.rgb(245, 245, 245)
                val endColor = Color.WHITE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val v = ValueAnimator.ofArgb(startColor, endColor)
                    v.addUpdateListener { holder.itemView.setBackgroundColor(it.animatedValue as Int) }
                    v.duration = 300
                    v.start()
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

        //rv_kits.addItemDecoration(GridDividerItemDecoration(1, 1, ContextCompat.getColor(activity!!, R.color.dk_color_E5E5E5)))
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
        //mAdapter.onAttachedToRecyclerView(rv_kits)
    }

    class DefaultSpanSizeLookup : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            if (position == 0 || position == 5) {
                return 1
            } else {
                return 4
            }
        }

    }


//    private class TitleItemBinder : QuickItemBinder<String>() {
//        override fun getLayoutId(): Int {
//            return R.layout.dk_item_group_title
//        }
//
//        override fun convert(holder: BaseViewHolder, item: String) {
//            holder.getView<TextView>(R.id.tv_title_name).setText(item)
//        }
//    }
//
//
//    private class KitViewItemBinder : QuickItemBinder<SysInfoKit>() {
//        override fun getLayoutId(): Int {
//            return R.layout.dk_item_group_kit_new
//        }
//
//        override fun convert(holder: BaseViewHolder, item: SysInfoKit) {
//            holder.getView<TextView>(R.id.tv_kit_name).setText(item.name)
//            holder.getView<ImageView>(R.id.iv_icon).setImageResource(item.icon)
//        }
//    }

}