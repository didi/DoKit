package com.didichuxing.doraemonkit.kit.colorpick;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.kit.colorpick.ColorPickConstants;
import com.didichuxing.doraemonkit.util.ColorUtil;

/**
 * Created by wanglikun on 2018/12/1.
 */

public class ColorPickerView extends View {
    private Paint mRingPaint;
    private Paint mBitmapPaint;
    private Paint mFocusPaint;
    private Paint mGridPaint;
    private Paint mGridShadowPaint;
    private TextPaint mTextPaint;
    private Path mClipPath = new Path();
    private Matrix mBitmapMatrix = new Matrix();
    private Rect mGridRect = new Rect();
    private Bitmap mCircleBitmap;
    private String mText;

    public ColorPickerView(Context context) {
        super(context);
        init();
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setColor(Color.WHITE);
        mRingPaint.setStyle(Paint.Style.STROKE);

        mFocusPaint = new Paint();
        mFocusPaint.setAntiAlias(true);
        mFocusPaint.setStyle(Paint.Style.STROKE);
        mFocusPaint.setStrokeWidth(3f);
        mFocusPaint.setColor(Color.BLACK);

        mBitmapPaint = new Paint();
        mBitmapPaint.setFilterBitmap(false);

        mGridPaint = new Paint();
        //设置线宽。单位为1像素
        mGridPaint.setStrokeWidth(1f);
        mGridPaint.setStyle(Paint.Style.STROKE);
        //画笔颜色
        mGridPaint.setColor(-3355444);
        mGridShadowPaint = new Paint(mGridPaint);
        mGridShadowPaint.setColor(-12303292);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.MONOSPACE);
        mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.dk_font_size_12));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mClipPath.rewind();
        mClipPath.moveTo(0, 0);
        mClipPath.addCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBitmap(canvas);
        drawGrid(canvas);
        drawRing(canvas);
        drawText(canvas);
        drawFocus(canvas);
    }

    private void drawText(Canvas canvas) {
        if (!TextUtils.isEmpty(mText)) {
            float ringWidth = ColorPickConstants.PIX_INTERVAL * 2 + 4;
            float hOffset = (float) (getWidth() * Math.PI * (90 * 1.0 / 360));
            float vOffset = ringWidth - 5;
            canvas.drawTextOnPath(mText, mClipPath, hOffset, vOffset, mTextPaint);
            canvas.setDrawFilter(null);
        }
    }

    private RoundedBitmapDrawable mGridDrawable;

    private void drawGrid(Canvas canvas) {
        if (mGridDrawable == null) {
            Bitmap gridBitmap = createGridBitmap(ColorPickConstants.PIX_INTERVAL, canvas);
            mGridDrawable = RoundedBitmapDrawableFactory.create(getResources(), gridBitmap);
            mGridDrawable.setBounds(0, 0, getRight(), getBottom());
            mGridDrawable.setCircular(true);
        }
        mGridDrawable.draw(canvas);
    }


    private Bitmap createGridBitmap(int pixInterval, Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        canvas.getClipBounds(mGridRect);

        Bitmap gridBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas gridCanvas = new Canvas(gridBitmap);

        if (pixInterval >= 4) {
            int alpha = Math.min(pixInterval * 36, 255);
            mGridPaint.setAlpha(alpha);
            mGridShadowPaint.setAlpha(alpha);
            float value;
            float start;
            float end;
            gridCanvas.save();
            for (int i = 0; i <= getWidth(); i += pixInterval) {
                value = (float) (i - 1);
                start = 0f;
                end = (float) height;
                gridCanvas.drawLine(value, start, value, end, this.mGridPaint);
                value = (float) i;
                gridCanvas.drawLine(value, start, value, end, this.mGridShadowPaint);
            }
            for (int i = 0; i <= getHeight(); i += pixInterval) {
                value = (float) (i - 1);
                start = 0f;
                end = (float) width;
                gridCanvas.drawLine(start, value, end, value, this.mGridPaint);
                value = (float) i;
                gridCanvas.drawLine(start, value, end, value, this.mGridShadowPaint);
            }
            gridCanvas.restore();
        }
        return gridBitmap;
    }

    private void drawFocus(Canvas canvas) {
        float focusWidth = ColorPickConstants.PIX_INTERVAL + 4;
        canvas.drawRect(getWidth() / 2 - 2,
                getWidth() / 2 - 2,
                getWidth() / 2 + focusWidth - 2,
                getWidth() / 2 + focusWidth - 2,
                mFocusPaint);
    }

    private void drawRing(Canvas canvas) {
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        float ringWidth = ColorPickConstants.PIX_INTERVAL * 2 + 4;

        mRingPaint.setStrokeWidth(ringWidth);
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - ringWidth / 2, mRingPaint);

        mRingPaint.setColor(getResources().getColor(R.color.dk_color_333333));
        mRingPaint.setStrokeWidth(0.5f);
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2, mRingPaint);
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - ringWidth, mRingPaint);
    }

    private void drawBitmap(Canvas canvas) {
        if (mCircleBitmap == null || mCircleBitmap.isRecycled()) {
            return;
        }
        canvas.save();
        canvas.clipPath(mClipPath);
        mBitmapMatrix.reset();
        mBitmapMatrix.postScale(getWidth() / (float) mCircleBitmap.getWidth(), getHeight() / (float) mCircleBitmap.getHeight());
        canvas.drawBitmap(mCircleBitmap, mBitmapMatrix, mBitmapPaint);
        canvas.restore();
    }

    public void setBitmap(Bitmap bitmap, int color, int x, int y) {
        mCircleBitmap = bitmap;
        mText = String.format(ColorPickConstants.TEXT_FOCUS_INFO, ColorUtil.parseColorInt(color), x + ColorPickConstants.PIX_INTERVAL, y + ColorPickConstants.PIX_INTERVAL);
        mRingPaint.setColor(color);
        if (ColorUtil.isColdColor(color)) {
            mFocusPaint.setColor(Color.WHITE);
            mTextPaint.setColor(Color.WHITE);
        } else {
            mFocusPaint.setColor(Color.BLACK);
            mTextPaint.setColor(Color.BLACK);
        }
        invalidate();
    }
}