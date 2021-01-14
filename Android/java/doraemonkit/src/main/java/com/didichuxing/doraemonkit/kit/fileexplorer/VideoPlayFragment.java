package com.didichuxing.doraemonkit.kit.fileexplorer;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.widget.videoview.MyVideoView;

import java.io.File;

/**
 * Created by wanglikun on 2019/4/16
 */
public class VideoPlayFragment extends BaseFragment {
    private MyVideoView mVideoView;
    private File mFile;

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_video_play;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        if (data != null) {
            mFile = (File) data.getSerializable(BundleKey.FILE_KEY);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mVideoView = findViewById(R.id.video_view);
        mVideoView.register(getActivity());
        mVideoView.setVideoPath(mFile.getPath());
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mVideoView.onResume();
    }
}