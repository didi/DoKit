package com.didichuxing.doraemondemo.mc;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.didichuxing.doraemonkit.util.UIUtils;

public class HierarchicalViewIdHelper {

    public static View debugViewIds(View view, String logtag) {
        if (view.getId() == -1) {
            Log.v(logtag, "traversing: " + view.getClass().getSimpleName() + ", id: " + view.getId());
        } else {
            Log.v(logtag, "traversing: " + view.getClass().getSimpleName() + ", id: " + UIUtils.getIdText(view));
        }
        if (view.getParent() != null && (view.getParent() instanceof ViewGroup)) {
            return debugViewIds((View) view.getParent(), logtag);
        } else {
            debugChildViewIds(view, logtag, 0);
            return view;
        }
    }

    private static void debugChildViewIds(View view, String logtag, int spaces) {
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (view.getId() == -1) {
                    Log.v(logtag, padString("view: " + child.getClass().getSimpleName() + "(" + child.getId() + ")", spaces));
                } else {
                    Log.v(logtag, padString("view: " + child.getClass().getSimpleName() + "(" + UIUtils.getIdText(child) + ")", spaces));
                }
                debugChildViewIds(child, logtag, spaces + 1);
            }
        }
    }

    private static String padString(String str, int noOfSpaces) {
        if (noOfSpaces <= 0) {
            return str;
        }
        StringBuilder builder = new StringBuilder(str.length() + noOfSpaces);
        for (int i = 0; i < noOfSpaces; i++) {
            builder.append(' ');
        }
        return builder.append(str).toString();
    }

}
