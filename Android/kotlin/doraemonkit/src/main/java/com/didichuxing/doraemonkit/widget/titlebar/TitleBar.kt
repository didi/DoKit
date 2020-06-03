package com.didichuxing.doraemonkit.widget.titlebar

import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.didichuxing.doraemonkit.R

/**
 * Created by jint on 2017/8/28.
 */
class TitleBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var mLeftIconView: ImageView
    private lateinit var mTitleView: TextView
    private lateinit var mLeftTextView: TextView
    lateinit var mRightIconView: ImageView
    lateinit var mRightTextView: TextView
    var onTitleBarClickListener: OnTitleBarClickListener? = null

    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.dk_title_bar, this, true)
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar)
        val leftIcon = a.getResourceId(R.styleable.TitleBar_dkLeftIcon, 0)
        val rightIcon = a.getResourceId(R.styleable.TitleBar_dkRightIcon, 0)
        val rightSubIcon = a.getResourceId(R.styleable.TitleBar_dkRightSubIcon, 0)
        val title = a.getString(R.styleable.TitleBar_dkTitle)
        val titleColor = a.getColor(R.styleable.TitleBar_dkTitleColor, 0)
        val titleBackground = a.getColor(R.styleable.TitleBar_dkTitleBackground, resources.getColor(R.color.dk_color_FFFFFF))
        val rightText = a.getString(R.styleable.TitleBar_dkRightText)
        val leftText = a.getString(R.styleable.TitleBar_dkLeftText)
        a.recycle()
        this.mLeftIconView = findViewById(R.id.left_icon)
        this.mRightIconView = findViewById(R.id.right_icon)
        mTitleView = findViewById(R.id.title)
        this.mRightTextView = findViewById(R.id.right_text)
        mLeftTextView = findViewById(R.id.left_text)
        (mLeftIconView.parent as ViewGroup).setOnClickListener {
            onTitleBarClickListener?.onLeftClick()
        }
        if (rightSubIcon == 0) {
            (mRightIconView.parent as ViewGroup).setOnClickListener {
                onTitleBarClickListener?.onRightClick()
            }
            (mRightTextView.parent as ViewGroup).setOnClickListener {
                onTitleBarClickListener?.onRightClick()
            }
        } else {
            mRightIconView.setOnClickListener {
                onTitleBarClickListener?.onRightClick()
            }
        }
        setLeftIcon(leftIcon)
        setLeftText(leftText)
        setRightText(rightText)
        setRightIcon(rightIcon)
        setRightTextColor(titleColor)
        setTitle(title)
        setTitleColor(titleColor)
        setBackgroundColor(titleBackground)
    }

    private fun setRightTextColor(titleColor: Int) {
        if (titleColor == 0) {
            return
        }
        mRightTextView.setTextColor(titleColor)
        mRightTextView.visibility = View.VISIBLE
    }

    private fun setTitleColor(titleColor: Int) {
        if (titleColor == 0) {
            return
        }
        mTitleView.setTextColor(titleColor)
        mTitleView.visibility = View.VISIBLE
    }

    fun setTitle(title: String?) {
        setTitle(title, true)
    }

    fun setTitle(title: String?, alpha: Boolean) {
        if (TextUtils.isEmpty(title)) {
            mTitleView.text = ""
        } else {
            mTitleView.text = title
            mTitleView.visibility = View.VISIBLE
            if (alpha) {
                mTitleView.alpha = 0f
                mTitleView.animate().alpha(1f).start()
            }
        }
    }

    fun setTitle(@StringRes title: Int) {
        setTitle(resources.getString(title))
    }

    fun setTitleImage(resId: Int) {
        mTitleView.setBackgroundResource(resId)
        mTitleView.visibility = View.VISIBLE
    }


    fun setLeftIcon(@DrawableRes id: Int) {
        if (id == 0) {
            return
        }
        mLeftIconView.setImageResource(id)
        mLeftIconView.visibility = View.VISIBLE
    }

    fun setRightIcon(@DrawableRes id: Int) {
        if (id == 0) {
            return
        }
        mRightIconView.setImageResource(id)
        mRightIconView.visibility = View.VISIBLE
    }

    fun setRightIcon(bitmap: Bitmap?) {
        if (bitmap == null) {
            return
        }
        mRightIconView.setImageBitmap(bitmap)
        mRightIconView.visibility = View.VISIBLE
        mRightTextView.visibility = View.GONE
    }

    val leftView: View?
        get() = mLeftIconView

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }

    fun setRightText(text: String?) {
        if (TextUtils.isEmpty(text)) {
            return
        }
        mRightTextView.text = text
        mRightTextView.visibility = View.VISIBLE
        mRightIconView.visibility = View.GONE
    }

    fun setLeftText(text: String?) {
        if (TextUtils.isEmpty(text)) {
            return
        }
        mLeftTextView.text = text
        mLeftTextView.visibility = View.VISIBLE
    }

    fun hideRight() {
        mRightTextView.visibility = View.GONE
        mRightIconView.visibility = View.GONE
    }

    /**
     * TitleBar 点击事件回调
     */
    interface OnTitleBarClickListener {
        fun onLeftClick()
        fun onRightClick()
    }

    interface OnTitleBarCheckListener {
        fun onCheckChange(isOn: Boolean)
    }

    init {
        init(context, attrs)
    }
}