package com.didichuxing.doraemonkit.kit.timecounter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.timecounter.bean.CounterInfo;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder;

import static android.text.format.DateUtils.FORMAT_SHOW_TIME;

public class TimeCounterListAdapter extends AbsRecyclerAdapter<AbsViewBinder<CounterInfo>, CounterInfo> {

    public TimeCounterListAdapter(Context context) {
        super(context);
    }

    @Override
    protected AbsViewBinder<CounterInfo> createViewHolder(View view, int viewType) {
        return new ItemViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(R.layout.dk_item_time_counter_list, parent, false);
    }

    private class ItemViewHolder extends AbsViewBinder<CounterInfo> {

        private TextView tvTime;
        private TextView tvTitle;
        private TextView tvTotal;
        private TextView tvPause;
        private TextView tvLaunch;
        private TextView tvRender;
        private TextView tvOther;

        public ItemViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            tvTime = getView(R.id.time);
            tvTitle = getView(R.id.title);
            tvTotal = getView(R.id.total_cost);
            tvPause = getView(R.id.pause_cost);
            tvLaunch = getView(R.id.launch_cost);
            tvRender = getView(R.id.render_cost);
            tvOther = getView(R.id.other_cost);
        }

        @Override
        public void bind(CounterInfo counterInfo) {

        }

        @Override
        public void bind(final CounterInfo info, int position) {
            tvTitle.setText(info.title);
            String time = DateUtils.formatDateTime(getContext(),
                    info.time, FORMAT_SHOW_TIME);
            tvTime.setText(time);
            setTotalCost(info.totalCost);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    info.show = !info.show;
                    showDetail(info);
                    if (info.type == CounterInfo.TYPE_APP && mContext != null) {
                        //跳转启动耗时详情页
                        DoKit.launchFullScreen(AppStartInfoFragment.class,mContext);
                    }
                }
            });
            showDetail(info);
        }

        private void setTotalCost(long cost) {
            tvTotal.setText("Total Cost: " + cost + "ms");
            if (cost <= CounterInfo.ACTIVITY_COST_FAST) {
                tvTotal.setTextColor(getContext().getResources().getColor(R.color.dk_color_48BB31));
            } else if (cost <= CounterInfo.ACTIVITY_COST_SLOW) {
                tvTotal.setTextColor(getContext().getResources().getColor(R.color.dk_color_FAD337));
            } else {
                tvTotal.setTextColor(getContext().getResources().getColor(R.color.dk_color_FF0006));
            }
        }

        //显示详情
        private void showDetail(CounterInfo info) {
            if (info.type == CounterInfo.TYPE_APP) {
                info.show = false;
            }
            if (info.show) {
                tvPause.setVisibility(View.VISIBLE);
                tvLaunch.setVisibility(View.VISIBLE);
                tvRender.setVisibility(View.VISIBLE);
                tvOther.setVisibility(View.VISIBLE);

                tvPause.setText("Pause Cost: " + info.pauseCost + "ms");
                tvLaunch.setText("Launch Cost: " + info.launchCost + "ms");
                tvRender.setText("Render Cost: " + info.renderCost + "ms");
                tvOther.setText("Other Cost: " + info.otherCost + "ms");
            } else {
                tvPause.setVisibility(View.GONE);
                tvLaunch.setVisibility(View.GONE);
                tvRender.setVisibility(View.GONE);
                tvOther.setVisibility(View.GONE);
            }
        }
    }
}

