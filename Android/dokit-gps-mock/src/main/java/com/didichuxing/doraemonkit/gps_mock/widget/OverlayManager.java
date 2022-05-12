package com.didichuxing.doraemonkit.gps_mock.widget;

import static com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnPolylineClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.didichuxing.doraemonkit.gps_mock.R;
import com.didichuxing.doraemonkit.util.ConvertUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 该类提供一个能够显示和管理多个Overlay的基类
 * <p>
 * 复写{@link #getOverlayOptions()} 设置欲显示和管理的Overlay列表
 * </p>
 * <p>
 * 通过
 * {@link BaiduMap#setOnMarkerClickListener(OnMarkerClickListener)}
 * 将覆盖物点击事件传递给OverlayManager后，OverlayManager才能响应点击事件。
 * <p>
 * 复写{@link #onMarkerClick(Marker)} 处理Marker点击事件
 * </p>
 */
public abstract class OverlayManager implements OnMarkerClickListener, OnPolylineClickListener {

    BaiduMap mBaiduMap = null;
    private List<OverlayOptions> mOverlayOptionList = null;

    List<Overlay> mOverlayList = null;

    // 漂移路线
    private Overlay mDriftRouteOverlay;
    private Overlay mDriftRandomOverlay;
    // 实时坐标
    private Overlay mLocMarkOverlay;
    private Bitmap mLocMarkBitmap;
    // 漂移范围的指示圆
    private Overlay mLocRangeCircleOverlay;

    public static final int COLOR_ROUTE_DRIFT = 0x80FF0000;
    public static final int COLOR_ROUTE = 0xBF004EFF;

    /**
     * 通过一个BaiduMap 对象构造
     *
     * @param baiduMap
     */
    public OverlayManager(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;
        // mBaiduMap.setOnMarkerClickListener(this);
        if (mOverlayOptionList == null) {
            mOverlayOptionList = new ArrayList<OverlayOptions>();
        }
        if (mOverlayList == null) {
            mOverlayList = new ArrayList<Overlay>();
        }
    }

    /**
     * 覆写此方法设置要管理的Overlay列表
     *
     * @return 管理的Overlay列表
     */
    public abstract List<OverlayOptions> getOverlayOptions();

    public abstract OverlayOptions getPolylineOptions(List<LatLng> points, int lineColor);

    /**
     * 将所有Overlay 添加到地图上
     */
    public final void addToMap() {
        if (mBaiduMap == null) {
            return;
        }

        removeFromMap();
        List<OverlayOptions> overlayOptions = getOverlayOptions();
        if (overlayOptions != null) {
            mOverlayOptionList.addAll(overlayOptions);
        }

        for (OverlayOptions option : mOverlayOptionList) {
            mOverlayList.add(mBaiduMap.addOverlay(option));
        }
    }


    public final void addDriftRouteToMap(List<LatLng> points, int lineColor) {
        if (points == null || points.size() <= 0) return;

        if (mBaiduMap == null) {
            return;
        }

        if (mDriftRandomOverlay != null) {
            mDriftRandomOverlay.remove();
        }

        if (mDriftRouteOverlay != null) {
            mDriftRouteOverlay.remove();
        }

        if (mDriftRandomDotOverlay.size() > 0) {
            for (Overlay overlay : mDriftRandomDotOverlay) {
                overlay.remove();
            }
        }

        OverlayOptions driftOverlayOption = getPolylineOptions(points, lineColor);
        mDriftRouteOverlay = mBaiduMap.addOverlay(driftOverlayOption);
    }

    private final List<Overlay> mDriftRandomDotOverlay = new ArrayList<>();

    public final void addDriftRandomDotToMap(List<LatLng> points, int color) {
        if (points == null || points.size() <= 0) return;

        if (mBaiduMap == null) {
            return;
        }
        if (mDriftRouteOverlay != null) {
            mDriftRouteOverlay.remove();
        }

        if (mDriftRandomDotOverlay.size() > 0) {
            for (Overlay overlay : mDriftRandomDotOverlay) {
                overlay.remove();
            }
        }

        for (LatLng latLng : points) {
            mDriftRandomDotOverlay.add(addPointMark(latLng, 8, color));
        }
    }

    public final void addDriftRandomRouteToMap(List<LatLng> points, int lineColor) {
        if (points == null || points.size() <= 0) return;

        if (mBaiduMap == null) {
            return;
        }
        if (mDriftRouteOverlay != null) {
            mDriftRouteOverlay.remove();
        }

        if (mDriftRandomOverlay != null) {
            mDriftRandomOverlay.remove();
        }

        OverlayOptions driftOverlayOption = getPolylineOptions(points, lineColor);
        mDriftRandomOverlay = mBaiduMap.addOverlay(driftOverlayOption);
    }

    private Bitmap getBitmap(int vectorDrawableId) {
        Bitmap bitmap;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable vectorDrawable = BMapManager.getContext().getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(BMapManager.getContext().getResources(), vectorDrawableId);
        }

        return bitmap;
    }


    public final void addLocMark(LatLng latLng) {
        if (mLocMarkOverlay != null) {
            mLocMarkOverlay.remove();
        }
        if (mLocMarkBitmap == null || mLocMarkBitmap.isRecycled()) {
            mLocMarkBitmap = getBitmap(R.drawable.dk_icon_loc_circle_shape);
        }
        mLocMarkOverlay = mBaiduMap.addOverlay(new MarkerOptions()
            .position(latLng)
            .icon(
                BitmapDescriptorFactory
                    //                    .fromResource(R.mipmap.dk_icon_cur_loc))
                    .fromBitmap(mLocMarkBitmap))
            .yOffset(ConvertUtils.dp2px(18))
            .animateType(MarkerOptions.MarkerAnimateType.grow)
            .zIndex(10));
    }


    public final void addCircleOptions(LatLng latLng, int radius) {
        if (mLocRangeCircleOverlay != null) {
            mLocRangeCircleOverlay.remove();
        }

        mLocRangeCircleOverlay = mBaiduMap.addOverlay(new CircleOptions()
            .center(latLng)
            .radius(radius) // 单位:米
            .fillColor(0xAAFFFF00)
            .stroke(new Stroke(2, 0xAA00FF00)));
    }

    public final Overlay addPointMark(LatLng latLng, int radiusPx, int color) {
        return mBaiduMap.addOverlay(new DotOptions()
            .center(latLng)
            .radius(radiusPx) // 单位:像素
            .color(color));
    }

    /**
     * 将所有Overlay 从 地图上消除
     */
    public final void removeFromMap() {
        if (mBaiduMap == null) {
            return;
        }
        for (Overlay marker : mOverlayList) {
            marker.remove();
        }
        mOverlayOptionList.clear();
        mOverlayList.clear();

    }

    /**
     * 缩放地图，使所有Overlay都在合适的视野内
     * <p>
     * 注： 该方法只对Marker类型的overlay有效
     * </p>
     */
    public void zoomToSpan() {
        if (mBaiduMap == null) {
            return;
        }
        if (mOverlayList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Overlay overlay : mOverlayList) {
                // polyline 中的点可能太多，只按marker 缩放
                if (overlay instanceof Marker) {
                    builder.include(((Marker) overlay).getPosition());
                }
            }
            MapStatus mapStatus = mBaiduMap.getMapStatus();
            if (null != mapStatus) {
                int width = mapStatus.winRound.right - mBaiduMap.getMapStatus().winRound.left - 400;
                int height = mapStatus.winRound.bottom - mBaiduMap.getMapStatus().winRound.top - 400;
                mBaiduMap.setMapStatus(MapStatusUpdateFactory
                    .newLatLngBounds(builder.build(), width, height));
            }

        }
    }

    /**
     * 设置显示在规定宽高中的地图地理范围
     */
    public void zoomToSpanPaddingBounds(int paddingLeft, int paddingTop, int paddingRight, int paddingBottom) {
        if (mBaiduMap == null) {
            return;
        }
        if (mOverlayList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (Overlay overlay : mOverlayList) {
                // polyline 中的点可能太多，只按marker 缩放
                if (overlay instanceof Marker) {
                    builder.include(((Marker) overlay).getPosition());
                }
            }

            mBaiduMap.setMapStatus(MapStatusUpdateFactory
                .newLatLngBounds(builder.build(), paddingLeft, paddingTop, paddingRight, paddingBottom));
        }
    }

}
