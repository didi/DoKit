package com.didichuxing.doraemonkit.kit.performance;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.didichuxing.doraemonkit.R;


public class PolyLineItemView extends View {

    private static float pointBottomY;
    private static float pointTopY = 50;
    private static Paint mGradientPaint;


    private final int GRAPH_STROKE_WIDTH = 2;
    private final float SMALL_RADIUS = 10;
    private final float BIG_RADIUS = 20;
    private final float CIRCLE_STROKE_WIDTH = 2;


    private float maxValue;
    private float minValue;
    private float currentValue;
    private String label;
    private float lastValue;
    private float nextValue;
    private Paint mPaint = new Paint();
    private float viewHeight;
    private float viewWidth;

    private float pointX;
    private float pointY;
    private float pointSize = SMALL_RADIUS;


    private boolean drawLeftLine = true;
    private boolean drawRightLine = true;
    private boolean onTouch = false;
    private boolean touchable = true;
    private boolean showLabel;

    private boolean drawDiver;

    public PolyLineItemView(Context context) {
        super(context);
    }

    public PolyLineItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PolyLineItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setCurrentValue(float currentValue) {
        if (currentValue > maxValue) {
            currentValue = (int) maxValue;
        }
        if (currentValue < minValue) {
            currentValue = (int) minValue;
        }
        this.currentValue = currentValue;
        invalidate();
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = getMeasuredHeight();
        viewWidth = getMeasuredWidth();
        pointX = viewWidth / 2;
        if (pointBottomY == 0) {
            pointBottomY = viewHeight - pointSize;
        }
        pointY = (1 - (currentValue / (maxValue - minValue))) * (pointBottomY - pointTopY) + pointTopY;

        if (mGradientPaint == null) {
            mGradientPaint = new Paint();
            mGradientPaint.setShader(new LinearGradient(0, 0, viewWidth, viewHeight, getResources().getColor(R.color.dk_color_3300BFFF),
                    getResources().getColor(R.color.dk_color_33434352), Shader.TileMode.CLAMP));
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawGraph(canvas);
        drawPoint(canvas);
        drawValue(canvas);
        drawLine(canvas);
    }


    private void drawValue(Canvas canvas) {
        if (onTouch || showLabel) {
            mPaint.setTextSize(20);
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(0);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float baseLine = pointY - fontMetrics.bottom * 4;
            canvas.drawText(label, viewWidth / 2, baseLine, mPaint);

        }
    }

    public void setlastValue(float lastValue) {
        if (lastValue > maxValue) {
            lastValue = (int) maxValue;
        }
        if (lastValue < minValue) {
            lastValue = (int) minValue;
        }
        this.lastValue = lastValue;
    }

    public void setNextValue(float nextValue) {
        if (nextValue > maxValue) {
            nextValue = (int) maxValue;
        }
        if (nextValue < minValue) {
            nextValue = (int) minValue;
        }
        this.nextValue = nextValue;
    }


    private void drawGraph(Canvas canvas) {

        mPaint.setPathEffect(null);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.dk_color_4c00C9F4));
        mPaint.setStrokeWidth(GRAPH_STROKE_WIDTH);
        mPaint.setAntiAlias(true);
        if (drawLeftLine) {
            float middleValue = currentValue - (currentValue - lastValue) / 2f;
            float middleY = ((pointBottomY - pointTopY) * 1f / (maxValue - minValue) * (maxValue - middleValue + minValue) + pointTopY);
            canvas.drawLine(0, middleY, pointX, pointY, mPaint);
            drawGradient(canvas, middleY, false);
        }
        if (drawRightLine) {
            float middleValue = currentValue - (currentValue - nextValue) / 2f;
            float middleY = ((pointBottomY - pointTopY) * 1f / (maxValue - minValue) * (maxValue - middleValue + minValue) + pointTopY);
            canvas.drawLine(pointX, pointY, viewWidth, middleY, mPaint);
            drawGradient(canvas, middleY, true);
        }
    }

    private void drawGradient(Canvas canvas, float middleY, boolean isRight) {
        Path path = new Path();
        if (!isRight) {
            path.moveTo(0, middleY);
            path.lineTo(pointX, pointY);
            path.lineTo(pointX, pointBottomY);
            path.lineTo(0, pointBottomY);
        } else {
            path.moveTo(pointX, pointY);
            path.lineTo(pointX, pointBottomY);
            path.lineTo(pointX + viewWidth / 2, pointBottomY);
            path.lineTo(pointX + viewWidth / 2, middleY);
        }
        canvas.drawPath(path, mGradientPaint);

    }


    private void drawPoint(Canvas canvas) {
        int color;
        if (onTouch) {
            color = getResources().getColor(R.color.dk_color_4c00C9F4);
            mPaint.setColor(color);
            mPaint.setPathEffect(null);
            mPaint.setStrokeWidth(CIRCLE_STROKE_WIDTH);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(pointX, pointY, BIG_RADIUS, mPaint);
        }
        color = getResources().getColor(R.color.dk_color_ff00C9F4);
        mPaint.setColor(color);
        mPaint.setStrokeWidth(CIRCLE_STROKE_WIDTH);
        canvas.drawCircle(pointX, pointY, pointSize, mPaint);
    }

    private void drawLine(Canvas canvas) {
        if (!drawDiver) {
            return;
        }
        mPaint.setColor((getResources().getColor(R.color.dk_color_999999)));
        mPaint.setPathEffect(null);

        mPaint.setStrokeWidth(GRAPH_STROKE_WIDTH);
        mPaint.setStyle(Paint.Style.FILL);
        if (drawLeftLine) {
            canvas.drawLine(0, pointBottomY, viewWidth / 2, pointBottomY, mPaint);
        }
        if (drawRightLine) {
            canvas.drawLine(viewWidth / 2, pointBottomY, viewWidth, pointBottomY, mPaint);

        }
    }

    public void setDrawLeftLine(boolean drawLeftLine) {
        this.drawLeftLine = drawLeftLine;
    }

    public void setDrawRightLine(boolean drawRightLine) {
        this.drawRightLine = drawRightLine;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!touchable) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouch = true;
                setBackgroundResource(R.drawable.dk_line_chart_selected_background);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onTouch = false;
                setBackgroundResource(0);
                break;
        }
        return super.onTouchEvent(event);
    }

    public void setDrawDiver(boolean drawDiver) {
        this.drawDiver = drawDiver;
    }

    public void setPointSize(float pointSize) {
        if (pointSize != 0) {
            this.pointSize = pointSize;
        }
    }

    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    public void showLabel(boolean showLatestLabel) {
        this.showLabel = showLatestLabel;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}