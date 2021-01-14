package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.widget.titlebar.TitleBar;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by wanglikun on 2018/10/19.
 */

public class TextDetailFragment extends BaseFragment {
    private static final String TAG = "TextDetailFragment";

    private RecyclerView mContent;
    private TextContentAdapter mContentAdapter;

    private File mFile;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setOnTitleBarClickListener(new TitleBar.OnTitleBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }
        });
        initContent();
        Bundle data = getArguments();
        if (data != null) {
            mFile = (File) data.getSerializable(BundleKey.FILE_KEY);
        }
        readFile(mFile);
    }

    public void initContent() {
        mContent = findViewById(R.id.text_list);
        mContent.setLayoutManager(new LinearLayoutManager(getContext()));
        mContentAdapter = new TextContentAdapter(getContext());
        mContent.setAdapter(mContentAdapter);
    }

    private void readFile(File file) {
        if (mFile == null) {
            return;
        }
        FileReadTask task = new FileReadTask(this);
        task.execute(file);
    }

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_text_detail;
    }

    private static class FileReadTask extends AsyncTask<File, String, Void> {
        private WeakReference<TextDetailFragment> mReference;

        public FileReadTask(TextDetailFragment fragment) {
            mReference = new WeakReference<>(fragment);
        }

        @Override
        protected Void doInBackground(File... files) {
            try {
                FileReader fileReader = new FileReader(files[0]);
                BufferedReader br = new BufferedReader(fileReader);
                String textLine;
                while ((textLine = br.readLine()) != null) {
                    publishProgress(textLine);
                }
                br.close();
                fileReader.close();
            } catch (IOException e) {
                LogHelper.e(TAG, e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            if (mReference.get() != null) {
                mReference.get().mContentAdapter.append(values[0]);
            }
        }
    }
}
