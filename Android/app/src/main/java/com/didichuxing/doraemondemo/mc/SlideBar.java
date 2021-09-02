package com.didichuxing.doraemondemo.mc;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.didichuxing.doraemondemo.R;


/**
 * 拖动解锁按钮
 *
 * @author jint
 */
public class SlideBar extends FrameLayout {

    private View backgroundView;
    private View floatView;
    private View floatBackground;
    private TextView mText = null;

    private int thumbWidth = 0;
    private boolean sliding = false;
    private int sliderPosition = 0;
    private int initialSliderPosition = 0;
    private float initialSlidingX = 0;

    private static final int TEXT_ALIGN_CENTER = 0;
    private static final int TEXT_ALIGN_CENTER_EXCEPT_THUMB = 1;

    private int textAlign = TEXT_ALIGN_CENTER;

    private OnUnlockListener listener = null;

    public void setOnUnlockListener(OnUnlockListener listener) {
        this.listener = listener;
    }

    public SlideBar(Context context) {
        this(context, null);
    }

    public SlideBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_slidebar, this, true);

        backgroundView = findViewById(R.id.slidebar_root_bg);
        mText = findViewById(R.id.slidebar_text_label);
        floatBackground = findViewById(R.id.slidebar_float_background);
        floatView = findViewById(R.id.slidebar_float_view);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.UnlockBar);
            String label = a.getString(R.styleable.UnlockBar_label);
            mText.setText(label);
            textAlign = a.getInt(R.styleable.UnlockBar_textAlign, TEXT_ALIGN_CENTER);
            a.recycle();
        } else {
            textAlign = TEXT_ALIGN_CENTER_EXCEPT_THUMB;
        }
//        if (textAlign == TEXT_ALIGN_CENTER_EXCEPT_THUMB) {
//            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mText.getLayoutParams();
//            params.setMargins(context.getResources().getDimensionPixelSize(R.dimen.slidebar_thumb_width) / 2, 0, 0, 0);
//            mText.setLayoutParams(params);
//        }

        LayoutParams params = (LayoutParams) floatView.getLayoutParams();
        ViewGroup p = (ViewGroup) floatBackground.getParent();
        thumbWidth = p.getPaddingLeft() + p.getPaddingRight()
                + params.leftMargin + params.rightMargin
                + context.getResources().getDimensionPixelSize(R.dimen.slidebar_thumb_width);
    }

    public void reset() {
        sliderPosition = 0;

        final LayoutParams bgParams = (LayoutParams) floatBackground.getLayoutParams();
        ValueAnimator animator = ValueAnimator.ofInt(bgParams.leftMargin, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                setMarginLeftExtra(value);
                if (value <= 1 && !isPressed()) {
                    mText.setAlpha(1f);
                }
                floatBackground.requestLayout();
            }
        });
        animator.setDuration(300);
        animator.start();
    }

    /**
     * 设置按钮文字
     */
    public void setLabel(CharSequence s) {
        mText.setText(s);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (!mDragAble) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getX() > sliderPosition && event.getX() < (sliderPosition + thumbWidth)) {
                sliding = true;
                initialSlidingX = event.getX();
                initialSliderPosition = sliderPosition;
                mText.setAlpha(0);
                setPressed(true);
            }
            event.setAction(MotionEvent.ACTION_DOWN);
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            if (sliderPosition >= (getMeasuredWidth() - thumbWidth) * 0.7) {
                //到70%就行
                if (listener != null) listener.onUnlock(this);
            } else if (sliding) {
                reset();
            }
            sliding = false;
            setPressed(false);
            event.setAction(MotionEvent.ACTION_UP);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE && sliding) {
            sliderPosition = (int) (initialSliderPosition + (event.getX() - initialSlidingX));
            if (sliderPosition <= 0) sliderPosition = 0;

            if (sliderPosition >= (getMeasuredWidth() - thumbWidth)) {
                sliderPosition = getMeasuredWidth() - thumbWidth;
            } else {
//                int max = getMeasuredWidth() - thumbWidth;
//                int progress = (int) (sliderPosition * 100 / (max * 1.0f));
                mText.setAlpha(0);
            }
            setMarginLeftExtra(sliderPosition);
            event.setAction(MotionEvent.ACTION_MOVE);
        }
        return true;
    }

    public void setMarginLeftExtra(int leftMargin) {
        if (floatBackground == null) return;
        if (listener != null) listener.progress(this, leftMargin);
        LayoutParams params = (LayoutParams) floatBackground.getLayoutParams();
        params.setMargins(leftMargin, params.topMargin, params.rightMargin, params.bottomMargin);
        floatBackground.setLayoutParams(params);
    }

    public interface OnUnlockListener {
        void onUnlock(View view);

        void progress(View view, int leftMargin);
    }


    /**
     * 设置后台背景
     *
     * @param color
     */
    public void setBackgroundColor(int color) {
        if (backgroundView != null) {
            backgroundView.setBackgroundColor(color);
        }
    }

    /**
     * 设置前台背景
     *
     * @param color
     */
    public void setForegroundColor(int color) {
        if (floatBackground != null) {
            floatBackground.setBackgroundColor(color);
        }
    }

    /**
     * 设置圆角
     *
     * @param radius
     */
    public void setBorderRadius(float radius) {
        // TODO: 在这里添加设置圆角代码
    }

    /**
     * 设置是否可以拖动
     */
    private boolean mDragAble = true;

    public void setDragAble(boolean able) {
        if (able) {
            floatView.setVisibility(View.VISIBLE);
            mDragAble = true;
        } else {
            floatView.setVisibility(View.GONE);
            mDragAble = false;
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
