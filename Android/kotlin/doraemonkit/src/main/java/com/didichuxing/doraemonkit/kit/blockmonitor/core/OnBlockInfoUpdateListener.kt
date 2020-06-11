package com.didichuxing.doraemonkit.kit.blockmonitor.core

import com.didichuxing.doraemonkit.kit.blockmonitor.bean.BlockInfo

interface OnBlockInfoUpdateListener {
    fun onBlockInfoUpdate(blockInfo: BlockInfo)
}