package com.didichuxing.doraemonkit.kit.colorpick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.PageTag;
import com.didichuxing.doraemonkit.constant.RequestCode;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.base.PageIntent;
import com.didichuxing.doraemonkit.ui.setting.SettingItem;
import com.didichuxing.doraemonkit.ui.setting.SettingItemAdapter;
import com.didichuxing.doraemonkit.ui.widget.titlebar.HomeTitleBar;

/**
 * Created by wanglikun on 2018/9/15.
 */

public class ColorPickerSettingFragment extends BaseFragment {
    private HomeTitleBar mTitleBar;
    private SettingItem mSettingItem;
    private RecyclerView mSettingList;
    private SettingItemAdapter mSettingItemAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTitleBar();
    }

    private void initTitleBar() {
        mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setListener(new HomeTitleBar.OnTitleBarClickListener() {
            @Override
            public void onRightClick() {
                finish();
            }
        });
        mSettingItem = new SettingItem(R.string.dk_kit_color_picker, false);
        mSettingList = findViewById(R.id.setting_list);
        mSettingList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSettingItemAdapter = new SettingItemAdapter(getContext());
        mSettingItemAdapter.setOnSettingItemSwitchListener(new SettingItemAdapter.OnSettingItemSwitchListener() {
            @Override
            public void onSettingItemSwitch(View view, SettingItem data, boolean on) {
                if (mSettingItem == data) {
                    if (on) {
                        boolean result = requestCaptureScreen();
                        if (!result) {
                            mSettingItem.isChecked = true;
                            mSettingItemAdapter.notifyDataSetChanged();
                        }
                    } else {
                        FloatPageManager.getInstance().removeAll(ColorPickerFloatPage.class);
                        FloatPageManager.getInstance().removeAll(ColorPickerInfoFloatPage.class);
                    }
                }
            }
        });
        mSettingItemAdapter.append(mSettingItem);
        mSettingList.setAdapter(mSettingItemAdapter);
    }

    private boolean requestCaptureScreen() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mediaProjectionManager == null) {
            return false;
        }
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), RequestCode.CAPTURE_SCREEN);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.CAPTURE_SCREEN && resultCode == Activity.RESULT_OK) {
            showColorPicker(data);
            finish();
        } else {
            mSettingItem.isChecked = true;
            mSettingItemAdapter.notifyDataSetChanged();
        }
    }

    private void showColorPicker(Intent data) {
        PageIntent pageIntent = new PageIntent(ColorPickerInfoFloatPage.class);
        pageIntent.tag = PageTag.PAGE_COLOR_PICKER_INFO;
        FloatPageManager.getInstance().add(pageIntent);

        pageIntent = new PageIntent(ColorPickerFloatPage.class);
        pageIntent.bundle = data.getExtras();
        FloatPageManager.getInstance().add(pageIntent);
    }

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_color_picker_setting;
    }
}
