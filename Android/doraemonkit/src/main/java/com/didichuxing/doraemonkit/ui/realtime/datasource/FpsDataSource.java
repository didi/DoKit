package com.didichuxing.doraemonkit.ui.realtime.datasource;

import com.didichuxing.doraemonkit.kit.common.PerformanceDataManager;
import com.didichuxing.doraemonkit.ui.realtime.widget.LineChart;
import com.didichuxing.doraemonkit.ui.realtime.widget.LineData;

public class FpsDataSource implements IDataSource {
    @Override
    public LineData createData() {
        float rate = PerformanceDataManager.getInstance().getLastFrameRate();
        return LineData.obtain(rate, Math.round(rate) + "");
    }
}
