package com.didichuxing.doraemonkit.ui.fileexplorer;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.dialog.DialogListener;
import com.didichuxing.doraemonkit.ui.dialog.DialogProvider;
import com.didichuxing.doraemonkit.ui.setting.SettingItem;
import com.didichuxing.doraemonkit.ui.setting.SettingItemAdapter;

import java.io.File;

/**
 * Created by wanglikun on 2019/4/16
 */
public class FileExplorerChooseDialog extends DialogProvider<File> {
    private RecyclerView mChooseList;
    private SettingItemAdapter mAdapter;

    public FileExplorerChooseDialog(File data, DialogListener listener) {
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
    protected void bindData(final File file) {
        if (file.isFile()) {
            mAdapter.append(new SettingItem(R.string.dk_share));
        }
        mAdapter.append(new SettingItem(R.string.dk_delete));
        mAdapter.setOnSettingItemClickListener(new SettingItemAdapter.OnSettingItemClickListener() {
            @Override
            public void onSettingItemClick(View view, SettingItem data) {
                if (data.desc == R.string.dk_delete) {
                    if (onButtonClickListener != null) {
                        onButtonClickListener.onDeleteClick(FileExplorerChooseDialog.this);
                    }
                } else if (data.desc == R.string.dk_share) {
                    if (onButtonClickListener != null) {
                        onButtonClickListener.onShareClick(FileExplorerChooseDialog.this);
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
        void onDeleteClick(FileExplorerChooseDialog dialog);

        void onShareClick(FileExplorerChooseDialog dialog);
    }
}