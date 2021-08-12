package com.didichuxing.doraemonkit.kit.loginfo.util;

import android.text.TextUtils;

import com.didichuxing.doraemonkit.kit.loginfo.LogLine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchCriteria {

    public static final String PID_KEYWORD = "pid:";
    public static final String TAG_KEYWORD = "tag:";

    private static final Pattern PID_PATTERN = Pattern.compile("pid:(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern TAG_PATTERN = Pattern.compile("tag:(\"[^\"]+\"|\\S+)", Pattern.CASE_INSENSITIVE);

    private int pid = -1;
    private String tag;
    private String searchText;
    private int searchTextAsInt = -1;

    public SearchCriteria(CharSequence inputQuery) {

        // check for the "pid" keyword

        StringBuilder query = new StringBuilder(StringUtil.nullToEmpty(inputQuery));
        Matcher pidMatcher = PID_PATTERN.matcher(query);
        if (pidMatcher.find()) {
            try {
                pid = Integer.parseInt(pidMatcher.group(1));
                query.replace(pidMatcher.start(), pidMatcher.end(), ""); // detach
                // from
                // search
                // string
            } catch (NumberFormatException ignore) {
            }
        }

        // check for the "tag" keyword

        Matcher tagMatcher = TAG_PATTERN.matcher(query);
        if (tagMatcher.find()) {
            tag = tagMatcher.group(1);
            if (tag.startsWith("\"") && tag.endsWith("\"")) {
                tag = tag.substring(1, tag.length() - 1); // detach quotes
            }
            query.replace(tagMatcher.start(), tagMatcher.end(), ""); // detach
            // from
            // search
            // string
        }

        // everything else becomes a search term
        searchText = query.toString().trim();

        try {
            searchTextAsInt = Integer.parseInt(searchText);
        } catch (NumberFormatException ignore) {
        }

    }

    public boolean isEmpty() {
        return pid == -1 && TextUtils.isEmpty(tag) && TextUtils.isEmpty(searchText);
    }

    public boolean matches(LogLine logLine) {

        // consider the criteria to be ANDed
        if (!checkFoundPid(logLine)) {
            return false;
        }
        if (!checkFoundTag(logLine)) {
            return false;
        }
        return checkFoundText(logLine);
    }

    private boolean checkFoundText(LogLine logLine) {
        return TextUtils.isEmpty(searchText)
                || (searchTextAsInt != -1 && searchTextAsInt == logLine.getProcessId())
                || (logLine.getTag() != null && StringUtil.containsIgnoreCase(logLine.getTag(), searchText))
                || (logLine.getLogOutput() != null && StringUtil.containsIgnoreCase(logLine.getLogOutput(), searchText));
    }

    private boolean checkFoundTag(LogLine logLine) {
        return TextUtils.isEmpty(tag)
                || (logLine.getTag() != null && StringUtil.containsIgnoreCase(logLine.getTag(), tag));
    }

    private boolean checkFoundPid(LogLine logLine) {
        return pid == -1 || logLine.getProcessId() == pid;
    }

}
