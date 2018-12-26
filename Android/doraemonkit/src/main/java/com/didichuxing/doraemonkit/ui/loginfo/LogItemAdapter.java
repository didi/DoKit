package com.didichuxing.doraemonkit.ui.loginfo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.logInfo.LogInfoItem;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsViewBinder;

/**
 * Created by wanglikun on 2018/10/30.
 */

public class LogItemAdapter extends AbsRecyclerAdapter<AbsViewBinder<LogInfoItem>, LogInfoItem> {


    public LogItemAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<LogInfoItem> createViewHolder(View view, int viewType) {
        return new LogInfoViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_log, parent, false);
    }

    public class LogInfoViewHolder extends AbsViewBinder<LogInfoItem> {

        private TextView mLogText;

        private ImageView mShowFullBtn;

        public LogInfoViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            mLogText = getView(R.id.log_text);
            mShowFullBtn = getView(R.id.show_full);
        }

        @Override
        protected void onViewClick(View view, LogInfoItem data) {
            super.onViewClick(view, data);
            data.showFull = !data.showFull;
            if (data.showFull) {
                mLogText.setMaxLines(Integer.MAX_VALUE);
                mShowFullBtn.setImageResource(R.drawable.dk_arrow_bottom);
            } else {
                mLogText.setMaxLines(2);
                mShowFullBtn.setImageResource(R.drawable.dk_arrow_right);
            }
        }

        @Override
        public void bind(LogInfoItem item) {
            switch (item.level) {
                case Log.VERBOSE: {
                    mLogText.setTextColor(getContext().getResources().getColor(R.color.dk_color_BBBBBB));
                }
                break;
                case Log.DEBUG: {
                    mLogText.setTextColor(getContext().getResources().getColor(R.color.dk_color_0070BB));
                }
                break;
                case Log.INFO: {
                    mLogText.setTextColor(getContext().getResources().getColor(R.color.dk_color_48BB31));
                }
                break;
                case Log.WARN: {
                    mLogText.setTextColor(getContext().getResources().getColor(R.color.dk_color_BBBB23));
                }
                break;
                case Log.ERROR: {
                    mLogText.setTextColor(getContext().getResources().getColor(R.color.dk_color_FF0006));
                }
                break;
                case Log.ASSERT: {
                    mLogText.setTextColor(getContext().getResources().getColor(R.color.dk_color_8F0005));
                }
                break;
                default:
                    break;
            }
            String text = item.date + " " + item.time + "\n" + item.meseage;
            mLogText.setText(text);
        }
    }
}