package com.didichuxing.doraemonkit.kit.crash;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.crash.CrashCaptureAdapter;
import com.didichuxing.doraemonkit.ui.widget.titlebar.TitleBar;

import java.io.File;

public class CrashCaptureFragment extends BaseFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }


    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_crash_capture;
    }

    private void initView() {
        TitleBar mTitleBar = findViewById(R.id.title_bar);
        mTitleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftClick() {
                getActivity().onBackPressed();
            }

            @Override
            public void onRightClick() {

            }
        });
        File file = new File(CrashHandlerManager.getInstance().getFilePath());
        if (file.exists()) {
            ListView crash = findViewById(R.id.lv_crash);
            final File[] listFiles = file.listFiles();
            if (listFiles != null && listFiles.length != 0) {
                crash.setAdapter(new CrashCaptureAdapter(listFiles));
            } else {
                Toast.makeText(getContext(), R.string.dk_crash_capture_no_record, Toast.LENGTH_SHORT).show();
            }
            crash.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bundle bundle = new Bundle();
                    File listFile = listFiles[position];
                    bundle.putSerializable(BundleKey.FILE_KEY, listFile);
                    showContent(CrashDetailFragment.class, bundle);
                }
            });
        } else {
            Toast.makeText(getContext(), R.string.dk_crash_capture_no_record, Toast.LENGTH_SHORT).show();
        }

    }

}
