package com.didichuxing.doraemonkit.kit.performance.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.text.TextUtils;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.util.UIUtils;

/**
 * @desc: 折线图渲染器
 */
public class LineRender {

    // 顶部预留50sp,防止文字绘制出界
    private int mPaddingTop = 50;
    // 底部预留2dp,防止圆点绘制补全
    private int mPaddingBottom;

    private final int GRAPH_STROKE_WIDTH = 2;
    private final float SMALL_RADIUS = 10;
    private final float CIRCLE_STROKE_WIDTH = 2;

    private Context mContext;

    private float maxValue;//最高值
    private float minValue;//最低值
    private String label;//显示值
    private float nextValue;//下一个值
    private Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mGradientPaint = new Paint();
    private Paint mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float viewHeight;
    private float viewWidth;

    private float pointX;//所有点的x坐标
    private float pointY;//当前点的Y
    private float pointSize = SMALL_RADIUS;// 圆点大小


    private boolean drawRightLine = true;//是否画右边的线
    private boolean showLabel;
    private float labelAlpha;
    private float startPosition;
    private float baseLine = 20;

    private Path mGradientPath = new Path();

    public LineRender(Context context) {
        mContext = context;
        mPaddingBottom = UIUtils.dp2px(2);
    }


    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    protected void measure(float width, float height) {
        viewHeight = height - mPaddingBottom - mPaddingTop;
        viewWidth = width;

        initPaint();
    }

    private void initPaint() {
        mGradientPaint.setShader(new LinearGradient(0, 0, viewWidth, viewHeight, mContext.getResources().getColor(R.color.dk_color_3300BFFF),
                mContext.getResources().getColor(R.color.dk_color_33434352), Shader.TileMode.CLAMP));

        mLabelPaint.setTextSize(mContext.getResources().getDimensionPixelSize(R.dimen.dk_font_size_10));
        mLabelPaint.setColor(Color.WHITE);
        mLabelPaint.setTextAlign(Paint.Align.CENTER);

        mLinePaint.setPathEffect(null);

        mLinePaint.setStyle(Paint.Style.FILL);
        mLinePaint.setColor(mContext.getResources().getColor(R.color.dk_color_4c00C9F4));
        mLinePaint.setStrokeWidth(GRAPH_STROKE_WIDTH);
        mLinePaint.setAntiAlias(true);

        int color = mContext.getResources().getColor(R.color.dk_color_ff00C9F4);
        mPointPaint.setColor(color);
        mPointPaint.setStrokeWidth(CIRCLE_STROKE_WIDTH);
    }


    public void draw(Canvas canvas) {
        drawGraph(canvas);
        drawGradient(canvas);
        drawPoint(canvas);
        drawLabel(canvas);
    }


    /**
     * 画数字
     *
     * @param canvas
     */
    private void drawLabel(Canvas canvas) {
        if (showLabel && !TextUtils.isEmpty(label)) {
            mLabelPaint.setAlpha((int) (labelAlpha * 0xff));
            canvas.drawText(label, startPosition, pointY - baseLine, mLabelPaint);
        }
    }

    public void setNextValue(float nextValue) {
        if (nextValue > maxValue) {
            nextValue = (int) maxValue;
        }
        if (nextValue < minValue) {
            nextValue = (int) minValue;
        }
        this.nextValue = (1 - nextValue / (maxValue - minValue)) * viewHeight + mPaddingTop;
    }

    public void setCurrentValue(int index, float currentValue) {
        if (currentValue > maxValue) {
            currentValue = (int) maxValue;
        }
        if (currentValue < minValue) {
            currentValue = (int) minValue;
        }
        startPosition = index * viewWidth;
        pointX = startPosition;
        pointY = (1 - currentValue / (maxValue - minValue)) * viewHeight + mPaddingTop;
    }


    /**
     * 画折线
     *
     * @param canvas
     */

    private void drawGraph(Canvas canvas) {
        if (drawRightLine) {
            float middleY = nextValue;
            canvas.drawLine(startPosition, pointY, viewWidth + startPosition, middleY, mLinePaint);
        }
    }

    private void drawGradient(Canvas canvas) {
        if (drawRightLine) {
            mGradientPath.rewind();
            mGradientPath.moveTo(pointX, pointY);
            mGradientPath.lineTo(pointX, viewHeight + mPaddingTop);
            mGradientPath.lineTo(pointX + viewWidth, viewHeight + mPaddingTop);
            mGradientPath.lineTo(pointX + viewWidth, nextValue);
            canvas.drawPath(mGradientPath, mGradientPaint);
        }

    }

    /**
     * 画数字下面的小圆圈
     *
     * @param canvas
     */
    private void drawPoint(Canvas canvas) {
        canvas.drawCircle(pointX, pointY, pointSize, mPointPaint);
    }


    public void setDrawRightLine(boolean drawRightLine) {
        this.drawRightLine = drawRightLine;
    }


    public void setPointSize(float pointSize) {
        if (pointSize != 0) {
            this.pointSize = pointSize;
        }
    }


    public void setLabel(String label) {
        this.label = label;
    }

    public void setShowLabel(boolean show) {
        showLabel = show;
    }

    public void setLabelAlpha(float alpha) {
        labelAlpha = alpha;
    }
}