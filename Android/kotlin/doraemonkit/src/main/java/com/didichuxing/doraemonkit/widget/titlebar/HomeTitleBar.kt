package com.didichuxing.doraemonkit.widget.titlebar

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.didichuxing.doraemonkit.R

/**
 * Created by wanglikun on 2018/12/4.
 */
class HomeTitleBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    var mListener: OnTitleBarClickListener? = null
    private lateinit var mTitle: TextView
    private lateinit var mIcon: ImageView

    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.dk_home_title_bar, this, true)
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.HomeTitleBar)
        val icon = a.getResourceId(R.styleable.HomeTitleBar_dkIcon, 0)
        val title = a.getString(R.styleable.HomeTitleBar_dkTitle)
        a.recycle()
        mIcon = findViewById(R.id.icon)
        mTitle = findViewById(R.id.title)
        mIcon.setOnClickListener {
            mListener?.onRightClick()
        }
        setTitle(title)
        setIcon(icon)
    }

    /**
     * TitleBar 点击事件回调
     */
    interface OnTitleBarClickListener {
        fun onRightClick()
    }

    fun setTitle(@StringRes title: Int) {
        setTitle(resources.getString(title))
    }

    fun setTitle(title: String?) {
        if (TextUtils.isEmpty(title)) {
            mTitle.text = ""
        } else {
            mTitle.text = title
            mTitle.alpha = 0f
            mTitle.visibility = View.VISIBLE
            mTitle.animate().alpha(1f).start()
        }
    }

    private fun setIcon(@DrawableRes id: Int) {
        if (id == 0) {
            return
        }
        mIcon.setImageResource(id)
        mIcon.visibility = View.VISIBLE
    }


    init {
        init(context, attrs)
    }
}