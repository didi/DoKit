package com.didichuxing.doraemonkit.ui.realtime.datasource;

import com.didichuxing.doraemonkit.kit.common.PerformanceDataManager;
import com.didichuxing.doraemonkit.ui.realtime.widget.LineChart;

public class MemoryDataSource implements IDataSource {
    @Override
    public LineChart.LineData createData() {
        float info = PerformanceDataManager.getInstance().getLastMemoryInfo();
        return LineChart.LineData.obtain(info, Math.round(info) + "MB");
    }
}
