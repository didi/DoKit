package com.didichuxing.doraemonkit.widget.chart

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import com.didichuxing.doraemonkit.R

/**
 * @desc: 条形图
 */
class BarChart : LinearLayout {
    private var markFirst: TextView? = null
    private var markSecond: TextView? = null
    private var markThird: TextView? = null
    private var getValue: View? = null
    private var postValue: View? = null
    private var lineEnd: View? = null

    constructor(context: Context?) : super(context, null) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        val inflate = LayoutInflater.from(context).inflate(R.layout.dk_item_bar_chart, this, true)
        markFirst = inflate.findViewById(R.id.mark_first)
        markSecond = inflate.findViewById(R.id.mark_second)
        markThird = inflate.findViewById(R.id.mark_third)
        postValue = findViewById(R.id.post_value)
        getValue = findViewById(R.id.get_value)
        lineEnd = findViewById(R.id.solid_line_end)
    }

    fun setData(postNumber: Int, @ColorInt postColor: Int, getNumber: Int, @ColorInt getColor: Int) {
        var max = if (postNumber > getNumber) postNumber else getNumber
        max = max + 10 - max % 10
        val proportion = calculationProportion(max.toFloat())
        markFirst!!.text = "0"
        markThird!!.text = max.toString()
        markSecond!!.text = (max / 2).toString()
        postValue!!.setBackgroundColor(postColor)
        var layoutParams = postValue!!.layoutParams
        layoutParams.width = (proportion * postNumber).toInt()
        postValue!!.layoutParams = layoutParams
        layoutParams = getValue!!.layoutParams
        layoutParams.width = (proportion * getNumber).toInt()
        getValue!!.setBackgroundColor(getColor)
    }

    private fun calculationProportion(max: Float): Float {
        val layoutParams = lineEnd!!.layoutParams as RelativeLayout.LayoutParams
        return layoutParams.leftMargin / max
    }
}