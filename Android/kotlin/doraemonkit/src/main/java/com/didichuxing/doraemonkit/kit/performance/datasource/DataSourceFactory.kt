package com.didichuxing.doraemonkit.kit.performance.datasource

/**
 * @desc: 产生数据源对象的工厂类
 */
object DataSourceFactory {
    const val TYPE_NETWORK = 1
    fun createDataSource(type: Int): IDataSource {
        return when (type) {
            TYPE_NETWORK -> NetworkDataSource()
            else -> NetworkDataSource()
        }
    }
}