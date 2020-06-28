package com.didichuxing.doraemonkit.kit.performance.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.performance.datasource.IDataSource

/**
 * @desc: 实时折线图
 */
class LineChart : FrameLayout {
    private var mTitle: TextView? = null
    var performanceType = 0
    private var mLine: CardiogramView? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    private fun initView(context: Context) {
        View.inflate(context, R.layout.dk_view_line_chart, this)
        mTitle = findViewById(R.id.tv_title)
        mLine = findViewById(R.id.line_chart_view)
    }

    fun setTitle(title: String?) {
        mTitle!!.text = title
    }

    fun startMove() {
        mLine!!.startMove()
    }

    fun stopMove() {
        mLine!!.stopMove()
    }

    fun setInterval(interval: Int) {
        mLine!!.setInterval(interval)
    }

    fun setDataSource(dataSource: IDataSource) {
        mLine!!.setDataSource(dataSource)
    }
}