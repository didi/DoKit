package com.didichuxing.doraemonkit.kit.network.ui

import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.DokitDbManager.Companion.instance
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.MockInterceptApiBean
import com.didichuxing.doraemonkit.widget.MultiLineRadioGroup
import com.didichuxing.doraemonkit.widget.bravh.entity.node.BaseNode
import com.didichuxing.doraemonkit.widget.bravh.provider.BaseNodeProvider
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder
import com.didichuxing.doraemonkit.widget.jsonviewer.JsonRecyclerView
import org.json.JSONException
import org.json.JSONObject

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/30-15:50
 * 描    述：
 * 修订历史：
 * ================================================
 */
class InterceptDetailNodeProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = InterceptMockAdapter.Companion.TYPE_CONTENT

    override val layoutId: Int
        get() = R.layout.dk_mock_intercept_content_item

    override fun convert(holder: BaseViewHolder, item: BaseNode) {
        if (item is MockInterceptApiBean) {
            val mockApi = item
            holder.setText(R.id.tv_path, "path:" + mockApi.path)
            val jsonQuery = holder.getView<JsonRecyclerView>(R.id.jsonviewer_query)
            val jsonBody = holder.getView<JsonRecyclerView>(R.id.jsonviewer_body)
            try {
                holder.getView<View>(R.id.rl_query).visibility = View.VISIBLE
                val jsonObject = JSONObject(mockApi.query)
                if (jsonObject.length() == 0) {
                    holder.getView<View>(R.id.rl_query).visibility = View.GONE
                } else {
                    jsonQuery.bindJson(mockApi.query)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                holder.getView<View>(R.id.rl_query).visibility = View.GONE
            }
            try {
                holder.getView<View>(R.id.rl_body).visibility = View.VISIBLE
                val jsonObject = JSONObject(mockApi.body)
                if (jsonObject.length() == 0) {
                    holder.getView<View>(R.id.rl_body).visibility = View.GONE
                } else {
                    jsonBody.bindJson(mockApi.body)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                holder.getView<View>(R.id.rl_body).visibility = View.GONE
            }
            holder.setText(R.id.tv_group, "group:" + mockApi.group)
            holder.setText(R.id.tv_create, "create person:" + mockApi.createPerson)
            holder.setText(R.id.tv_modify, "modify person:" + mockApi.modifyPerson)
            val radioGroup = holder.getView<MultiLineRadioGroup>(R.id.radio_group)
            if (mockApi.sceneList != null && mockApi.sceneList!!.size != 0) {
                val radioButtons = arrayOfNulls<String>(mockApi.sceneList!!.size)
                for (index in mockApi.sceneList!!.indices) {
                    radioButtons[index] = mockApi.sceneList!![index].name
                }
                radioGroup.removeAllButtons()
                radioGroup.addButtons(*radioButtons)
                radioGroup.setOnCheckedChangeListener(object : MultiLineRadioGroup.OnCheckedChangeListener {
                    override fun onCheckedChanged(group: ViewGroup?, button: RadioButton?) {
                        val index = radioGroup.checkedRadioButtonIndex
                        val sceneListBean = mockApi.sceneList!![index]
                        mockApi.selectedSceneName = sceneListBean.name

                        mockApi.selectedSceneId = (sceneListBean._id!!)
                        instance.updateInterceptApi(mockApi)
                    }
                })
                var index = 0
                for (i in mockApi.sceneList!!.indices) {
                    if (mockApi.sceneList!![i]._id == mockApi.selectedSceneId) {
                        index = i
                        break
                    }
                }
                //默认选中第一个场景
                radioGroup.checkAt(index)
            }
        }
    }
}