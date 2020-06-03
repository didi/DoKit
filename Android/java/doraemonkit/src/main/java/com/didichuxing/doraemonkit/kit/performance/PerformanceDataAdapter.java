package com.didichuxing.doraemonkit.kit.performance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder;

public class PerformanceDataAdapter extends AbsRecyclerAdapter<AbsViewBinder<PerformanceData>, PerformanceData> {
    private OnViewClickListener mOnViewClickListener;
    private OnViewLongClickListener mOnViewLongClickListener;

    public PerformanceDataAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<PerformanceData> createViewHolder(View view, int viewType) {
        return new PerformanceItemViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_performance_detail, parent, false);
    }

    public class PerformanceItemViewHolder extends AbsViewBinder<PerformanceData> {
        private TextView date;
        private TextView time;
        private TextView parameter;


        public PerformanceItemViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            date = getView(R.id.date);
            time = getView(R.id.time);
            parameter = getView(R.id.parameter);
        }

        @Override
        public void bind(PerformanceData performanceData) {
            date.setText(performanceData.date);
            time.setText(performanceData.time);
            parameter.setText(String.valueOf(performanceData.parameter));
        }

        @Override
        protected void onViewClick(View view, PerformanceData data) {
            super.onViewClick(view, data);
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
        void onViewClick(View v, PerformanceData data);
    }

    public interface OnViewLongClickListener {
        boolean onViewLongClick(View v, PerformanceData data);
    }

}
