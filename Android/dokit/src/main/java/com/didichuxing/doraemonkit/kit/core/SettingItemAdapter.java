package com.didichuxing.doraemonkit.kit.core;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder;

/**
 * Created by wanglikun on 2018/9/14.
 */

public class SettingItemAdapter extends AbsRecyclerAdapter<AbsViewBinder<SettingItem>, SettingItem> {
    private OnSettingItemClickListener mOnSettingItemClickListener;
    private OnSettingItemSwitchListener mOnSettingItemSwitchListener;

    public SettingItemAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<SettingItem> createViewHolder(View view, int viewType) {
        return new SettingItemViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_setting, parent, false);
    }

    public class SettingItemViewHolder extends AbsViewBinder<SettingItem> {
        private TextView mDesc;
        private CheckBox mMenuSwitch;
        private ImageView mIcon;
        private TextView mRightDesc;

        public SettingItemViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            mMenuSwitch = getView(R.id.menu_switch);
            mDesc = getView(R.id.desc);
            mIcon = getView(R.id.right_icon);
            mRightDesc = getView(R.id.right_desc);
        }

        @Override
        public void bind(final SettingItem settingItem) {
            mDesc.setText(settingItem.desc);
            if (settingItem.canCheck) {
                mMenuSwitch.setVisibility(View.VISIBLE);
                mMenuSwitch.setChecked(settingItem.isChecked);
                mMenuSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isMatched(settingItem.desc)) {
                            if (AppHealthInfoUtil.getInstance().isAppHealthRunning()) {
                                mMenuSwitch.setChecked(true);
                                return;
                            }
                        }


                        settingItem.isChecked = isChecked;
                        mOnSettingItemSwitchListener.onSettingItemSwitch(mMenuSwitch, settingItem, isChecked);
                    }
                });
            }
            if (settingItem.icon != 0) {
                mIcon.setVisibility(View.VISIBLE);
                mIcon.setImageResource(settingItem.icon);
            }
            if (!TextUtils.isEmpty(settingItem.rightDesc)) {
                mRightDesc.setVisibility(View.VISIBLE);
                mRightDesc.setText(settingItem.rightDesc);
            }
        }

        @Override
        protected void onViewClick(View view, SettingItem data) {
            super.onViewClick(view, data);
            if (mOnSettingItemClickListener != null) {
                mOnSettingItemClickListener.onSettingItemClick(view, data);
            }
        }
    }

    /**
     * 是否命中
     *
     * @return
     */
    private boolean isMatched(@StringRes int desc) {
        int[] resources = new int[]{
                R.string.dk_weak_network_switch,
                R.string.dk_item_block_switch,
                R.string.dk_crash_capture_switch,
                R.string.dk_cpu_detection_switch,
                R.string.dk_frameinfo_detection_switch,
                R.string.dk_ram_detection_switch,
        };
        boolean isMatches = false;
        for (int res : resources) {
            if (res == desc) {
                isMatches = true;
                break;
            }
        }

        return isMatches;
    }

    public void setOnSettingItemClickListener(OnSettingItemClickListener onSettingItemClickListener) {
        mOnSettingItemClickListener = onSettingItemClickListener;
    }

    public void setOnSettingItemSwitchListener(OnSettingItemSwitchListener onSettingItemSwitchListener) {
        mOnSettingItemSwitchListener = onSettingItemSwitchListener;
    }

    public interface OnSettingItemClickListener {
        void onSettingItemClick(View view, SettingItem data);
    }

    public interface OnSettingItemSwitchListener {
        void onSettingItemSwitch(View view, SettingItem data, boolean on);
    }
}
