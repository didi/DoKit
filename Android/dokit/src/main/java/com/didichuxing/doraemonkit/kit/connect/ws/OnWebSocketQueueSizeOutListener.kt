package com.didichuxing.doraemonkit.kit.connect.ws


/**
 * didi Create on 2022/4/15 .
 *
 * Copyright (c) 2022/4/15 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/15 11:16 上午
 * @Description 等待发送队列超限制监听，当前队列超限制会触发重新链接，重新链接可能导致部分数据丢失
 */

interface OnWebSocketQueueSizeOutListener {

    fun onWebSocketQueueSizeOut()
}
