package com.didichuxing.doraemonkit.kit.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsRecyclerAdapter;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.AbsViewBinder;

import java.text.DecimalFormat;

public class PageDataItemAdapter extends AbsRecyclerAdapter<AbsViewBinder<PageDataItem>, PageDataItem> {
    private Context mContext;
    public PageDataItemAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected AbsViewBinder<PageDataItem> createViewHolder(View view, int viewType) {
        return new ViewHolder(view);
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup parent, int viewType) {

        return inflater.inflate(R.layout.dk_fragment_monitor_pagedata_item, parent, false);
    }

    public class ViewHolder extends AbsViewBinder<PageDataItem> {
        private TextView page_name_txt;
        private View up_network_item;
        private View down_network_item;
        private View memory_item;
        private View cpu_item;
        private View fps_item;

        public ViewHolder(View view) {
            super(view);
        }

        @Override
        protected void getViews() {
            page_name_txt = getView(R.id.page_name_txt);

            up_network_item = getView(R.id.up_network_item);
            down_network_item = getView(R.id.down_network_item);
            memory_item = getView(R.id.memory_item);
            cpu_item = getView(R.id.cpu_item);
            fps_item = getView(R.id.fps_item);
        }

        @Override
        public void bind(PageDataItem item) {
            page_name_txt.setText(item.pageName);

            setValue(up_network_item, item.upNetWork);
            setValue(down_network_item, item.downNetWork);
            setValue(memory_item, item.memory);
            setValue(cpu_item, item.cpu);
            setValue(fps_item, item.fps);
        }
    }

    private void setValue(View iteView, PageDataItemChild data) {
        int visibility = data.getVisibility(data);
        iteView.setVisibility(visibility);
        if(View.GONE == visibility){
            return;
        }
        String rule = getFormatRule(data.nameResId);

        ((TextView)iteView.findViewById(R.id.data_name_txt)).setText(data.nameResId);
        ((TextView)iteView.findViewById(R.id.high_data_txt)).setText(String.format(rule, getFormatText(data.nameResId, data.max)));
        ((TextView)iteView.findViewById(R.id.low_data_txt)).setText(String.format(rule, getFormatText(data.nameResId, data.min)));
        ((TextView)iteView.findViewById(R.id.avg_data_txt)).setText(String.format("%s : "+rule, mContext.getString(R.string.dk_frameinfo_avg_value),getFormatText(data.nameResId, data.avg)));
    }

    private String getFormatText(int nameResId, double value){
        if (nameResId == R.string.dk_frameinfo_downstream || nameResId == R.string.dk_frameinfo_upstream) {
            return RealTimePerformDataFloatPage.getFlowTxt((long) value);
        }
        DecimalFormat df=new DecimalFormat(".#");
        return df.format(value);
    }
    private String getFormatRule(int nameResId){
        String formatRule = "";
        if (nameResId == R.string.dk_frameinfo_ram) {
            formatRule = "%sM";
        } else if (nameResId == R.string.dk_frameinfo_cpu) {
            formatRule = "%s%%";
        } else if (nameResId == R.string.dk_frameinfo_fps) {
            formatRule = "%s";
        } else if (nameResId == R.string.dk_frameinfo_downstream || nameResId == R.string.dk_frameinfo_upstream) {
            formatRule = "%s";
        }

        return formatRule;
    }
}
