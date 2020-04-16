package com.didichuxing.doraemonkit.kit.performance;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

public class PolyLineAdapter extends RecyclerView.Adapter<PolyLineAdapter.ViewHolder> {
    private List<PerformanceData> data;

    public int maxValue;
    public int minValue;
    public int itemWidth;
    public boolean drawDiver;
    public float pointSize;
    public boolean touchable;
    private boolean showLatestLabel;

    private OnViewClickListener onViewClickListener;


    private PolyLineAdapter() {

    }

    private PolyLineAdapter(List<PerformanceData> data, int maxValue, int minValue, int itemWidth) {
        this.data = data;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.itemWidth = itemWidth;

    }

    public void setData(List<PerformanceData> d) {
        if (data != null) {
            data.clear();
            data.addAll(d);

            notifyDataSetChanged();
        }
    }

    public void addData(PerformanceData bean) {
        data.add(bean);
        notifyDataSetChanged();
    }

    public void addData(List<PerformanceData> beans) {
        data.addAll(beans);
        notifyDataSetChanged();
    }

    public static class Builder {
        private int maxValue = 100;
        private int minValue = 0;
        private final int itemWidth;
        private List<PerformanceData> data = new ArrayList<>();
        private boolean drawDiver = true;
        private float pointSize;
        private boolean touchable = true;
        // 显示最后一个item的文案，用以绘制实时折线图
        private boolean showLatestLabel;

        public Builder(Context context, int itemNumber) {
            this.itemWidth = UIUtils.getWidthPixels() / itemNumber;
        }

        public Builder setMaxValue(int maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Builder setMinValue(int minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder setData(List<PerformanceData> data) {
            this.data = data;
            return this;
        }

        public Builder setDrawDiver(boolean drawDiver) {
            this.drawDiver = drawDiver;
            return this;
        }

        public Builder setPointSize(float size) {
            this.pointSize = size;
            return this;
        }

        public PolyLineAdapter build() {
            PolyLineAdapter adapter = new PolyLineAdapter(data, maxValue, minValue, itemWidth);
            adapter.drawDiver = drawDiver;
            adapter.pointSize = pointSize;
            adapter.touchable = touchable;
            adapter.showLatestLabel = showLatestLabel;
            return adapter;
        }

        public Builder setTouchable(boolean touchable) {
            this.touchable = touchable;
            return this;
        }

        public Builder setShowLatestLabel(boolean showLatestLabel) {
            this.showLatestLabel = showLatestLabel;
            return this;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        PolyLineItemView item = new PolyLineItemView(parent.getContext());
        item.setMinValue(minValue);
        item.setMaxValue(maxValue);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(itemWidth, ViewGroup.LayoutParams.MATCH_PARENT);//这个数字表示每一个item的宽度
        item.setLayoutParams(layoutParams);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull PolyLineAdapter.ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        this.onViewClickListener = onViewClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        PolyLineItemView item;

        public ViewHolder(View itemView) {
            super(itemView);
            this.item = (PolyLineItemView) itemView;
            item.setDrawDiver(drawDiver);
            item.setPointSize(pointSize);
            item.setTouchable(touchable);
        }

        public void bindData(final int position) {
            if (onViewClickListener != null) {
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onViewClickListener.onViewClick(position, data.get(position));
                    }
                });
            }
            if (position == 0) {
                item.setDrawLeftLine(false);
            } else {
                item.setDrawLeftLine(true);
                item.setlastValue((data.get(position - 1)).parameter);
            }
            item.setCurrentValue((data.get(position)).parameter);
            item.setLabel(data.get(position).date);
            if (position == data.size() - 1) {
                item.setDrawRightLine(false);
            } else {
                item.setDrawRightLine(true);
                item.setNextValue((data.get(position + 1)).parameter);
            }
            item.showLabel(showLatestLabel && position > data.size() - 3);
        }
    }

    public interface OnViewClickListener {
        void onViewClick(int position, PerformanceData data);
    }


}