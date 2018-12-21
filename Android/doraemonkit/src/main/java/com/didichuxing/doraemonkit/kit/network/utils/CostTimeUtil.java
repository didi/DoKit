package com.didichuxing.doraemonkit.kit.network.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;

import com.didichuxing.doraemonkit.R;

public class CostTimeUtil {
    public final static long SECOND = 1000;
    public final static long MINUTE = SECOND * 60;
    public final static long HOUR = MINUTE * 60;
    public final static long DAY = HOUR * 24;


    public static SpannableString formatTime(Context context, long time) {
        SpannableString spannableString;
        if (time == 0) {
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.5f);
            spannableString = new SpannableString(context.getString(R.string.dk_network_summary_total_time_default));
            spannableString.setSpan(sizeSpan, spannableString.length() - 1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (time < 100 * SECOND) {
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.5f);
            spannableString = new SpannableString(context.getString(R.string.dk_network_summary_total_time_second, time / SECOND));
            spannableString.setSpan(sizeSpan, spannableString.length() - 1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (time < 100 * MINUTE) {
            long minute = time / MINUTE;
            long second = time % MINUTE / SECOND;
            spannableString = new SpannableString(context.getString(R.string.dk_network_summary_total_time_minute, minute, second));
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.5f);
            spannableString.setSpan(sizeSpan, String.valueOf(minute).length(), String.valueOf(minute).length() + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            sizeSpan = new RelativeSizeSpan(0.5f);
            spannableString.setSpan(sizeSpan, spannableString.length() - 1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else if (time < 100 * HOUR) {
            long hour = time / HOUR;
            long minute = time % HOUR / MINUTE;
            spannableString = new SpannableString(context.getString(R.string.dk_network_summary_total_time_hour, hour, minute));
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.5f);
            spannableString.setSpan(sizeSpan, String.valueOf(hour).length(), String.valueOf(hour).length() + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            sizeSpan = new RelativeSizeSpan(0.5f);
            spannableString.setSpan(sizeSpan, spannableString.length() - 1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            long day = time / DAY;
            long hour = time % DAY / HOUR;
            spannableString = new SpannableString(context.getString(R.string.dk_network_summary_total_time_day, day, hour));
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.5f);
            spannableString.setSpan(sizeSpan, String.valueOf(day).length(), String.valueOf(day).length() + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            sizeSpan = new RelativeSizeSpan(0.5f);
            spannableString.setSpan(sizeSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }


}
