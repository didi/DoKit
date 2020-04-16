package com.didichuxing.doraemonkit.kit.webdoor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder;

/**
 * Created by wanglikun on 2018/11/17.
 */

public class WebDoorHistoryAdapter extends AbsRecyclerAdapter<AbsViewBinder<String>, String> {
    private OnItemClickListener mOnItemClickListener;

    public WebDoorHistoryAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<String> createViewHolder(View view, int viewType) {
        return new WebDoorHistoryViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_web_door_history, parent, false);
    }

    public class WebDoorHistoryViewHolder extends AbsViewBinder<String> {
        private TextView mContent;

        public WebDoorHistoryViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            mContent = getView(R.id.content);
        }

        @Override
        public void bind(String s) {
            mContent.setText(s);
        }

        @Override
        protected void onViewClick(View view, String data) {
            super.onViewClick(view, data);
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, data);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String data);
    }
}
