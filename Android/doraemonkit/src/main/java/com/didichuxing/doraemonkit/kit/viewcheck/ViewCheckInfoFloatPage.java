package com.didichuxing.doraemonkit.kit.viewcheck;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.PageTag;
import com.didichuxing.doraemonkit.ui.base.BaseFloatPage;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.util.ColorUtil;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * Created by wanglikun on 2018/11/23.
 */

public class ViewCheckInfoFloatPage extends BaseFloatPage implements ViewCheckFloatPage.OnViewSelectListener, View.OnTouchListener {
    private TextView mName;
    private TextView mId;
    private TextView mPosition;
    private TextView mDesc;
    private ImageView mClose;

    private float sdX, sdY;
    private float ldX, ldY;

    private WindowManager mWindowManager;

    @Override
    protected void onCreate(Context context) {
        super.onCreate(context);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        ViewCheckFloatPage page = (ViewCheckFloatPage) FloatPageManager.getInstance().getFloatPage(PageTag.PAGE_VIEW_CHECK);
        page.setViewSelectListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewCheckFloatPage page = (ViewCheckFloatPage) FloatPageManager.getInstance().getFloatPage(PageTag.PAGE_VIEW_CHECK);
        page.removeViewSelectListener(this);
    }

    @Override
    protected View onCreateView(Context context, ViewGroup view) {
        return LayoutInflater.from(context).inflate(R.layout.float_view_check_info, null);
    }

    @Override
    protected void onViewCreated(View view) {
        super.onViewCreated(view);
        mId = findViewById(R.id.id);
        mName= findViewById(R.id.name);
        mPosition = findViewById(R.id.position);
        mDesc = findViewById(R.id.desc);
        mClose = findViewById(R.id.close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatPageManager.getInstance().removeAll(ViewCheckDrawFloatPage.class);
                FloatPageManager.getInstance().removeAll(ViewCheckInfoFloatPage.class);
                FloatPageManager.getInstance().removeAll(ViewCheckFloatPage.class);
            }
        });
        getRootView().setOnTouchListener(this);
    }

    @Override
    protected void onLayoutParamsCreated(WindowManager.LayoutParams params) {
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.x = 0;
        params.y = UIUtils.getHeightPixels(getContext()) - UIUtils.dp2px(getContext(), 125);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    public void onViewSelected(View view) {
        if (view == null) {
            mName.setText("");
            mId.setText("");
            mPosition.setText("");
            mDesc.setText("");
        } else {
            mName.setText(getResources().getString(R.string.dk_view_check_info_class, view.getClass().getSimpleName()));
            String idText = getResources().getString(R.string.dk_view_check_info_id, UIUtils.getIdText(view));
            mId.setText(idText);
            String positionText = getResources().getString(R.string.dk_view_check_info_size, view.getWidth(), view.getHeight());
            mPosition.setText(positionText);
            Drawable drawable = view.getBackground();
            String backgroundColor = "";
            if (drawable instanceof ColorDrawable) {
                int colorInt = ((ColorDrawable) drawable).getColor();
                backgroundColor = ColorUtil.parseColorInt(colorInt);
            }
            String descText = getResources().getString(R.string.dk_view_check_info_desc, backgroundColor);
            mDesc.setText(descText);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getRawX();
        float y = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                sdX = ldX = x;
                sdY = ldY = y;
                return false;
            case MotionEvent.ACTION_MOVE:
                getLayoutParams().x += (x - ldX + 0.5f);
                getLayoutParams().y += (y - ldY + 0.5f);
                ldX = x;
                ldY = y;
                mWindowManager.updateViewLayout(getRootView(), getLayoutParams());
                return false;
            case MotionEvent.ACTION_UP:
                int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (Math.abs(x - sdX) <= mTouchSlop && Math.abs(y - sdY) <= mTouchSlop) {
                    return false;
                }
                return true;
            default:
                break;
        }
        return false;
    }
}