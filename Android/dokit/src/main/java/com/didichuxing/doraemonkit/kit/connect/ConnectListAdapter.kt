package com.didichuxing.doraemonkit.kit.connect


import android.view.View
import android.widget.TextView
import androidx.core.view.isGone
import com.didichuxing.doraemonkit.R

import com.didichuxing.doraemonkit.widget.brvah.BaseQuickAdapter
import com.didichuxing.doraemonkit.widget.brvah.viewholder.BaseViewHolder


/**
 * didi Create on 2022/1/18 .
 *
 * Copyright (c) 2022/1/18 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/1/18 8:12 下午
 * @Description 用一句话说明文件功能
 */

class ConnectListAdapter(clientList: MutableList<ConnectAddress>, callback: (client: ConnectAddress) -> Unit) :
    BaseQuickAdapter<ConnectAddress, BaseViewHolder>(R.layout.dk_item_connect_address, clientList) {

    val callback2 = callback
    override fun convert(holder: BaseViewHolder, item: ConnectAddress) {
        holder.getView<TextView>(R.id.tv_name).text = "主机名称:${item.name}"
        holder.getView<TextView>(R.id.tv_address).text = "地址:${item.url}"
        holder.getView<TextView>(R.id.tv_time).text = "时间:${item.time}"
        holder.getView<TextView>(R.id.connect).setOnClickListener {
            callback2(item)
        }
        holder.getView<View>(R.id.state_dot).isGone = !item.enable
        holder.getView<TextView>(R.id.connect).isGone = item.enable
    }
}



