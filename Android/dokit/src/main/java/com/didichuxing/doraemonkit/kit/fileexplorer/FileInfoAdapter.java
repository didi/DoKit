package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder;
import com.didichuxing.doraemonkit.util.DoKitFileUtil;

/**
 * Created by wanglikun on 2018/10/16.
 */

public class FileInfoAdapter extends AbsRecyclerAdapter<AbsViewBinder<FileInfo>, FileInfo> {
    private OnViewClickListener mOnViewClickListener;
    private OnViewLongClickListener mOnViewLongClickListener;

    public FileInfoAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<FileInfo> createViewHolder(View view, int viewType) {
        return new FileInfoViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_file_info, parent, false);
    }

    public class FileInfoViewHolder extends AbsViewBinder<FileInfo> {
        private TextView mName;
        private ImageView mIcon;
        private ImageView mMoreBtn;

        public FileInfoViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            mName = getView(R.id.name);
            mIcon = getView(R.id.icon);
            mMoreBtn = getView(R.id.more);
        }

        @Override
        public void bind(final FileInfo fileInfo) {
            getView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return mOnViewLongClickListener != null && mOnViewLongClickListener.onViewLongClick(v, fileInfo);
                }
            });
            mName.setText(fileInfo.file.getName());
            if (fileInfo.file.isDirectory()) {
                mIcon.setImageResource(R.mipmap.dk_dir_icon);
                mMoreBtn.setVisibility(View.VISIBLE);
            } else {
                if (DoKitFileUtil.getSuffix(fileInfo.file).equals(DoKitFileUtil.JPG)) {
                    mIcon.setImageResource(R.mipmap.dk_jpg_icon);
                } else if (DoKitFileUtil.getSuffix(fileInfo.file).equals(DoKitFileUtil.TXT)) {
                    mIcon.setImageResource(R.mipmap.dk_txt_icon);
                } else if (DoKitFileUtil.getSuffix(fileInfo.file).equals(DoKitFileUtil.DB)){
                    mIcon.setImageResource(R.mipmap.dk_file_db);
                }else {
                    mIcon.setImageResource(R.mipmap.dk_file_icon);
                }
                mMoreBtn.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onViewClick(View view, FileInfo data) {
            super.onViewClick(view, data);
            if (mOnViewClickListener != null) {
                mOnViewClickListener.onViewClick(view, data);
            }
        }
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
    }

    public void setOnViewLongClickListener(OnViewLongClickListener onViewLongClickListener) {
        mOnViewLongClickListener = onViewLongClickListener;
    }

    public interface OnViewClickListener {
        void onViewClick(View v, FileInfo fileInfo);
    }

    public interface OnViewLongClickListener {
        boolean onViewLongClick(View v, FileInfo fileInfo);
    }
}
