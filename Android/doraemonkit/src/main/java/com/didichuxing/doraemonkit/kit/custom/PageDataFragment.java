package com.didichuxing.doraemonkit.kit.custom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.common.PerformanceDataManager;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.ui.widget.titlebar.TitleBar;
import com.didichuxing.doraemonkit.util.JsonUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PageDataFragment extends BaseFragment {
    private RecyclerView mRvList;
    private PageDataItemAdapter mAdapter;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_monitor_pagedata;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PerformanceDataManager.getInstance().init();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

        initData();
    }

    private void initView() {
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

        mRvList = findViewById(R.id.info_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvList.setLayoutManager(layoutManager);
        mAdapter = new PageDataItemAdapter(getContext());
        mRvList.setAdapter(mAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.dk_divider_gray));
        mRvList.addItemDecoration(decoration);
        mRvList.setAdapter(mAdapter);
    }

    private void initData() {
        new LoadDataTask().execute(PerformanceDataManager.getInstance().getCustomFilePath());
    }

    private class LoadDataTask extends AsyncTask<String, Integer, List<PageDataItem>> {
        @Override
        protected void onPostExecute(List<PageDataItem> items) {
            mAdapter.setData(items);
        }

        @Override
        protected List<PageDataItem> doInBackground(String... strings) {
            File file = new File(strings[0]);

            String fileString = getFileString(file);
            fileString = '['+fileString+']';
            fileString = fileString.replaceAll("\\}\\{","},{");

            List<UploadMonitorInfoBean> infoBeans = JsonUtil.jsonToList(fileString, UploadMonitorInfoBean.class);

            return convert2ItemData(infoBeans);
        }
    }

    private List<PageDataItem> convert2ItemData(List<UploadMonitorInfoBean> infoBeans) {
        List<PageDataItem> dataItems = new ArrayList<>();
        if(null == infoBeans || 0 >= infoBeans.size()){
            return dataItems;
        }

        Map<String,List<UploadMonitorItem>> listMap = new TreeMap<>();
        for (UploadMonitorInfoBean infoBean : infoBeans) {
            if(null == infoBean || null == infoBean.performanceArray || 0 >= infoBean.performanceArray.size()){
                continue;
            }

            List<UploadMonitorItem> performanceInfos = infoBean.performanceArray;
            for (UploadMonitorItem item : performanceInfos) {
                List<UploadMonitorItem> itemList = listMap.get(item.page);
                if(null == itemList){
                    itemList = new ArrayList<>();
                    listMap.put(item.page, itemList);
                }

                itemList.add(item);
            }
        }

        for (String pageName : listMap.keySet()) {
            dataItems.addAll(getPageItemData(pageName, listMap.get(pageName)));
        }

        return dataItems;
    }

    private List<PageDataItem> getPageItemData(String appName, List<UploadMonitorItem> performanceInfos) {
        List<PageDataItem> dataItems = new ArrayList<>();

        PageDataItem item = new PageDataItem();
        item.pageName = appName;

        item.upNetWork  =new PageDataItemChild(R.string.dk_frameinfo_upstream);
        item.downNetWork = new PageDataItemChild(R.string.dk_frameinfo_downstream);
        item.memory = new PageDataItemChild(R.string.dk_frameinfo_ram);
        item.cpu = new PageDataItemChild(R.string.dk_frameinfo_cpu);
        item.fps = new PageDataItemChild(R.string.dk_frameinfo_fps);

        for (UploadMonitorItem monitorItem : performanceInfos) {
            setValue(item.upNetWork,monitorItem.upFlow);
            setValue(item.downNetWork,monitorItem.downFlow);
            setValue(item.memory,monitorItem.memory);
            setValue(item.cpu,monitorItem.cpu);
            setValue(item.fps,monitorItem.fps);
        }

        int size = performanceInfos.size();
        if(0 < size){
            item.upNetWork.avg /= size;
            item.downNetWork.avg /= size;
            item.memory.avg /= size;
            item.cpu.avg /= size;
            item.fps.avg /= size;
        }else{
            item.upNetWork.avg = 0;
            item.downNetWork.avg = 0;
            item.memory.avg = 0;
            item.cpu.avg = 0;
            item.fps.avg = 0;
        }

        dataItems.add(item);

        return dataItems;
    }

    private void setValue(PageDataItemChild child, double newValue) {
        child.min = 0 == child.min || 0 == newValue ? child.min+newValue : Math.min(child.min, newValue);
        child.max = 0 == child.max || 0 == newValue ? child.max+newValue : Math.max(child.max, newValue);
        child.avg += newValue;
    }

    private String getFileString(File file) {
        StringBuilder stringBuilder = new StringBuilder();
        if (file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempString = null;
                while ((tempString = reader.readLine()) != null) {
                    stringBuilder.append(tempString);
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

        return stringBuilder.toString();
    }
}
