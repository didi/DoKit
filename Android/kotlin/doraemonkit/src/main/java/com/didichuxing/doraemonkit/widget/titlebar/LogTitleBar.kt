package com.didichuxing.doraemonkit.widget.titlebar

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.didichuxing.doraemonkit.R

class LogTitleBar : FrameLayout {
    private var mListener: OnTitleBarClickListener? = null
    private lateinit var mBack: TextView
    private lateinit var mTitle: TextView
    private lateinit var mIcon: ImageView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }


    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.dk_log_title_bar, this, true)

        mBack = findViewById(R.id.back)
        mIcon = findViewById(R.id.icon)
        mTitle = findViewById(R.id.title)

        context.obtainStyledAttributes(attrs, R.styleable.LogTitleBar).apply {
            val icon: Int = getResourceId(R.styleable.LogTitleBar_dkIcon, 0)
            val title: String = getString(R.styleable.LogTitleBar_dkTitle) ?: ""
            val back: String = getString(R.styleable.LogTitleBar_dkBack) ?: ""
            setBack(back)
            setTitle(title)
            setIcon(icon)
            recycle()
        }


        mIcon.setOnClickListener {
            if (mListener != null) {
                mListener!!.onRightClick()
            }
        }
        mBack.setOnClickListener {
            if (mListener != null) {
                mListener!!.onLeftClick()
            }
        }

    }

    /**
     * TitleBar 点击事件回调
     */
    interface OnTitleBarClickListener {
        fun onRightClick()
        fun onLeftClick()
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

    fun setBack(back: String?) {
        if (TextUtils.isEmpty(back)) {
            mBack.text = ""
        } else {
            mBack.text = back
        }
    }

    fun setIcon(@DrawableRes id: Int) {
        if (id == 0) {
            return
        }
        mIcon.setImageResource(id)
        mIcon.visibility = View.VISIBLE
    }

    fun setListener(listener: OnTitleBarClickListener?) {
        mListener = listener
    }
}