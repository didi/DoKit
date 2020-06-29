package com.didichuxing.doraemonkit.kit.performance.manager.datasource

/**
 * 产生数据源对象的工厂类
 */
object DataSourceFactory {
    const val TYPE_CPU = 1
    const val TYPE_RAM = 2
    const val TYPE_FPS = 3
    const val TYPE_NETWORK = 4
    fun createDataSource(type: Int): IDataSource {
        return when (type) {
            TYPE_CPU -> CpuDataSource()
            TYPE_RAM -> RamDataSource()
            TYPE_FPS -> FpsDataSource()
            TYPE_NETWORK-> NetworkDataSource()
            else -> DefaultDataSource()
        }
    }
}