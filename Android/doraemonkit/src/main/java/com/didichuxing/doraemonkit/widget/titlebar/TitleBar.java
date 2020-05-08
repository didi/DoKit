package com.didichuxing.doraemonkit.widget.titlebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.didichuxing.doraemonkit.R;

/**
 * Created by jinliang on 2017/8/28.
 */

public class TitleBar extends FrameLayout {
    private ImageView mLeftIcon;
    private TextView mTitle;
    private TextView mLeftText;
    private ImageView mRightIcon;
    private TextView mRightText;

    private OnTitleBarClickListener mListener;

    public TitleBar(@NonNull Context context) {
        this(context, null);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.dk_title_bar, this, true);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);
        int leftIcon = a.getResourceId(R.styleable.TitleBar_dkLeftIcon, 0);
        int rightIcon = a.getResourceId(R.styleable.TitleBar_dkRightIcon, 0);
        int rightSubIcon = a.getResourceId(R.styleable.TitleBar_dkRightSubIcon, 0);
        String title = a.getString(R.styleable.TitleBar_dkTitle);
        int titleColor = a.getColor(R.styleable.TitleBar_dkTitleColor, 0);
        int titleBackground = a.getColor(R.styleable.TitleBar_dkTitleBackground, getResources().getColor(R.color.dk_color_FFFFFF));
        String rightText = a.getString(R.styleable.TitleBar_dkRightText);
        String leftText = a.getString(R.styleable.TitleBar_dkLeftText);
        a.recycle();

        mLeftIcon = findViewById(R.id.left_icon);
        mRightIcon = findViewById(R.id.right_icon);
        mTitle = findViewById(R.id.title);

        mRightText = findViewById(R.id.right_text);
        mLeftText = findViewById(R.id.left_text);

        ((ViewGroup) mLeftIcon.getParent()).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onLeftClick();
                }
            }
        });
        if (rightSubIcon == 0) {
            ((ViewGroup) mRightIcon.getParent()).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onRightClick();
                    }
                }
            });
            ((ViewGroup) mRightText.getParent()).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onRightClick();
                    }
                }
            });
        } else {
            mRightIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onRightClick();
                    }
                }
            });
        }

        setLeftIcon(leftIcon);
        setLeftText(leftText);
        setRightText(rightText);
        setRightIcon(rightIcon);
        setRightTextColor(titleColor);
        setTitle(title);
        setTitleColor(titleColor);
        setBackgroundColor(titleBackground);
    }

    private void setRightTextColor(int titleColor) {
        if (titleColor == 0) {
            return;
        }
        mRightText.setTextColor(titleColor);
        mRightText.setVisibility(View.VISIBLE);
    }

    private void setTitleColor(int titleColor) {
        if (titleColor == 0) {
            return;
        }
        mTitle.setTextColor(titleColor);
        mTitle.setVisibility(View.VISIBLE);
    }

    public void setTitle(String title) {
        setTitle(title, true);
    }

    public void setTitle(String title, boolean alpha) {
        if (TextUtils.isEmpty(title)) {
            mTitle.setText("");
        } else {
            mTitle.setText(title);
            mTitle.setVisibility(View.VISIBLE);
            if (alpha) {
                mTitle.setAlpha(0);
                mTitle.animate().alpha(1).start();
            }
        }
    }

    public void setTitle(@StringRes int title) {
        setTitle(getResources().getString(title));
    }

    public void setTitleImage(int resId) {
        mTitle.setBackgroundResource(resId);
        mTitle.setVisibility(VISIBLE);
    }

    public void setOnTitleBarClickListener(OnTitleBarClickListener listener) {
        mListener = listener;
    }

    public void setLeftIcon(@DrawableRes int id) {
        if (id == 0) {
            return;
        }
        mLeftIcon.setImageResource(id);
        mLeftIcon.setVisibility(View.VISIBLE);
    }

    public void setRightIcon(@DrawableRes int id) {
        if (id == 0) {
            return;
        }
        mRightIcon.setImageResource(id);
        mRightIcon.setVisibility(View.VISIBLE);
    }

    public void setRightIcon(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        mRightIcon.setImageBitmap(bitmap);
        mRightIcon.setVisibility(VISIBLE);
        mRightText.setVisibility(GONE);
    }

    public View getLeftView() {
        return mLeftIcon;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void setRightText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        mRightText.setText(text);
        mRightText.setVisibility(View.VISIBLE);
        mRightIcon.setVisibility(GONE);
    }

    public void setLeftText(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        mLeftText.setText(text);
        mLeftText.setVisibility(View.VISIBLE);
    }

    public void hideRight() {
        mRightText.setVisibility(View.GONE);
        mRightIcon.setVisibility(View.GONE);
    }

    public ImageView getRightIcon() {
        return mRightIcon;
    }

    public TextView getRightText() {
        return mRightText;
    }

    public ImageView getLeftIcon() {
        return mLeftIcon;
    }


    /**
     * TitleBar 点击事件回调
     */
    public interface OnTitleBarClickListener {
        void onLeftClick();

        void onRightClick();
    }

    public interface OnTitleBarCheckListener {
        void onCheckChange(boolean isOn);
    }
}
