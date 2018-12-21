package com.didichuxing.doraemonkit.ui.setting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsViewBinder;

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
        return inflater.inflate(R.layout.item_setting, parent, false);
    }

    public class SettingItemViewHolder extends AbsViewBinder<SettingItem> {
        private TextView mDesc;
        private CheckBox mMenuSwitch;
        private ImageView mIcon;

        public SettingItemViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            mMenuSwitch = getView(R.id.menu_switch);
            mDesc = getView(R.id.desc);
            mIcon = getView(R.id.right_icon);
        }

        @Override
        public void bind(final SettingItem settingItem) {
            mDesc.setText(settingItem.desc);
            if (settingItem.canCheck) {
                mMenuSwitch.setVisibility(View.VISIBLE);
                mMenuSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        settingItem.isChecked = isChecked;
                        mOnSettingItemSwitchListener.onSettingItemSwitch(mMenuSwitch, settingItem, isChecked);
                    }
                });
                mMenuSwitch.setChecked(settingItem.isChecked);
            }
            if (settingItem.icon != 0) {
                mIcon.setVisibility(View.VISIBLE);
                mIcon.setImageResource(settingItem.icon);
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
