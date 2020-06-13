package com.didichuxing.doraemonkit.kit.performance.manager.datasource

import com.didichuxing.doraemonkit.kit.performance.manager.widget.LineData

/**
 * @desc: 折线图绘制的数据源接口
 */
interface IDataSource {
    /**
     * 返回在折线图上显示的最新数据
     *
     * @return
     */
    fun createData(): LineData?
}