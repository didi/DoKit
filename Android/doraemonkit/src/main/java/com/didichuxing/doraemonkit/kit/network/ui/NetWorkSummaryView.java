package com.didichuxing.doraemonkit.kit.network.ui;

import android.content.Context;
import android.content.res.Resources;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.utils.ByteUtil;
import com.didichuxing.doraemonkit.kit.network.utils.CostTimeUtil;
import com.didichuxing.doraemonkit.widget.chart.BarChart;
import com.didichuxing.doraemonkit.widget.chart.PieChart;

import java.util.ArrayList;
import java.util.List;

public class NetWorkSummaryView extends LinearLayout {

    public NetWorkSummaryView(Context context) {
        super(context);
        inflate(context, R.layout.dk_fragment_network_summary_page, this);
        initView();
    }

    public NetWorkSummaryView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.dk_fragment_network_summary_page, this);
        initView();
    }

    private void initView() {
        TextView totalSec = findViewById(R.id.total_sec);
        TextView totalNumber = findViewById(R.id.total_number);
        TextView totalUpload = findViewById(R.id.total_upload);
        TextView totalDown = findViewById(R.id.total_down);

        int postCount = NetworkManager.get().getPostCount();
        int getCount = NetworkManager.get().getGetCount();

        int totalCount = NetworkManager.get().getTotalCount();
        totalNumber.setText(String.valueOf(totalCount));

        long time = NetworkManager.get().getRunningTime();
        totalSec.setText(CostTimeUtil.formatTime(getContext(), time));

        long requestSize = NetworkManager.get().getTotalRequestSize();
        long responseSize = NetworkManager.get().getTotalResponseSize();

        totalUpload.setText(ByteUtil.getPrintSizeForSpannable(requestSize));
        totalDown.setText(ByteUtil.getPrintSizeForSpannable(responseSize));


        PieChart chart = findViewById(R.id.network_pier_chart);
        List<PieChart.PieData> data = new ArrayList<>();
        Resources resource = getResources();
        if (postCount != 0) {
            data.add(new PieChart.PieData(resource.getColor(R.color.dk_color_55A8FD), postCount));
        }
        if (getCount != 0) {
            data.add(new PieChart.PieData(resource.getColor(R.color.dk_color_FAD337), getCount));
        }
        chart.setData(data);


        BarChart barChart = findViewById(R.id.network_bar_chart);
        barChart.setData(postCount, getResources().getColor(R.color.dk_color_55A8FD), getCount, getResources().getColor(R.color.dk_color_FAD337));
    }


}
