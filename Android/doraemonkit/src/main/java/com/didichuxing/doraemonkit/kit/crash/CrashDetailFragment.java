package com.didichuxing.doraemonkit.kit.crash;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.widget.titlebar.TitleBar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class CrashDetailFragment extends BaseFragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();

    }

    @Override
    protected int onRequestLayout() {
        return R.layout.fragment_crash_detail_info;
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

        Bundle bundle = getArguments();
        if (bundle != null) {
            try {
                File file = (File) bundle.getSerializable(BundleKey.FILE_KEY);
                FileInputStream fin = new FileInputStream(file);
                if (fin != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(fin);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStreamReader.close();

                    ((TextView) findViewById(R.id.tv_crash_detail)).setText(stringBuilder.toString());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
