package com.didichuxing.doraemonkit.widget.videoview;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * Created by wanglikun on 2019/4/16
 */
public class MyVideoView extends RelativeLayout {

    private CustomVideoView videoView;
    private SeekBar seekbarProgress;
    private ImageView btnController;
    private TextView tvCurrentProgress;
    private TextView tvTotalProgress;
    private ImageView ivVolume;
    private SeekBar seekbarVolume;
    private ImageView btnScreen;
    private FrameLayout flVolume;
    private FrameLayout flLight;
    private LinearLayout llyController;
    private RelativeLayout rlContainer;
    private AudioManager mAudioManager;
    private int screenWidth;
    private int screenHeight;
    private Context mContext;
    private View videoLayout;
    private Activity mActivity;
    private int videoPos;
    private int state = 0;
    private String mVideoPath;
    private boolean isVerticalScreen = true;
    private static final int UPDATE_PROGRESS = 1;

    public MyVideoView(Context context) {
        super(context, null);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
        initView();
        initData();
        initListener();
    }

    public void register(Activity activity) {
        this.mActivity = activity;
    }

    public void setVideoPath(String path) {

        this.mVideoPath = path;
        if (path.startsWith("http") || path.startsWith("https")) {
            videoView.setVideoURI(Uri.parse(path));
        } else {
            videoView.setVideoPath(mVideoPath);
        }
    }

    private void init() {
        screenWidth = UIUtils.getWidthPixels();
        screenHeight = UIUtils.getRealHeightPixels();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    private void initView() {

        videoLayout = LayoutInflater.from(mContext).inflate(R.layout.dk_video_layout, this, true);
        flVolume = videoLayout.findViewById(R.id.fl_volume);
        flLight = videoLayout.findViewById(R.id.fl_light);
        videoView = videoLayout.findViewById(R.id.videoView);
        seekbarProgress = videoLayout.findViewById(R.id.seekbar_progress);
        seekbarVolume = videoLayout.findViewById(R.id.seekbar_volume);
        btnController = videoLayout.findViewById(R.id.btn_controller);
        btnScreen = videoLayout.findViewById(R.id.btn_screen);
        tvCurrentProgress = videoLayout.findViewById(R.id.tv_currentProgress);
        tvTotalProgress = videoLayout.findViewById(R.id.tv_totalProgress);
        ivVolume = videoLayout.findViewById(R.id.iv_volume);
        llyController = videoLayout.findViewById(R.id.lly_controller);
        rlContainer = videoLayout.findViewById(R.id.rl_container);
    }

    private void initData() {
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        seekbarVolume.setProgress(currentVolume);
    }

    private void initListener() {

        btnScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVerticalScreen) {
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });

        btnController.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    btnController.setImageResource(R.drawable.dk_btn_play_style);
                    videoView.pause();
                    mHandler.removeMessages(UPDATE_PROGRESS);
                } else {
                    btnController.setImageResource(R.drawable.dk_btn_pause_style);
                    videoView.start();
                    mHandler.sendEmptyMessage(UPDATE_PROGRESS);
                    if (state == 0) state = 1;
                }
            }
        });

        videoView.setStateListener(new CustomVideoView.StateListener() {

            @Override
            public void changeVolumn(float detlaY) {

                if (flVolume.getVisibility() == View.GONE) {
                    flVolume.setVisibility(View.VISIBLE);
                }
                int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int index = (int) (detlaY / screenHeight * maxVolume * 3);
                int volume = Math.max(0, currentVolume - index);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
                seekbarVolume.setProgress(volume);
            }

            @Override
            public void changeBrightness(float detlaY) {

                if (flLight.getVisibility() == View.GONE) {
                    flLight.setVisibility(View.VISIBLE);
                }
                WindowManager.LayoutParams wml = mActivity.getWindow().getAttributes();
                float screenBrightness = wml.screenBrightness;
                float index = -detlaY / screenHeight / 5;
                screenBrightness += index;
                if (screenBrightness > 1.0f) {
                    screenBrightness = 1.0f;
                } else if (screenBrightness < 0.01f) {
                    screenBrightness = 0.01f;
                }
                wml.screenBrightness = screenBrightness;
                mActivity.getWindow().setAttributes(wml);
            }

            @Override
            public void hideHint() {
                if (flLight.getVisibility() == View.VISIBLE) {
                    flLight.setVisibility(GONE);
                }

                if (flVolume.getVisibility() == View.VISIBLE) {
                    flVolume.setVisibility(GONE);
                }
            }
        });

        seekbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekbarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTextViewFormat(tvCurrentProgress, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 暂停刷新
                mHandler.removeMessages(UPDATE_PROGRESS);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (state != 0) {
                    mHandler.sendEmptyMessage(UPDATE_PROGRESS);
                }
                videoView.seekTo(seekBar.getProgress());
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //竖屏
            isVerticalScreen = true;
            ivVolume.setVisibility(View.GONE);
            seekbarVolume.setVisibility(View.GONE);
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dp2px(290));
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            isVerticalScreen = false;
            ivVolume.setVisibility(View.VISIBLE);
            seekbarVolume.setVisibility(View.VISIBLE);
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // 移除半屏状态
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void setVideoViewScale(int width, int height) {

        ViewGroup.LayoutParams videoViewLayoutParams = videoView.getLayoutParams();
        videoViewLayoutParams.width = width;
        videoViewLayoutParams.height = height;
        videoView.setLayoutParams(videoViewLayoutParams);

        RelativeLayout.LayoutParams rlContainerLayoutParams = (RelativeLayout.LayoutParams) rlContainer.getLayoutParams();
        rlContainerLayoutParams.width = width;
        rlContainerLayoutParams.height = height;
        rlContainer.setLayoutParams(rlContainerLayoutParams);
    }

    private void updateTextViewFormat(TextView tv, int m) {

        String result;
        // 毫秒转成秒
        int second = m / 1000;
        int hour = second / 3600;
        int minute = second % 3600 / 60;
        int ss = second % 60;

        if (hour != 0) {
            result = String.format("%02d:%02d:%02d", hour, minute, ss);
        } else {
            result = String.format("%02d:%02d", minute, ss);
        }
        tv.setText(result);
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == UPDATE_PROGRESS) {

                // 获取当前时间
                int currentTime = videoView.getCurrentPosition();
                // 获取总时间
                int totalTime = videoView.getDuration() - 100;
                if (currentTime >= totalTime) {
                    videoView.pause();
                    videoView.seekTo(0);
                    seekbarProgress.setProgress(0);
                    btnController.setImageResource(R.drawable.dk_btn_play_style);
                    updateTextViewFormat(tvCurrentProgress, 0);
                    mHandler.removeMessages(UPDATE_PROGRESS);
                } else {
                    seekbarProgress.setMax(totalTime);
                    seekbarProgress.setProgress(currentTime);
                    updateTextViewFormat(tvCurrentProgress, currentTime);
                    updateTextViewFormat(tvTotalProgress, totalTime);
                    mHandler.sendEmptyMessageDelayed(UPDATE_PROGRESS, 100);
                }
            }
        }
    };

    public void onPause() {
        videoPos = videoView.getCurrentPosition();
        videoView.stopPlayback();
        mHandler.removeMessages(UPDATE_PROGRESS);
    }

    public void onResume() {
        videoView.seekTo(videoPos);
        videoView.resume();
    }

    public void setProgressBg(Drawable drawable) {
        seekbarProgress.setProgressDrawable(drawable);
    }

    public void setVolumeBg(Drawable drawable) {
        seekbarVolume.setProgressDrawable(drawable);
    }
}