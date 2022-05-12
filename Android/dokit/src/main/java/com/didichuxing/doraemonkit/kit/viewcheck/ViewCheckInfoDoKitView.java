package com.didichuxing.doraemonkit.kit.viewcheck;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView;
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams;
import com.didichuxing.doraemonkit.util.ColorUtil;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.List;

/**
 * Created by jintai on 2019/09/26.
 */
public class ViewCheckInfoDoKitView extends AbsDoKitView implements
        ViewCheckDoKitView.OnViewSelectListener, View.OnClickListener {
    private TextView mName;
    private TextView mId;
    private TextView mPosition;
    private TextView mDesc;
    private TextView mActivityInfo;
    private TextView mFragmentInfo;

    private ImageView mPre;
    private ImageView mNext;
    private ImageView mClose;


    @Override
    public void onCreate(Context context) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ViewCheckDoKitView dokitView = DoKit.getDoKitView(getActivity(), ViewCheckDoKitView.class);
        if (dokitView != null) {
            dokitView.removeViewSelectListener(this);
        }
    }

    @Override
    public View onCreateView(Context context, FrameLayout view) {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_view_check_info, null);
    }

    @Override
    public void onViewCreated(FrameLayout view) {
        mId = findViewById(R.id.id);
        mName = findViewById(R.id.name);
        mPosition = findViewById(R.id.position);
        mDesc = findViewById(R.id.desc);
        mActivityInfo = findViewById(R.id.activity);
        mFragmentInfo = findViewById(R.id.fragment);
        mClose = findViewById(R.id.close);
        mClose.setOnClickListener(this);
        mPre = findViewById(R.id.pre);
        mPre.setOnClickListener(this);
        mNext = findViewById(R.id.next);
        mNext.setOnClickListener(this);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewCheckDoKitView dokitView = DoKit.getDoKitView(getActivity(), ViewCheckDoKitView.class);
                if (dokitView != null) {
                    dokitView.setViewSelectListener(ViewCheckInfoDoKitView.this);
                }
            }
        }, 200);
    }

    @Override
    public void initDokitViewLayoutParams(DoKitViewLayoutParams params) {
        params.flags = DoKitViewLayoutParams.FLAG_NOT_FOCUSABLE;
        params.x = 0;
        params.y = UIUtils.getHeightPixels() - UIUtils.dp2px(185);
        params.width = getScreenShortSideLength();
        params.height = DoKitViewLayoutParams.WRAP_CONTENT;
    }

    @Override
    public void updateViewLayout(String tag, boolean isActivityBackResume) {
        super.updateViewLayout(tag, isActivityBackResume);
        // 由于父类在此方法限制了高度无法自适应，所以重新设成wrap_content以自适应
        final FrameLayout.LayoutParams params = getNormalLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDoKitView().setLayoutParams(params);
    }

    @Override
    public void onClick(View v) {
        if (v == mClose) {
            DoKit.removeFloating(ViewCheckDrawDoKitView.class);
            DoKit.removeFloating(ViewCheckInfoDoKitView.class);
            DoKit.removeFloating(ViewCheckDoKitView.class);
        }
        if (v == mNext) {
            ViewCheckDoKitView dokitView = DoKit.getDoKitView(getActivity(), ViewCheckDoKitView.class);
            if (dokitView != null) {
                dokitView.preformNextCheckView();
            }
        }
        if (v == mPre) {
            ViewCheckDoKitView dokitView = DoKit.getDoKitView(getActivity(), ViewCheckDoKitView.class);
            if (dokitView != null) {
                dokitView.preformPreCheckView();
            }
        }
    }

    @Override
    public void onViewSelected(@Nullable View current, @NonNull List<View> checkViewList) {

        mNext.setVisibility(checkViewList.size() > 1 ? View.VISIBLE : View.GONE);
        mPre.setVisibility(checkViewList.size() > 1 ? View.VISIBLE : View.GONE);

        if (current == null) {
            mName.setText("");
            mId.setText("");
            mPosition.setText("");
            mDesc.setText("");
        } else {
            mName.setText(getResources().getString(R.string.dk_view_check_info_class, current.getClass().getCanonicalName()));
            String idText = getResources().getString(R.string.dk_view_check_info_id, UIUtils.getIdText(current));
            mId.setText(idText);
            String positionText = getResources().getString(R.string.dk_view_check_info_size, current.getWidth(), current.getHeight());
            mPosition.setText(positionText);
            String descText = getViewExtraInfo(current);
            if (TextUtils.isEmpty(descText)) {
                mDesc.setVisibility(View.GONE);
            } else {
                mDesc.setText(descText);
                mDesc.setVisibility(View.VISIBLE);
            }
            Activity activity = ActivityUtils.getTopActivity();
            if (activity != null) {
                String activityText = activity.getClass().getSimpleName();
                setTextAndVisible(mActivityInfo, getResources().getString(R.string.dk_view_check_info_activity, activityText));
                String fragmentText = getVisibleFragment(activity);
                if (!TextUtils.isEmpty(fragmentText)) {
                    setTextAndVisible(mFragmentInfo, getResources().getString(R.string.dk_view_check_info_fragment, fragmentText));
                } else {
                    setTextAndVisible(mFragmentInfo, "");
                }
            } else {
                setTextAndVisible(mActivityInfo, "");
                setTextAndVisible(mFragmentInfo, "");
            }
        }
    }

    private String getViewExtraInfo(View v) {
        StringBuilder info = new StringBuilder();
        // 背景色
        Drawable drawable = v.getBackground();
        if (drawable != null) {
            if (drawable instanceof ColorDrawable) {
                int colorInt = ((ColorDrawable) drawable).getColor();
                String backgroundColor = ColorUtil.parseColorInt(colorInt);
                info.append(getResources().getString(R.string.dk_view_check_info_desc, backgroundColor));
                info.append("\n");
            }
        }
        // padding
        if (v.getPaddingLeft() != 0 && v.getPaddingTop() != 0 && v.getPaddingRight() != 0 && v.getPaddingBottom() != 0) {
            info.append(getResources().getString(R.string.dk_view_check_info_padding, v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), v.getPaddingBottom()));
            info.append("\n");
        }
        // margin
        final ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            final ViewGroup.MarginLayoutParams mp = ((ViewGroup.MarginLayoutParams) layoutParams);
            if (mp.leftMargin != 0 && mp.topMargin != 0 && mp.rightMargin != 0 && mp.bottomMargin != 0) {
                info.append(getResources().getString(R.string.dk_view_check_info_margin, mp.leftMargin, mp.topMargin, mp.rightMargin, mp.bottomMargin));
                info.append("\n");
            }
        }
        // TextView信息
        if (v instanceof TextView) {
            TextView tv = ((TextView) v);
            String textColor = ColorUtil.parseColorInt(tv.getCurrentTextColor());
            info.append(getResources().getString(R.string.dk_view_check_info_text_color, textColor));
            info.append("\n");
            info.append(getResources().getString(R.string.dk_view_check_info_text_size, (int) tv.getTextSize()));
            info.append("\n");

        }
        // 删除最后一个换行
        if (!TextUtils.isEmpty(info)) {
            info.deleteCharAt(info.length() - 1);
        }
        return info.toString();
    }

    private void setTextAndVisible(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
            textView.setText("");
        } else {
            textView.setVisibility(View.VISIBLE);
            textView.setText(text);
        }
    }

    private String getVisibleFragment(Activity activity) {
        if (activity == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        if (activity instanceof AppCompatActivity) {
            AppCompatActivity compatActivity = (AppCompatActivity) activity;
            FragmentManager fragmentManager = compatActivity.getSupportFragmentManager();
            List<Fragment> fragments = fragmentManager.getFragments();
            if (fragments != null && fragments.size() != 0) {
                for (int i = 0; i < fragments.size(); i++) {
                    Fragment fragment = fragments.get(i);
                    if (fragment != null && fragment.isVisible()) {
                        builder.append(fragment.getClass().getSimpleName() + "#" + fragment.getId());
                        if (i < fragments.size() - 1) {
                            builder.append(";");
                        }
                    }
                }
                return builder.toString();
            } else {
                return getFragmentForActivity(activity);
            }
        } else {
            return getFragmentForActivity(activity);
        }
    }

    private String getFragmentForActivity(Activity activity) {
        StringBuilder builder = new StringBuilder();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.FragmentManager manager = activity.getFragmentManager();
            List<android.app.Fragment> list = manager.getFragments();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    android.app.Fragment fragment = list.get(i);
                    if (fragment != null && fragment.isVisible()) {
                        builder.append(fragment.getClass().getSimpleName() + "#" + fragment.getId());
                        if (i < list.size() - 1) {
                            builder.append(";");
                        }
                    }
                }
            }
        }
        return builder.toString();
    }

}
