package com.didichuxing.doraemonkit.kit.performance;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PerformanceFragment extends BaseFragment {
    public final static int CPU = 0;
    public final static int RAM = 1;
    public final static int FPS = 2;

    private PerformanceDataAdapter performanceDataAdapter;
    private PolyLineAdapter adapter;
    private TextView parameter;
    private TextView time;
    private TextView date;

    @Override

    protected int onRequestLayout() {
        return R.layout.dk_fragment_cpu_cache_log;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PerformanceDataManager.getInstance().init();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initview();
    }

    private void initview() {


        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftClick() {
                getActivity().onBackPressed();
            }

            @Override
            public void onRightClick() {

            }
        });
        RecyclerView dataShow = findViewById(R.id.data_show);

        PolyLineAdapter.Builder builder = new PolyLineAdapter.Builder(getActivity(), 10);

        dataShow.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


        RecyclerView detail = findViewById(R.id.data_detail);
        detail.setLayoutManager(new LinearLayoutManager(getActivity()));
        performanceDataAdapter = new PerformanceDataAdapter(getActivity());
        detail.setAdapter(performanceDataAdapter);


        TextView model = findViewById(R.id.model);
        Bundle arguments = getArguments();
        if (arguments != null) {
            int type = arguments.getInt(BundleKey.PERFORMANCE_TYPE, CPU);
            if (type == RAM) {
                builder.setMaxValue((int) PerformanceDataManager.getInstance().getMaxMemory()).setMinValue(0);
                model.setText(R.string.dk_frameinfo_ram);
                new LoadDataTask().execute(PerformanceDataManager.getInstance().getMemoryFilePath());
            } else if (type == FPS) {
                builder.setMaxValue(100).setMinValue(0);
                model.setText(R.string.dk_frameinfo_fps);
                new LoadDataTask().execute(PerformanceDataManager.getInstance().getFpsFilePath());
            } else {
                builder.setMaxValue(100).setMinValue(0);
                model.setText(R.string.dk_frameinfo_cpu);
                new LoadDataTask().execute(PerformanceDataManager.getInstance().getCpuFilePath());
            }
        }

        adapter = builder.build();
        dataShow.setAdapter(adapter);


        parameter = findViewById(R.id.parameter);
        time = findViewById(R.id.time);
        date = findViewById(R.id.date);

        performanceDataAdapter.setOnViewClickListener(new PerformanceDataAdapter.OnViewClickListener() {
            @Override
            public void onViewClick(View v, PerformanceData data) {
                updateTips(data);
            }
        });

        adapter.setOnViewClickListener(new PolyLineAdapter.OnViewClickListener() {
            @Override
            public void onViewClick(int position, PerformanceData data) {
                updateTips(data);
            }
        });


    }

    private void updateTips(PerformanceData data) {
        parameter.setText(String.valueOf(data.parameter));
        time.setText(data.time);
        date.setText(data.date);
    }


    private class LoadDataTask extends AsyncTask<String, Integer, List<PerformanceData>> {
        @Override
        protected void onPostExecute(List<PerformanceData> result) {
            performanceDataAdapter.append(result);
            adapter.setData(result);
            if (result.size() > 1) {
                updateTips(result.get(1));
            }
        }

        @Override
        protected List doInBackground(String... strings) {
            File file = new File(strings[0]);
            ArrayList<PerformanceData> datas = new ArrayList<>();
            if (file.exists()) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(file));
                    String tempString = null;
                    while ((tempString = reader.readLine()) != null) {
                        String[] split = tempString.split(" ");
                        datas.add(new PerformanceData(split[1], split[2], Float.valueOf(split[0])));
                    }
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
            return datas;
        }
    }


}
