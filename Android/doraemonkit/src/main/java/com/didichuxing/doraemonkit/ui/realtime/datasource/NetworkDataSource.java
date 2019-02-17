package com.didichuxing.doraemonkit.ui.realtime.datasource;

import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.utils.ByteUtil;
import com.didichuxing.doraemonkit.ui.realtime.widget.LineChart;

/**
 * @desc: 抓包数据源
 */
public class NetworkDataSource implements IDataSource {
    private long latestTotalLength = -1;

    @Override
    public LineChart.LineData createData() {
        long diff = 0;
        long totalSize = NetworkManager.get().getTotalSize();
        if (latestTotalLength >= 0) {
            diff = totalSize - latestTotalLength;
            if (diff < 0) {
                diff = 0;
            }
        }
        latestTotalLength = totalSize;
        if (diff == 0) {
            return LineChart.LineData.obtain(diff / 1024, null);
        } else {
            return LineChart.LineData.obtain(diff / 1024, ByteUtil.getPrintSize(diff));
        }
    }
}
