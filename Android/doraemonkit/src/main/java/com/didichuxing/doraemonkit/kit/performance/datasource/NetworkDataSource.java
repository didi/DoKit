package com.didichuxing.doraemonkit.kit.performance.datasource;

import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.utils.ByteUtil;
import com.didichuxing.doraemonkit.kit.performance.widget.LineData;

/**
 * @desc: 抓包数据源
 */
public class NetworkDataSource implements IDataSource {
    private static final String TAG = "NetworkDataSource";
    private long latestTotalLength = -1;

    @Override
    public LineData createData() {
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
            return LineData.obtain((float) Math.ceil(diff / 1024f), null);
        } else {
            return LineData.obtain((float) Math.ceil(diff / 1024f), ByteUtil.getPrintSize(diff));
        }
    }
}
