package com.didichuxing.doraemonkit.kit.sysinfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.didichuxing.doraemonkit.util.PermissionUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder;
import com.didichuxing.doraemonkit.widget.textview.LabelTextView;


/**
 * Created by wanglikun on 2018/9/14.
 */

public class SysInfoItemAdapter extends AbsRecyclerAdapter<AbsViewBinder<SysInfoItem>, SysInfoItem> {

    private final static int TYPE_ITEM = 0;
    private final static int TYPE_TITLE = 1;

    public SysInfoItemAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<SysInfoItem> createViewHolder(View view, int viewType) {
        if (viewType == TYPE_TITLE) {
            return new TitleItemViewHolder(view);
        } else {
            return new SysInfoItemViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getData().get(position) instanceof TitleItem) {
            return TYPE_TITLE;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == TYPE_TITLE) {
            return inflater.inflate(R.layout.dk_item_sys_title, parent, false);
        } else {
            return inflater.inflate(R.layout.dk_item_sys_info, parent, false);
        }
    }

    public class SysInfoItemViewHolder extends AbsViewBinder<SysInfoItem> {
        private LabelTextView mLabelText;

        public SysInfoItemViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            mLabelText = getView(R.id.label_text);
        }

        @Override
        public void bind(final SysInfoItem sysInfoItem) {
            mLabelText.setLabel(sysInfoItem.name);
            mLabelText.setText(sysInfoItem.value);
            mLabelText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (sysInfoItem.isPermission) {
                        PermissionUtils.launchAppDetailsSettings();
                    }
                }
            });
        }
    }

    public class TitleItemViewHolder extends AbsViewBinder<SysInfoItem> {
        private TextView mTextView;

        public TitleItemViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            mTextView = getView(R.id.tv_title);
        }

        @Override
        public void bind(SysInfoItem sysInfoItem) {
            mTextView.setText(sysInfoItem.name);
        }
    }
}
