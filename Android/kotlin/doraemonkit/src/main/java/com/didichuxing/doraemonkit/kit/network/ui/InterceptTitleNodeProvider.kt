package com.didichuxing.doraemonkit.kit.network.ui

import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.MockInterceptTitleBean
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.DokitDbManager.Companion.instance
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.MockInterceptApiBean
import com.didichuxing.doraemonkit.widget.bravh.entity.node.BaseNode
import com.didichuxing.doraemonkit.widget.bravh.provider.BaseNodeProvider
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/30-15:50
 * 描    述：
 * 修订历史：
 * ================================================
 */
class InterceptTitleNodeProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = InterceptMockAdapter.Companion.TYPE_TITLE

    override val layoutId: Int
        get() = R.layout.dk_mock_title_item

    override fun convert(holder: BaseViewHolder, item: BaseNode) {
        if (item is MockInterceptTitleBean<*>) {
            val mockTitleBean = item
            val mockApi0 = mockTitleBean.childNode[0] as MockInterceptApiBean
            holder.setText(R.id.tv_title, mockTitleBean.name)
            if (mockTitleBean.isExpanded) {
                holder.setImageResource(R.id.iv_more, R.mipmap.dk_arrow_open)
            } else {
                holder.setImageResource(R.id.iv_more, R.mipmap.dk_arrow_normal)
            }
            val checkBox = holder.getView<CheckBox>(R.id.menu_switch)
            //建议将setOnCheckedChangeListener放在控件checkBox.setChecked前面 否则代码设置选中时会触发回调导致状态不正确
            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                val mockApi = mockTitleBean.childNode[0] as MockInterceptApiBean
                //LogHelper.i(TAG, "checkBox====>" + mockApi.getMockApiName() + "----" + isChecked);
                mockApi.isOpen = (isChecked)
                //默认选中第一个场景
                if (TextUtils.isEmpty(mockApi.selectedSceneId)) {
                    val sceneListBeans = mockApi.sceneList
                    if (sceneListBeans != null && sceneListBeans.size > 0) {
                        val sceneListBean = sceneListBeans[0]
                        mockApi.selectedSceneName = sceneListBean.name
                        mockApi.selectedSceneId = (sceneListBean._id!!)
                    }
                }
                instance.updateInterceptApi(mockApi)
            }
            //LogHelper.i(TAG, "init====>" + mockApi0.getMockApiName() + "----" + mockApi0.isOpen());
            checkBox.isChecked = mockApi0.isOpen
        }
    }

    override fun onClick(holder: BaseViewHolder, view: View, data: BaseNode, position: Int) {
        super.onClick(holder, view, data, position)
        if (data is MockInterceptTitleBean<*> && getAdapter() != null) {
            getAdapter()!!.expandOrCollapse(position)
            if (data.isExpanded) {
                holder.setImageResource(R.id.iv_more, R.mipmap.dk_arrow_normal)
            } else {
                holder.setImageResource(R.id.iv_more, R.mipmap.dk_arrow_open)
            }
        }
    }
}
