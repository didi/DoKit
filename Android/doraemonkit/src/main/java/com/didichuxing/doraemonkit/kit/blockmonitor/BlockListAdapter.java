package com.didichuxing.doraemonkit.kit.blockmonitor;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.blockmonitor.bean.BlockInfo;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder;

import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_TIME;

public class BlockListAdapter extends AbsRecyclerAdapter<AbsViewBinder<BlockInfo>, BlockInfo> {

    private OnItemClickListener mListener;

    public BlockListAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<BlockInfo> createViewHolder(View view, int viewType) {
        return new ItemViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_block_list, parent, false);
    }

    private class ItemViewHolder extends AbsViewBinder<BlockInfo> {
        private TextView tvTime;
        private TextView tvTitle;

        public ItemViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            tvTime = getView(R.id.time);
            tvTitle = getView(R.id.title);
        }

        @Override
        public void bind(BlockInfo blockInfoEx) {

        }

        @Override
        public void bind(final BlockInfo info, int position) {
            String index;
            index = (BlockListAdapter.this.getItemCount() - position) + ". ";
            String title = index + info.concernStackString + " " +
                    getContext().getString(R.string.dk_block_class_has_blocked, String.valueOf(info.timeCost));
            tvTitle.setText(title);
            String time = DateUtils.formatDateTime(getContext(),
                    info.time, FORMAT_SHOW_TIME | FORMAT_SHOW_DATE);
            tvTime.setText(time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClick(info);
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onClick(BlockInfo info);
    }
}

