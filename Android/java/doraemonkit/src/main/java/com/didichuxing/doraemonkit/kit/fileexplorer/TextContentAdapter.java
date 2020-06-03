package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder;

/**
 * Created by wanglikun on 2018/12/14.
 */

public class TextContentAdapter extends AbsRecyclerAdapter<AbsViewBinder<String>, String> {

    public TextContentAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<String> createViewHolder(View view, int viewType) {
        return new TextContentViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_text_content, parent, false);
    }

    private class TextContentViewHolder extends AbsViewBinder<String> {
        private TextView mTextView;

        public TextContentViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            mTextView = getView(R.id.text);
        }

        @Override
        public void bind(String s) {
            mTextView.setText(s);
        }
    }
}
