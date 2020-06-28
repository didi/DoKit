package com.didichuxing.doraemonkit.kit.network.ui

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.WhiteHostBean
import com.didichuxing.doraemonkit.util.DokitUtil.getString
import com.didichuxing.doraemonkit.widget.bravh.BaseQuickAdapter
import com.didichuxing.doraemonkit.widget.bravh.viewholder.BaseViewHolder

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/19-14:41
 * 描    述：
 * 修订历史：
 * ================================================
 */
class WhiteHostAdapter internal constructor(layoutResId: Int, data: MutableList<WhiteHostBean>?) : BaseQuickAdapter<WhiteHostBean, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, obj: WhiteHostBean) {
        obj?.let { item ->

            if (item.isCanAdd) {
                helper.getView<TextView>(R.id.tv_add).text = "+"
            } else {
                helper.getView<TextView>(R.id.tv_add).text = "-"
            }
            helper.getView<EditText>(R.id.ed_host).setText(item.host)
            helper.getView<EditText>(R.id.ed_host).addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable) {
                    if (s != null) {
                        item.host = s.toString()
                    }
                }
            })
            helper.getView<View>(R.id.fl_add_wrap).setOnClickListener(View.OnClickListener {
                val hostBeans: MutableList<WhiteHostBean> = data
                val text = helper.getView<TextView>(R.id.tv_add).text.toString()
                if (text == "+") {
                    val editText = helper.getView<EditText>(R.id.ed_host).text.toString()
                    if (TextUtils.isEmpty(editText)) {
                        ToastUtils.showShort(getString(R.string.dk_kit_net_monitor_white_host_edit_toast))
                        return@OnClickListener
                    }
                    for (hostBean in hostBeans) {
                        hostBean.isCanAdd = false
                    }
                    hostBeans.add(WhiteHostBean("", true))
                } else {
                    hostBeans.remove(item)
                }
                notifyDataSetChanged()
            })
        }
    }

}