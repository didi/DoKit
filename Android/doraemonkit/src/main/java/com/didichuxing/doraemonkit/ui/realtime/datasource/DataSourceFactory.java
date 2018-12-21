package com.didichuxing.doraemonkit.ui.realtime.datasource;

/**
 * @desc: 产生数据源对象的工厂类
 */
public class DataSourceFactory {
    public static final int TYPE_NETWORK = 1;
    public static final int TYPE_CPU = 2;
    public static final int TYPE_MEMORY = 3;
    public static final int TYPE_FRAME = 4;

    public static IDataSource createDataSource(int type) {
        switch (type) {
            case TYPE_NETWORK:
                return new NetworkDataSource();
            case TYPE_CPU:
                return new CpuDataSource();
            case TYPE_MEMORY:
                return new MemoryDataSource();
            case TYPE_FRAME:
                return new FrameDataSource();
            default:
                return null;
        }
    }
}
