package com.didichuxing.doraemonkit.kit.performance.manager.datasource

import com.didichuxing.doraemonkit.kit.network.NetworkManager.Companion.get
import com.didichuxing.doraemonkit.kit.network.utils.ByteUtil
import com.didichuxing.doraemonkit.kit.performance.manager.datasource.IDataSource
import com.didichuxing.doraemonkit.kit.performance.manager.widget.LineData
import com.didichuxing.doraemonkit.kit.performance.manager.widget.LineData.Companion.obtain
/**
 * @desc: 抓包数据源
 */
class NetworkDataSource : IDataSource {
    private var latestTotalLength: Long = -1
    override fun createData(): LineData {
        var diff: Long = 0
        val totalSize = get().totalSize
        if (latestTotalLength >= 0) {
            diff = totalSize - latestTotalLength
            if (diff < 0) {
                diff = 0
            }
        }
        latestTotalLength = totalSize
        return if (diff == 0L) {
            obtain(Math.ceil(diff / 1024f.toDouble()).toFloat(), null)
        } else {
            obtain(Math.ceil(diff / 1024f.toDouble()).toFloat(), ByteUtil.getPrintSize(diff))
        }
    }

    companion object {
        private const val TAG = "NetworkDataSource"
    }
}