package com.didichuxing.doraemonkit.weex.storage;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder;
import com.didichuxing.doraemonkit.weex.R;

/**
 * @author haojianglong
 * @date 2019-06-18
 */

public class StorageAdapter extends AbsRecyclerAdapter<StorageAdapter.ViewHolder, StorageInfo> {

    private OnItemClickListener onItemClickListener;
    private final String KEY = "key";
    private final String VALUE = "value";

    private final int TITLE_BACKGROUND_COLOR = Color.parseColor("#BBBBBB");
    private final int NORMAL_BACKGROUND_COLOR = Color.parseColor("#CDCDCD");

    public StorageAdapter(Context context) {
        super(context);
        append(new StorageInfo(KEY, VALUE));
    }

    @Override
    protected ViewHolder createViewHolder(View view, int viewType) {
        return new ViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_storage_watch, parent, false);
    }

    public class ViewHolder extends AbsViewBinder<StorageInfo> {

        private TextView key;
        private TextView value;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            key = getView(R.id.tv_tip_key);
            value = getView(R.id.tv_tip_value);
        }

        @Override
        public void bind(final StorageInfo bean) {
            if (getAdapterPosition() == 0) {
                key.setBackgroundColor(TITLE_BACKGROUND_COLOR);
                value.setBackgroundColor(TITLE_BACKGROUND_COLOR);
            } else {
                key.setBackgroundColor(NORMAL_BACKGROUND_COLOR);
                value.setBackgroundColor(NORMAL_BACKGROUND_COLOR);
            }
            key.setText(bean.key);
            value.setText(bean.value);
            getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() != 0 && onItemClickListener != null) {
                        onItemClickListener.onItemClick(bean);
                    }
                }
            });
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        /**
         * 点击事件
         *
         * @param info
         */
        void onItemClick(StorageInfo info);
    }

}
