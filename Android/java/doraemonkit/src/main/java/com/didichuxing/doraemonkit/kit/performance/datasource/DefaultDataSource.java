package com.didichuxing.doraemonkit.kit.performance.datasource;

import com.didichuxing.doraemonkit.kit.performance.widget.LineData;

public class DefaultDataSource implements IDataSource {
    @Override
    public LineData createData() {
        float rate = 50.0f;
        return LineData.obtain(rate, Math.round(rate) + "");
    }
}
