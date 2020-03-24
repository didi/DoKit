package com.didichuxing.doraemonkit.kit.largepicture;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.picasso.MemoryPolicy;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsViewBinder;
import com.didichuxing.doraemonkit.util.ClipboardUtils;
import com.didichuxing.doraemonkit.picasso.DokitPicasso;

import java.text.DecimalFormat;

public class LargeImageListAdapter extends AbsRecyclerAdapter<AbsViewBinder<LargeImageInfo>, LargeImageInfo> {
    private DecimalFormat mDecimalFormat = new DecimalFormat("#0.00");

    public LargeImageListAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<LargeImageInfo> createViewHolder(View view, int viewType) {
        return new ItemViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_large_img_list, parent, false);
    }

    private class ItemViewHolder extends AbsViewBinder<LargeImageInfo> {
        private ImageView iv;
        private TextView tvLink;
        /**
         * 框架
         */
        private TextView tvFrameWork;

        /**
         * 文件大小
         */
        private TextView tvFileSize;
        /**
         * 内存大小
         */
        private TextView tvMemorySize;
        /**
         * 图片尺寸
         */
        private TextView tvSize;

        private Button btnCopy;

        public ItemViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            iv = getView(R.id.iv);
            tvLink = getView(R.id.tv_link);
            tvFrameWork = getView(R.id.tv_framework);
            tvFileSize = getView(R.id.tv_file_size);
            tvMemorySize = getView(R.id.tv_memory_size);
            tvSize = getView(R.id.tv_size);
            btnCopy = getView(R.id.btn_copy);
        }

        @Override
        public void bind(final LargeImageInfo largeImageInfo) {
            DokitPicasso.with(DoraemonKit.APPLICATION)
                    .load(largeImageInfo.getUrl())
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .resize(ConvertUtils.dp2px(100), ConvertUtils.dp2px(100))
                    .centerCrop()
                    .into(iv);
            tvLink.setText(largeImageInfo.getUrl());
            tvFrameWork.setText(String.format("framework:%s", largeImageInfo.getFramework()));
            tvFileSize.setText(String.format("fileSize:%s", mDecimalFormat.format(largeImageInfo.getFileSize()) + "KB"));
            tvMemorySize.setText(String.format("memorySize:%s", mDecimalFormat.format(largeImageInfo.getMemorySize()) + "MB"));
            tvSize.setText(String.format("width:%s   height:%s", largeImageInfo.getWidth(), largeImageInfo.getHeight()));
            btnCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardUtils.copyUri(Uri.parse(largeImageInfo.getUrl()));
                    ToastUtils.showShort("image url  has copied");
                }

            });
        }


    }

}

