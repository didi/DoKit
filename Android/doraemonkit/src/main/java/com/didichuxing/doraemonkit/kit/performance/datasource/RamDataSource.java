package com.didichuxing.doraemonkit.kit.performance.datasource;

import com.didichuxing.doraemonkit.kit.performance.PerformanceDataManager;
import com.didichuxing.doraemonkit.kit.performance.widget.LineData;

public class RamDataSource implements IDataSource {
    private float mMaxRam;

    public RamDataSource() {
        mMaxRam = (float) (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024));
    }

    @Override
    public LineData createData() {
        float info = PerformanceDataManager.getInstance().getLastMemoryInfo();
        return LineData.obtain(info / mMaxRam * 100, Math.round(info) + "MB");
    }
}
