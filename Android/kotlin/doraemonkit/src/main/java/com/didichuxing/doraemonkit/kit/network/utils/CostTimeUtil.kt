package com.didichuxing.doraemonkit.kit.network.utils

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import com.didichuxing.doraemonkit.R

object CostTimeUtil {
    const val SECOND: Long = 1000
    const val MINUTE = SECOND * 60
    const val HOUR = MINUTE * 60
    const val DAY = HOUR * 24
    fun formatTime(context: Context, time: Long): SpannableString {
        val spannableString: SpannableString
        if (time == 0L) {
            val sizeSpan = RelativeSizeSpan(0.5f)
            spannableString = SpannableString(context.getString(R.string.dk_network_summary_total_time_default))
            spannableString.setSpan(sizeSpan, spannableString.length - 1, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        } else if (time < 100 * SECOND) {
            val sizeSpan = RelativeSizeSpan(0.5f)
            spannableString = SpannableString(context.getString(R.string.dk_network_summary_total_time_second, time / SECOND))
            spannableString.setSpan(sizeSpan, spannableString.length - 1, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        } else if (time < 100 * MINUTE) {
            val minute = time / MINUTE
            val second = time % MINUTE / SECOND
            spannableString = SpannableString(context.getString(R.string.dk_network_summary_total_time_minute, minute, second))
            var sizeSpan = RelativeSizeSpan(0.5f)
            spannableString.setSpan(sizeSpan, minute.toString().length, minute.toString().length + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            sizeSpan = RelativeSizeSpan(0.5f)
            spannableString.setSpan(sizeSpan, spannableString.length - 1, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        } else if (time < 100 * HOUR) {
            val hour = time / HOUR
            val minute = time % HOUR / MINUTE
            spannableString = SpannableString(context.getString(R.string.dk_network_summary_total_time_hour, hour, minute))
            var sizeSpan = RelativeSizeSpan(0.5f)
            spannableString.setSpan(sizeSpan, hour.toString().length, hour.toString().length + 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            sizeSpan = RelativeSizeSpan(0.5f)
            spannableString.setSpan(sizeSpan, spannableString.length - 1, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        } else {
            val day = time / DAY
            val hour = time % DAY / HOUR
            spannableString = SpannableString(context.getString(R.string.dk_network_summary_total_time_day, day, hour))
            var sizeSpan = RelativeSizeSpan(0.5f)
            spannableString.setSpan(sizeSpan, day.toString().length, day.toString().length + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            sizeSpan = RelativeSizeSpan(0.5f)
            spannableString.setSpan(sizeSpan, spannableString.length - 2, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        return spannableString
    }
}