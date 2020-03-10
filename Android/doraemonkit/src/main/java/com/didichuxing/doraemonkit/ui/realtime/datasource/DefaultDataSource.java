package com.didichuxing.doraemonkit.ui.realtime.datasource;

import com.didichuxing.doraemonkit.ui.realtime.widget.LineData;

public class DefaultDataSource implements IDataSource {
    @Override
    public LineData createData() {
        float rate = 50.0f;
        return LineData.obtain(rate, Math.round(rate) + "");
    }
}
