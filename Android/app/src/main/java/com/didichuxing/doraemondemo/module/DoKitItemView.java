package com.didichuxing.doraemondemo.module;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.didichuxing.doraemondemo.R;

/**
 * didi Create on 2022/5/26 .
 * <p>
 * Copyright (c) 2022/5/26 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/5/26 11:18 上午
 * @Description 用一句话说明文件功能
 */

public class DoKitItemView extends FrameLayout {

    private int itemLayoutId;
    private int itemIcon;
    private int itemTextSize;
    private boolean itemIconShow;
    private boolean itemTextShow;
    private String itemText = "";

    private TextView textView;
    private ImageView imageView;


    public DoKitItemView(@NonNull Context context) {
        super(context);
    }

    public DoKitItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DoKitItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DoKitItemView);
        int layout = a.getInt(R.styleable.DoKitItemView_itemLayout, R.layout.view_dokit_item_view);
        int icon = a.getResourceId(R.styleable.DoKitItemView_itemIcon, R.mipmap.dk_arrow_normal);
        String text = a.getString(R.styleable.DoKitItemView_itemText);
        int textSize = a.getDimensionPixelSize(R.styleable.DoKitItemView_itemTextSize, -1);
        boolean iconShow = a.getBoolean(R.styleable.DoKitItemView_itemIconShow, true);
        boolean textShow = a.getBoolean(R.styleable.DoKitItemView_itemTextShow, true);

        itemLayoutId = layout;
        itemIcon = icon;
        itemText = text == null ? "" : text;
        itemTextSize = textSize;
        itemIconShow = iconShow;
        itemTextShow = textShow;
        a.recycle();

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        inflate(getContext(), itemLayoutId, this);
        textView = findViewById(R.id.itemText);
        imageView = findViewById(R.id.itemIcon);
        View root = findViewById(R.id.rootView);

        ShadowDrawable.setShadowDrawable(root, Color.parseColor("#FFFFFF"), dpToPx(8),
            Color.parseColor("#26777777"), dpToPx(4), 0, 0);

        textView.setText(itemText);
        imageView.setBackgroundResource(itemIcon);
        if (!itemIconShow) {
            imageView.setVisibility(GONE);
        }
        if (!itemTextShow) {
            textView.setVisibility(GONE);
        }
        if (itemTextSize >= 0) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemTextSize);
        }
    }

    private int dpToPx(int dp) {
        return (int) getResources().getDisplayMetrics().density * dp;
    }

    public String getItemText() {
        return itemText;
    }
}
