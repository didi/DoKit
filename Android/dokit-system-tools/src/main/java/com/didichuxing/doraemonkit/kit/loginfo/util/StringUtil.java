package com.didichuxing.doraemonkit.kit.loginfo.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nolan
 */
public class StringUtil {

    /**
     * Pad the specified number of spaces to the input string to make it that length
     *
     * @param input
     * @param size
     * @return
     */
    public static String padLeft(String input, int size) {

        if (input.length() > size) {
            throw new IllegalArgumentException("input must be shorter than or equal to the number of spaces: " + size);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = input.length(); i < size; i++) {
            sb.append(" ");
        }
        return sb.append(input).toString();
    }

    /**
     * same as the String.split(), except it doesn't use regexes, so it's faster.
     *
     * @param str       - the string to split up
     * @param delimiter the delimiter
     * @return the split string
     */
    public static String[] split(String str, String delimiter) {
        List<String> result = new ArrayList<>();
        int lastIndex = 0;
        int index = str.indexOf(delimiter);
        while (index != -1) {
            result.add(str.substring(lastIndex, index));
            lastIndex = index + delimiter.length();
            index = str.indexOf(delimiter, index + delimiter.length());
        }
        result.add(str.substring(lastIndex, str.length()));

        return ArrayUtil.toArray(result, String.class);
    }

    /*
        * Replace all occurrences of the searchString in the originalString with the replaceString.  Faster than the
        * String.replace() method.  Does not use regexes.
        * <p/>
        * If your searchString is empty, this will spin forever.
        *
        *
        * @param originalString
        * @param searchString
        * @param replaceString
        * @return
        */
    public static String replace(String originalString, String searchString, String replaceString) {
        StringBuilder sb = new StringBuilder(originalString);
        int index = sb.indexOf(searchString);
        while (index != -1) {
            sb.replace(index, index + searchString.length(), replaceString);
            index += replaceString.length();
            index = sb.indexOf(searchString, index);
        }
        return sb.toString();
    }

    public static String join(String delimiter, String[] strings) {

        if (strings.length == 0) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String str : strings) {
            stringBuilder.append(" ").append(str);
        }

        return stringBuilder.substring(1);
    }


    public static int computeLevenshteinDistance(CharSequence str1, CharSequence str2) {


        int commonPrefixLength = findCommonPrefixLength(str1, str2);

        if (commonPrefixLength == str1.length() && commonPrefixLength == str2.length()) {
            return 0; // same exact string
        }

        int commonSuffixLength = findCommonSuffixLength(str1, str2, commonPrefixLength);

        str1 = str1.subSequence(commonPrefixLength, str1.length() - commonSuffixLength);
        str2 = str2.subSequence(commonPrefixLength, str2.length() - commonSuffixLength);

        int[][] distance = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            distance[i][0] = i;
        }
        for (int j = 0; j <= str2.length(); j++) {
            distance[0][j] = j;
        }

        for (int i = 1; i <= str1.length(); i++) {
            for (int j = 1; j <= str2.length(); j++) {
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
                                : 1));
            }
        }

        return distance[str1.length()][str2.length()];
    }

    private static int findCommonPrefixLength(CharSequence str1, CharSequence str2) {

        int length = (Math.min(str1.length(), str2.length()));
        for (int i = 0; i < length; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                return i;
            }
        }

        return 0;

    }

    private static int findCommonSuffixLength(CharSequence str1, CharSequence str2, int commonPrefixLength) {
        int length = (Math.min(str1.length(), str2.length()));
        for (int i = 0; i < length - commonPrefixLength; i++) {
            if (str1.charAt(str1.length() - i - 1) != str2.charAt(str2.length() - i - 1)) {
                return i;
            }
        }

        return 0;
    }

    private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static String join(int[] arr, String delimiter) {

        if (arr.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i : arr) {
            sb.append(delimiter).append(Integer.toString(i));
        }

        return sb.substring(delimiter.length());

    }

    public static String capitalize(String str) {

        StringBuilder sb = new StringBuilder(str);

        for (int i = 0; i < sb.length(); i++) {
            if (i == 0 || Character.isWhitespace(sb.charAt(i - 1))) {
                sb.replace(i, i + 1, Character.toString(Character.toUpperCase(sb.charAt(i))));
            }
        }

        return sb.toString();
    }

    public static String nullToEmpty(CharSequence str) {
        return str == null ? "" : str.toString();
    }

    public static boolean isEmptyOrWhitespaceOnly(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * same as String.contains, but ignores case.
     *
     * @param str
     * @param query
     * @return
     */
    public static boolean containsIgnoreCase(String str, String query) {
        if (str != null && query != null) {
            int limit = str.length() - query.length() + 1;
            for (int i = 0; i < limit; i++) {
                if (matchesIgnoreCase(str, query, i)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean matchesIgnoreCase(String str, String query, int startingAt) {
        int len = query.length();
        for (int i = 0; i < len; i++) {
            if (Character.toUpperCase(query.charAt(i)) != Character.toUpperCase(str.charAt(startingAt + i))) {
                return false;
            }
        }
        return true;
    }
}
