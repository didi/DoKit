package com.didichuxing.doraemonkit.widget.easyrefresh.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.easyrefresh.IRefreshHeader
import com.didichuxing.doraemonkit.widget.easyrefresh.State

class SimpleRefreshHeaderView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) : FrameLayout(context!!, attrs), IRefreshHeader {
    private val rotate_up: Animation
    private val rotate_down: Animation
    private val rotate_infinite: Animation
    private val textView: TextView
    private val arrowIcon: View
    private val successIcon: View
    private val loadingIcon: View
    override fun reset() {
        textView.text = resources.getText(R.string.dk_header_reset)
        successIcon.visibility = View.INVISIBLE
        arrowIcon.visibility = View.VISIBLE
        arrowIcon.clearAnimation()
        loadingIcon.visibility = View.INVISIBLE
        loadingIcon.clearAnimation()
    }

    override fun pull() {}
    override fun refreshing() {
        arrowIcon.visibility = View.INVISIBLE
        loadingIcon.visibility = View.VISIBLE
        textView.text = resources.getText(R.string.dk_header_refreshing)
        arrowIcon.clearAnimation()
        loadingIcon.startAnimation(rotate_infinite)
    }

    override fun onPositionChange(currentPos: Float, lastPos: Float, refreshPos: Float, isTouch: Boolean, state: State) {
        // 往上拉
        if (currentPos < refreshPos && lastPos >= refreshPos) {
            Log.i("", ">>>>up")
            if (isTouch && state == State.PULL) {
                textView.text = resources.getText(R.string.dk_header_pull)
                arrowIcon.clearAnimation()
                arrowIcon.startAnimation(rotate_down)
            }
            // 往下拉
        } else if (currentPos > refreshPos && lastPos <= refreshPos) {
            Log.i("", ">>>>down")
            if (isTouch && state == State.PULL) {
                textView.text = resources.getText(R.string.dk_header_pull_over)
                arrowIcon.clearAnimation()
                arrowIcon.startAnimation(rotate_up)
            }
        }
    }

    override fun complete() {
        loadingIcon.visibility = View.INVISIBLE
        loadingIcon.clearAnimation()
        successIcon.visibility = View.VISIBLE
        textView.text = resources.getText(R.string.dk_header_completed)
    }

    init {

        // 初始化动画
        rotate_up = AnimationUtils.loadAnimation(context, R.anim.dk_easy_refresh_rotate_up)
        rotate_down = AnimationUtils.loadAnimation(context, R.anim.dk_easy_refresh_rotate_down)
        rotate_infinite = AnimationUtils.loadAnimation(context, R.anim.dk_easy_refresh_rotate_infinite)
        View.inflate(context, R.layout.dk_refresh_default_refresh_header, this)
        textView = findViewById<View>(R.id.text) as TextView
        arrowIcon = findViewById(R.id.arrowIcon)
        successIcon = findViewById(R.id.successIcon)
        loadingIcon = findViewById(R.id.loadingIcon)
    }
}