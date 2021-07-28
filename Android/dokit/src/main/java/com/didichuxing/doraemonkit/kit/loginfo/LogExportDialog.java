package com.didichuxing.doraemonkit.kit.loginfo;


import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.dialog.DialogListener;
import com.didichuxing.doraemonkit.widget.dialog.DialogProvider;
import com.didichuxing.doraemonkit.kit.core.SettingItem;
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter;

/**
 * Created by wanglikun on 2019/4/16
 */
public class LogExportDialog extends DialogProvider<Object> {
    private RecyclerView mChooseList;
    private SettingItemAdapter mAdapter;

    public LogExportDialog(Object data, DialogListener listener) {
        super(data, listener);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dk_dialog_file_explorer_choose;
    }

    @Override
    protected void findViews(View view) {
        mChooseList = view.findViewById(R.id.choose_list);
        mAdapter = new SettingItemAdapter(getContext());
        mChooseList.setAdapter(mAdapter);
        mChooseList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    protected void bindData(final Object file) {
        mAdapter.append(new SettingItem(R.string.dk_save));

        mAdapter.append(new SettingItem(R.string.dk_share));
        mAdapter.setOnSettingItemClickListener(new SettingItemAdapter.OnSettingItemClickListener() {
            @Override
            public void onSettingItemClick(View view, SettingItem data) {
                if (data.desc == R.string.dk_save) {
                    if (onButtonClickListener != null) {
                        onButtonClickListener.onSaveClick(LogExportDialog.this);
                    }
                } else if (data.desc == R.string.dk_share) {
                    if (onButtonClickListener != null) {
                        onButtonClickListener.onShareClick(LogExportDialog.this);
                    }
                }
            }
        });
    }

    private OnButtonClickListener onButtonClickListener;

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public interface OnButtonClickListener {
        /**
         * @param dialog dialog
         */
        void onSaveClick(LogExportDialog dialog);

        /**
         * @param dialog dialog
         */
        void onShareClick(LogExportDialog dialog);
    }

    @Override
    public boolean isCancellable() {
        return false;
    }
}