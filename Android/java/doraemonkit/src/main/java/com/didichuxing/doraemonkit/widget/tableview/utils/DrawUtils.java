package com.didichuxing.doraemonkit.widget.tableview.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.didichuxing.doraemonkit.widget.tableview.style.FontStyle;
import com.didichuxing.doraemonkit.widget.tableview.TableConfig;


public class DrawUtils {

    public static int getTextHeight(FontStyle style, Paint paint){
        style.fillPaint(paint);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) (fontMetrics.descent - fontMetrics.ascent);
    }

    public static int getTextHeight(Paint paint){
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) (fontMetrics.descent - fontMetrics.ascent);
    }

    public static float getTextCenterY(int centerY, Paint paint){
       return centerY-((paint.descent() + paint.ascent()) / 2);
    }

    public static float getTextCenterX(int left, int right, Paint paint){
        Paint.Align align = paint.getTextAlign();
        if(align == Paint.Align.RIGHT){
            return right;
        }else if(align == Paint.Align.LEFT){
            return left;
        }else{
            return (right +left)/2;
        }
    }

    public static boolean isMixRect(Rect rect, int left, int top, int right, int bottom){

        return rect.bottom>= top && rect.right >= left && rect.top <bottom && rect.left< right;
    }

    public static boolean isClick(int left, int top, int right, int bottom, PointF clickPoint){
        return clickPoint.x >= left && clickPoint.x <=right && clickPoint.y>=top && clickPoint.y <=bottom;
    }

    public static boolean isClick(Rect rect, PointF clickPoint){
        return rect.contains((int)clickPoint.x,(int)clickPoint.y);
    }
    public static void fillBackground(Canvas canvas, int left, int top, int right, int bottom, int bgColor, Paint paint){
        if(bgColor != TableConfig.INVALID_COLOR) {
            paint.setColor(bgColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    public static boolean isMixHorizontalRect(Rect rect, int left, int right){

        return   rect.right >= left  && rect.left<= right;
    }
    public static boolean isVerticalMixRect(Rect rect, int top, int bottom){

        return rect.bottom>= top  && rect.top <=bottom;
    }

    /**
     * 获取多行文字高度
     * @param paint
     * @return
     */
    public static int getMultiTextHeight(Paint paint, String[] values){

        return getTextHeight(paint)* values.length;
    }

    /**
     * 获取多行文字宽度
     * @param paint
     * @return
     */
    public static int getMultiTextWidth(Paint paint, String[] values){

        int maxWidth  =0;
        for(String val :values){
            int width = (int) paint.measureText(val);
            if(maxWidth < width){
                maxWidth = width;
            }
        }
        return maxWidth;
    }



    /**
     * 绘制多行文字
     * @param canvas
     * @param paint
     * @param rect
     */
    public static void drawMultiText(Canvas canvas, Paint paint, Rect rect, String[] values){
        for(int i =0;i <values.length;i++) {
            int centerY = (int) ((rect.bottom + rect.top) / 2+ (values.length/2f-i-0.5)*getTextHeight(paint));
            canvas.drawText(values[values.length-i-1], DrawUtils.getTextCenterX(rect.left, rect.right, paint),
                    DrawUtils.getTextCenterY(centerY, paint), paint);
        }
    }

    /**
     * 绘制单行文字
     * @param canvas
     * @param paint
     * @param rect
     * @param value
     */
    public static void drawSingleText(Canvas canvas, Paint paint, Rect rect, String value){
        canvas.drawText(value, DrawUtils.getTextCenterX(rect.left, rect.right, paint),
                DrawUtils.getTextCenterY(rect.centerY(), paint), paint);
    }

}
