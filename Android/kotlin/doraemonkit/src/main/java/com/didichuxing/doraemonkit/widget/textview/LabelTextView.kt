package com.didichuxing.doraemonkit.widget.textview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.didichuxing.doraemonkit.R

/**
 * @author lostjobs created on 2020/6/9
 */
class LabelTextView : LinearLayout {

  private lateinit var mLabel: TextView
  private lateinit var mText: TextView

  constructor(context: Context) : this(context, null)
  constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  ) {
    initialize(context, attrs, defStyleAttr)
  }

  private fun initialize(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
    orientation = HORIZONTAL
    LayoutInflater.from(context).inflate(R.layout.dk_label_text_view, this)
    mLabel = findViewById(R.id.label)
    mText = findViewById(R.id.text)
    context.theme.obtainStyledAttributes(attrs, R.styleable.LabelTextView, defStyleAttr, 0)
      .apply {

        mLabel.text = getString(R.styleable.LabelTextView_dkLabel)
        mText.text = getString(R.styleable.LabelTextView_dkText)
        val maxLines = getInt(R.styleable.LabelTextView_dkMaxLines, 0)
        if (maxLines > 0) {
          mText.maxLines = maxLines
        }
        recycle()
      }
  }

  fun setText(text: String?) {
    mText.text = text
  }

  fun setText(@StringRes text: Int) {
    mText.setText(text)
  }

  fun setLabel(label: String?) {
    mLabel.text = label
  }

  fun setLabel(@StringRes label: Int) {
    mLabel.setText(label)
  }

  fun setTextColor(@ColorRes color: Int) {
    mText.setTextColor(ContextCompat.getColor(context, color))
  }

}