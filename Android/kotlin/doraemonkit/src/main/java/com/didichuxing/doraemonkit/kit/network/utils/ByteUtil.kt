package com.didichuxing.doraemonkit.kit.network.utils

import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan

object ByteUtil {
    fun getPrintSize(size: Long): String {
        var size = size
        size = if (size < 1024) {
            return size.toString() + "B"
        } else {
            size / 1024
        }
        size = if (size < 1024) {
            return size.toString() + "KB"
        } else {
            size / 1024
        }
        return if (size < 1024) {
            size = size * 100
            ((size / 100).toString() + "."
                    + (size % 100).toString() + "MB")
        } else {
            size = size * 100 / 1024
            ((size / 100).toString() + "."
                    + (size % 100).toString() + "GB")
        }
    }

    fun getPrintSizeForSpannable(size: Long): SpannableString {
        var size = size
        val spannableString: SpannableString
        val sizeSpan = RelativeSizeSpan(0.5f)
        if (size < 1024) {
            spannableString = SpannableString(size.toString() + "B")
            spannableString.setSpan(sizeSpan, spannableString.length - 1, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            return spannableString
        } else {
            size = size / 1024
        }
        if (size < 1024) {
            spannableString = SpannableString(size.toString() + "KB")
            spannableString.setSpan(sizeSpan, spannableString.length - 2, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            return spannableString
        } else {
            size = size / 1024
        }
        return if (size < 1024) {
            size = size * 100
            val string = ((size / 100).toString() + "."
                    + (size % 100).toString() + "MB")
            spannableString = SpannableString(string)
            spannableString.setSpan(sizeSpan, spannableString.length - 2, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            spannableString
        } else {
            size = size * 100 / 1024
            val string = ((size / 100).toString() + "."
                    + (size % 100).toString() + "GB")
            spannableString = SpannableString(string)
            spannableString.setSpan(sizeSpan, spannableString.length - 2, spannableString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            spannableString
        }
    }
}