package com.didichuxing.doraemonkit.kit.viewcheck;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.ViewCheckConfig;
import com.didichuxing.doraemonkit.ui.base.AbsDokitView;
import com.didichuxing.doraemonkit.ui.base.DokitViewLayoutParams;
import com.didichuxing.doraemonkit.ui.base.DokitViewManager;
import com.didichuxing.doraemonkit.util.ColorUtil;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.List;

/**
 * Created by jintai on 2019/09/26.
 */
public class ViewCheckInfoDokitView extends AbsDokitView implements ViewCheckDokitView.OnViewSelectListener {
    private TextView mName;
    private TextView mId;
    private TextView mPosition;
    private TextView mDesc;
    private TextView mActivityInfo;
    private TextView mFragmentInfo;

    private ImageView mClose;


    @Override
    public void onCreate(Context context) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ViewCheckDokitView popView = (ViewCheckDokitView) DokitViewManager.getInstance().getDokitView(getActivity(), ViewCheckDokitView.class.getSimpleName());
        if (popView != null) {
            popView.removeViewSelectListener(this);
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
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewCheckConfig.setViewCheckOpen(getContext(), false);
                DokitViewManager.getInstance().detach(ViewCheckDrawDokitView.class.getSimpleName());
                DokitViewManager.getInstance().detach(ViewCheckInfoDokitView.class.getSimpleName());
                DokitViewManager.getInstance().detach(ViewCheckDokitView.class.getSimpleName());
            }
        });
        postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewCheckDokitView popView = (ViewCheckDokitView) DokitViewManager.getInstance().getDokitView(getActivity(), ViewCheckDokitView.class.getSimpleName());
                if(popView != null){
                    popView.setViewSelectListener(ViewCheckInfoDokitView.this);
                }
            }
        },200);

    }


    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {
        params.flags = DokitViewLayoutParams.FLAG_NOT_FOCUSABLE;
        params.x = 0;
        params.y = UIUtils.getHeightPixels(getContext()) - UIUtils.dp2px(getContext(), 185);
        params.width = getScreenShortSideLength();
        params.height = DokitViewLayoutParams.WRAP_CONTENT;
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