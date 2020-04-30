package com.didichuxing.doraemonkit.widget.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.util.UIUtils;

import java.util.List;

/**
 * @desc: 流量监控圆环view, 使用drawPath方式实现了等间空隙. 调用方式：{@link PieChart#setData(List)}}
 */
public class PieChart extends View {
    private final static float DEFAULT_RING_WIDTH = 19.5f;
    private final static float DEFAULT_SLICE_SPACE = 2;
    private Paint mRenderPaint;
    protected Paint mTransparentCirclePaint;

    /**
     * View自身的宽和高
     */
    private int mHeight;
    private int mWidth;

    /**
     * 圆环宽度
     */
    private float mRingWidth;
    private float mSliceSpace;
    private List<PieData> mPieData;
    private Path mPath = new Path();

    private float FDEG2RAD = ((float) Math.PI / 180.f);
    private double DEG2RAD = (Math.PI / 180.0);

    public PieChart(Context context) {
        super(context);
        setRingWidth(DEFAULT_RING_WIDTH);
        setSliceSpace(DEFAULT_SLICE_SPACE);
        //初始化画笔
        initPaint();
    }

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setRingWidth(DEFAULT_RING_WIDTH);
        setSliceSpace(DEFAULT_SLICE_SPACE);
        //初始化画笔
        initPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPieData == null) {
            return;
        }
        //画圆形
        drawPie(canvas);
        //画镂空透明区域
        drawHolo(canvas);
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        mRenderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRenderPaint.setStyle(Paint.Style.FILL);

        mTransparentCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTransparentCirclePaint.setColor(Color.WHITE);
        mTransparentCirclePaint.setStyle(Paint.Style.FILL);
    }


    /**
     * 画圆环
     *
     * @param canvas
     */
    private void drawPie(Canvas canvas) {
        RectF rectF = new RectF(0, 0, mWidth, mHeight);
        Center center = new Center();
        center.x = mWidth / 2f;
        center.y = mHeight / 2f;
        float radius = mWidth / 2f;
        float sliceSpace = mSliceSpace;
        final float sliceSpaceAngleOuter = mPieData.size() == 1 ?
                0.f :
                sliceSpace / (FDEG2RAD * radius);
        float sliceAngle;
        float angle = 0;
        float startAngleOuter;
        float sweepAngleOuter;
        for (int i = 0; i < mPieData.size(); i++) {
            PieData data = mPieData.get(i);
            mRenderPaint.setColor(data.color);
            mPath.reset();
            sliceAngle = data.angel;
            startAngleOuter = (angle + sliceSpaceAngleOuter / 2.f) - 90;
            sweepAngleOuter = (sliceAngle - sliceSpaceAngleOuter);
            if (sweepAngleOuter < 0.f) {
                sweepAngleOuter = 0.f;
            }
            angle += sliceAngle;

            if (sweepAngleOuter >= 360) {
                mPath.addCircle(center.x, center.y, radius, Path.Direction.CW);
            } else {
                mPath.arcTo(rectF, startAngleOuter, sweepAngleOuter);
                float angleMiddle = startAngleOuter + sweepAngleOuter / 2.f;
                float arcStartPointX = center.x + radius * (float) Math.cos(startAngleOuter * FDEG2RAD);
                float arcStartPointY = center.y + radius * (float) Math.sin(startAngleOuter * FDEG2RAD);
                float sliceSpaceOffset =
                        calculateMinimumRadiusForSpacedSlice(
                                center,
                                radius,
                                sliceAngle,
                                arcStartPointX,
                                arcStartPointY,
                                startAngleOuter,
                                sweepAngleOuter);

                float arcEndPointX = center.x +
                        sliceSpaceOffset * (float) Math.cos(angleMiddle * FDEG2RAD);
                float arcEndPointY = center.y +
                        sliceSpaceOffset * (float) Math.sin(angleMiddle * FDEG2RAD);

                mPath.lineTo(
                        arcEndPointX,
                        arcEndPointY);
            }

            canvas.drawPath(mPath, mRenderPaint);
        }
    }


    private void drawHolo(Canvas canvas) {
        float holoRadius = mWidth / 2f - mRingWidth;
        canvas.drawCircle(mWidth / 2f, mHeight / 2f, holoRadius, mTransparentCirclePaint);
    }

    protected float calculateMinimumRadiusForSpacedSlice(
            Center center,
            float radius,
            float angle,
            float arcStartPointX,
            float arcStartPointY,
            float startAngle,
            float sweepAngle) {
        final float angleMiddle = startAngle + sweepAngle / 2.f;

        // Other point of the arc
        float arcEndPointX = center.x + radius * (float) Math.cos((startAngle + sweepAngle) * FDEG2RAD);
        float arcEndPointY = center.y + radius * (float) Math.sin((startAngle + sweepAngle) * FDEG2RAD);

        // Middle point on the arc
        float arcMidPointX = center.x + radius * (float) Math.cos(angleMiddle * FDEG2RAD);
        float arcMidPointY = center.y + radius * (float) Math.sin(angleMiddle * FDEG2RAD);

        // This is the base of the contained triangle
        double basePointsDistance = Math.sqrt(
                Math.pow(arcEndPointX - arcStartPointX, 2) +
                        Math.pow(arcEndPointY - arcStartPointY, 2));

        // After reducing space from both sides of the "slice",
        //   the angle of the contained triangle should stay the same.
        // So let's find out the height of that triangle.
        float containedTriangleHeight = (float) (basePointsDistance / 2.0 *
                Math.tan((180.0 - angle) / 2.0 * DEG2RAD));

        // Now we subtract that from the radius
        float spacedRadius = radius - containedTriangleHeight;

        // And now subtract the height of the arc that's between the triangle and the outer circle
        spacedRadius -= Math.sqrt(
                Math.pow(arcMidPointX - (arcEndPointX + arcStartPointX) / 2.f, 2) +
                        Math.pow(arcMidPointY - (arcEndPointY + arcStartPointY) / 2.f, 2));

        return spacedRadius;
    }


    /**
     * 设置圆环每个item间隔大小，单位dp
     *
     * @param space
     */
    public void setSliceSpace(float space) {
        mSliceSpace = UIUtils.dp2px(space);
    }

    /**
     * 设置圆环宽度，单位DP
     *
     * @param ringWidth
     */
    public void setRingWidth(float ringWidth) {
        mRingWidth = UIUtils.dp2px(ringWidth);
    }

    /**
     * 设置圆环数据
     *
     * @param pieData
     */
    public void setData(List<PieData> pieData) {
        mPieData = pieData;
        int weightSum = 0;
        for (PieData data :
                pieData) {
            weightSum += data.weight;
        }
        for (PieData data :
                pieData) {
            if (weightSum == 0) {
                data.angel = 360 / pieData.size();
            } else {
                data.angel = data.weight * 360f / weightSum;
            }
        }
    }

    public static class PieData {
        public final int color;
        /**
         * 比重，角度根据比重算出,angel=(weight/weightSum)*360
         */
        public final long weight;
        private float angel;

        public PieData(int color, long weight) {
            this.color = color;
            this.weight = weight;
        }
    }

    private class Center {
        public float x;
        public float y;
    }

}
