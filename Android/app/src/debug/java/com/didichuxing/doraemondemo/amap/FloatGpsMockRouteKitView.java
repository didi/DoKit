package com.didichuxing.doraemondemo.amap;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.didichuxing.doraemondemo.R;
import com.didichuxing.doraemondemo.dokit.SimpleDoKitView;
import com.didichuxing.doraemonkit.gps_mock.lbs.route.FloatGpsRouteMockCache;

public class FloatGpsMockRouteKitView extends SimpleDoKitView {
    public static final String TAG = "FloatGpsMockRoutKitView";
    private View mRootView;
    private static int sMockSpeed = 10;
    private TextView mMockSpeedTv;
    private SeekBar mSpeedSeekBar;
    private SeekBar routeSeekBar;
    private TextView mockRouteTv;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRouteUiRunnable = new Runnable() {
        @Override
        public void run() {
            routeSeekBar.setMax(FloatGpsRouteMockCache.getRouteCount());
            routeSeekBar.setProgress(FloatGpsRouteMockCache.getMockRouteProgress());
            mockRouteTv.setText("模拟路线进度:" + FloatGpsRouteMockCache.getMockRouteProgress() + " / " + FloatGpsRouteMockCache.getRouteCount());
            if (FloatGpsRouteMockCache.getRouteCount() == 0) {
                mockRouteTv.setText("使用 FloatGpsRouteMockCache.mockGpsRoute 设置模拟线路");
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.layout_mock_route;
    }


    @Override
    public void onViewCreated(FrameLayout rootView) {
        super.onViewCreated(rootView);
        mRootView = rootView;
        setMockLocationConfig();
    }

    private void setMockLocationConfig() {

        // 路线模拟工具
        FloatGpsRouteMockCache.setRouteChangeListener(new FloatGpsRouteMockCache.IOnRouteChange() {
            @Override
            public void onRouteChange() {
                updateRouteUI(0);
            }

            public void onIndexChange(int index) {
                Log.d(TAG, "⚠️ onIndexChange() called with: index = [" + index + "]");
                updateRouteUI(index);
            }
        });
        mockRouteTv = findViewById(R.id.tv_mock_route);
        routeSeekBar = findViewById(R.id.dk_sb_mock_route_seekBar);
        routeSeekBar.setMax(FloatGpsRouteMockCache.getRouteCount());
        routeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar routeSeekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateRoute(getContext().getApplicationContext(), progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar routeSeekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar routeSeekBar) {
            }
        });
        findViewById(R.id.btn_route_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRoute(v.getContext().getApplicationContext(), FloatGpsRouteMockCache.getMockRouteProgress() - 1);
            }
        });

        findViewById(R.id.btn_route_forward).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                updateRoute(v.getContext().getApplicationContext(), FloatGpsRouteMockCache.getMockRouteProgress() + 1);
            }
        });

        findViewById(R.id.btn_route_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatGpsRouteMockCache.pausePlayMockRoute();
            }
        });
        findViewById(R.id.btn_route_resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FloatGpsRouteMockCache.resumePlayMockRoute(getContext());
            }
        });
    }

    private void updateRoute(Context context, int index) {
        index = FloatGpsRouteMockCache.setMockRouteProgress(context, index);
        updateRouteUI(index);
    }

    private void updateRouteUI(int index) {
        handler.post(updateRouteUiRunnable);
    }

}
