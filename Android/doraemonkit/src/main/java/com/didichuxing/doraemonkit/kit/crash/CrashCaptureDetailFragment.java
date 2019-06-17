package com.didichuxing.doraemonkit.kit.crash;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.crash.CrashHistoryAdapter;
import com.didichuxing.doraemonkit.ui.widget.titlebar.TitleBar;

import java.util.List;

public class CrashCaptureDetailFragment extends BaseFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_crash_capture_detail;
    }

    private void initView() {
        TitleBar mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
        TextView noRecordHint = findViewById(R.id.no_record_hint);
        RecyclerView crashList = findViewById(R.id.crash_list);
        List<CrashInfo> caches = CrashCaptureManager.getInstance().getCrashCaches();
        if (caches.isEmpty()) {
            noRecordHint.setVisibility(View.VISIBLE);
            crashList.setVisibility(View.GONE);
        } else {
            noRecordHint.setVisibility(View.GONE);
            crashList.setVisibility(View.VISIBLE);
            crashList.setLayoutManager(new LinearLayoutManager(getContext()));
            CrashHistoryAdapter adapter = new CrashHistoryAdapter(getContext());
            adapter.append(caches);
            crashList.setAdapter(adapter);
        }
    }
}
