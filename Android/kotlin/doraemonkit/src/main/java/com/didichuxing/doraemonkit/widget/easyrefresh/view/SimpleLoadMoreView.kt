package com.didichuxing.doraemonkit.widget.easyrefresh.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.easyrefresh.ILoadMoreView
import com.github.ybq.android.spinkit.SpinKitView

/**
 * Created by guanaj on 16/9/22.
 */
class SimpleLoadMoreView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) : FrameLayout(context!!, attrs), ILoadMoreView {
    private val tvHitText: TextView
    private val spinKitView: SpinKitView
    override val canClickFailView: View
    override fun reset() {
        spinKitView.visibility = View.INVISIBLE
        tvHitText.visibility = View.INVISIBLE
        tvHitText.text = "正在加载..."
    }

    override fun loading() {
        spinKitView.visibility = View.VISIBLE
        tvHitText.visibility = View.VISIBLE
        tvHitText.text = "正在加载..."
    }

    override fun loadComplete() {
        spinKitView.visibility = View.INVISIBLE
        tvHitText.visibility = View.VISIBLE
        tvHitText.text = "加载完成"
    }

    override fun loadFail() {
        spinKitView.visibility = View.INVISIBLE
        tvHitText.visibility = View.VISIBLE
        tvHitText.text = "加载失败,点击重新加载"
    }

    override fun loadNothing() {
        spinKitView.visibility = View.INVISIBLE
        tvHitText.visibility = View.VISIBLE
        tvHitText.text = "没有更多可以加载"
    }

    init {
        canClickFailView = View.inflate(context, R.layout.dk_refresh_default_load_more, this)
        tvHitText = canClickFailView.findViewById<View>(R.id.tv_hit_content) as TextView
        spinKitView = canClickFailView.findViewById<View>(R.id.spin_kit) as SpinKitView
    }
}