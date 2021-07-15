package com.didichuxing.doraemonkit.kit.crash;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder;
import com.didichuxing.doraemonkit.util.DoKitFormatUtil;

/**
 * Created by wanglikun on 2019-06-12
 */
public class CrashHistoryAdapter extends AbsRecyclerAdapter<AbsViewBinder<CrashInfo>, CrashInfo> {

    public CrashHistoryAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<CrashInfo> createViewHolder(View view, int viewType) {
        return new CrashHistoryViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_crash_history, parent, false);
    }

    public static class CrashHistoryViewHolder extends AbsViewBinder<CrashInfo> {
        private TextView mContent;
        private TextView mTime;

        public CrashHistoryViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            mContent = getView(R.id.content);
            mTime = getView(R.id.time);
        }

        @Override
        public void bind(CrashInfo info) {
            mContent.setText(Log.getStackTraceString(info.tr));
            mTime.setText(DoKitFormatUtil.format(info.time));
        }
    }
}