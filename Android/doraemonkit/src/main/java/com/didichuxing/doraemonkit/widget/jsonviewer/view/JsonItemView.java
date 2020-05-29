package com.didichuxing.doraemonkit.widget.jsonviewer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.jsonviewer.adapter.BaseJsonViewerAdapter;


/**
 * Created by yuyuhang on 2017/11/29.
 */
public class JsonItemView extends LinearLayout {

    public static int TEXT_SIZE_DP = 12;

    private Context mContext;

    private TextView mTvLeft, mTvRight;
    private ImageView mIvIcon;

    public JsonItemView(Context context) {
        this(context, null);
    }

    public JsonItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JsonItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        LayoutInflater.from(mContext).inflate(R.layout.dk_jsonviewer_layout_item_view, this, true);

        mTvLeft = findViewById(R.id.tv_left);
        mTvRight = findViewById(R.id.tv_right);
        mIvIcon = findViewById(R.id.iv_icon);
    }

    public void setTextSize(float textSizeDp) {
        if (textSizeDp < 12) {
            textSizeDp = 12;
        } else if (textSizeDp > 30) {
            textSizeDp = 30;
        }

        TEXT_SIZE_DP = (int) textSizeDp;

        mTvLeft.setTextSize(TEXT_SIZE_DP);
        mTvRight.setTextSize(TEXT_SIZE_DP);
        mTvRight.setTextColor(BaseJsonViewerAdapter.BRACES_COLOR);

        // align the vertically expand/collapse icon to the text
        int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DP, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mIvIcon.getLayoutParams();
        layoutParams.height = textSize;
        layoutParams.width = textSize;
        layoutParams.topMargin = textSize / 5;

        mIvIcon.setLayoutParams(layoutParams);
    }

    public void setRightColor(int color) {
        mTvRight.setTextColor(color);
    }

    public void hideLeft() {
        mTvLeft.setVisibility(GONE);
    }

    public void showLeft(CharSequence text) {
        mTvLeft.setVisibility(VISIBLE);
        if (text != null) {
            mTvLeft.setText(text);
        }
    }

    public void hideRight() {
        mTvRight.setVisibility(GONE);
    }

    public void showRight(CharSequence text) {
        mTvRight.setVisibility(VISIBLE);
        if (text != null) {
            mTvRight.setText(text);
        }
    }

    public CharSequence getRightText() {
        return mTvRight.getText();
    }

    public void hideIcon() {
        mIvIcon.setVisibility(GONE);
    }

    public void showIcon(boolean isPlus) {
        mIvIcon.setVisibility(VISIBLE);
        mIvIcon.setImageResource(isPlus ? R.drawable.dk_jsonviewer_plus : R.drawable.dk_jsonviewer_minus);
        mIvIcon.setContentDescription(getResources().getString(isPlus ? R.string.dk_jsonViewer_icon_plus : R.string.dk_jsonViewer_icon_minus));
    }

    public void setIconClickListener(OnClickListener listener) {
        mIvIcon.setOnClickListener(listener);
    }

    public void addViewNoInvalidate(View child) {
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = generateDefaultLayoutParams();
            if (params == null) {
                throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
            }
        }
        addViewInLayout(child, -1, params);
    }
}
