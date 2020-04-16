package com.didichuxing.doraemonkit.kit.performance.datasource;


import androidx.annotation.NonNull;

/**
 * @desc: 产生数据源对象的工厂类
 */
public class DataSourceFactory {
    public static final int TYPE_NETWORK = 1;
    public static final int TYPE_CPU = 2;
    public static final int TYPE_RAM = 3;
    public static final int TYPE_FPS = 4;

    @NonNull
    public static IDataSource createDataSource(int type) {
        switch (type) {
            case TYPE_NETWORK:
                return new NetworkDataSource();
            case TYPE_CPU:
                return new CpuDataSource();
            case TYPE_RAM:
                return new RamDataSource();
            case TYPE_FPS:
                return new FpsDataSource();
            default:
                return new DefaultDataSource();
        }
    }
}
