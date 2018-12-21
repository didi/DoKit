package com.didichuxing.doraemonkit.kit.network.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;

public class ByteUtil {
    public static String getPrintSize(long size) {
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }

    public static SpannableString getPrintSizeForSpannable(long size) {
        SpannableString spannableString;
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.5f);
        if (size < 1024) {
            spannableString = new SpannableString(String.valueOf(size) + "B");
            spannableString.setSpan(sizeSpan, spannableString.length() - 1, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            spannableString = new SpannableString(String.valueOf(size) + "KB");
            spannableString.setSpan(sizeSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            size = size * 100;
            String string = String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
            spannableString = new SpannableString(string);
            spannableString.setSpan(sizeSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        } else {
            size = size * 100 / 1024;
            String string = String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
            spannableString = new SpannableString(string);
            spannableString.setSpan(sizeSpan, spannableString.length() - 2, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }

}
