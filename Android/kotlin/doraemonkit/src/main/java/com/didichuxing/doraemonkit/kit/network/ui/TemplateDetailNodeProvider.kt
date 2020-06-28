package com.didichuxing.doraemonkit.kit.network.ui

import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.core.UniversalActivity
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.DokitDbManager
import com.didichuxing.doraemonkit.kit.network.okhttp.room_db.MockTemplateApiBean
import com.didichuxing.doraemonkit.okgo.DokitOkGo
import com.didichuxing.doraemonkit.okgo.callback.StringCallback
import com.didichuxing.doraemonkit.okgo.model.Response
import com.didichuxing.doraemonkit.util.DokitUtil.getString
import com.didichuxing.doraemonkit.util.LogHelper.e
import com.didichuxing.doraemonkit.util.LogHelper.i
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
class TemplateDetailNodeProvider : BaseNodeProvider() {
    override val itemViewType: Int
        get() = TemplateMockAdapter.TYPE_CONTENT

    override val layoutId: Int
        get() = R.layout.dk_mock_template_content_item

    override fun convert(holder: BaseViewHolder, item: BaseNode) {
        if (item is MockTemplateApiBean) {
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
            val tvView = holder.getView<TextView>(R.id.tv_view)
            tvView.setOnClickListener(View.OnClickListener {
                if (TextUtils.isEmpty(mockApi.strResponse)) {
                    ToastUtils.showShort("no mock template data")
                    return@OnClickListener
                }
                //保存到全局
                DokitDbManager.instance.globalTemplateApiBean = mockApi
                val intent = Intent(tvView.context, UniversalActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_MOCK_TEMPLATE_PREVIEW)
                tvView.context.startActivity(intent)
            })
            val tvUpload = holder.getView<TextView>(R.id.tv_upload)
            tvUpload.setOnClickListener {
                DokitOkGo.patch<String>(TemplateMockAdapter.TEMPLATER_UPLOAD_URL)
                        .params("projectId", mockApi.projectId)
                        .params("id", mockApi.id)
                        .params("tempData", mockApi.strResponse)
                        .execute(object : StringCallback() {
                            override fun onSuccess(response: Response<String>) {
                                i(TAG, "上传模板===>" + response.body())
                                ToastUtils.showShort("upload template succeed")
                            }

                            override fun onError(response: Response<String>) {
                                super.onError(response)
                                ToastUtils.showShort("upload template failed")
                                e(TAG, "上传模板失败===>" + response.body())
                            }
                        })
            }
            val tvHasLocalMockData = holder.getView<TextView>(R.id.tv_local_has_mock_template)
            val hasLocalMockData: String
            if (!TextUtils.isEmpty(mockApi.strResponse)) {
                hasLocalMockData = "Y"
                tvUpload.isClickable = true
                tvUpload.setTextColor(tvUpload.context.resources.getColor(R.color.dk_color_337CC4))
            } else {
                hasLocalMockData = "N"
                tvUpload.isClickable = false
                tvUpload.setTextColor(tvUpload.context.resources.getColor(R.color.dk_color_999999))
            }
            tvHasLocalMockData.text = String.format(getString(R.string.dk_data_mock_template_tip), hasLocalMockData)
        }
    }

    companion object {
        private const val TAG = "TemplateDetailNodeProvider"
    }
}