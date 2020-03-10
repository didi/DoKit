/*
 * Copyright (C) 2014 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.didichuxing.doraemonkit.kit.loginfo.reader;

import java.util.regex.Pattern;

public class ScrubberUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9_]+(?:\\.[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*(@|%40)(?!([a-zA-Z0-9]*\\.[a-zA-Z0-9]*\\.[a-zA-Z0-9]*\\.))(?:[A-Za-z0-9](?:[a-zA-Z0-9-]*[A-Za-z0-9])?\\.)+[a-zA-Z](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?");
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?$");
    private static final Pattern WEB_URL_PATTERN = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final Pattern PHONE_INFO_PATTERN = Pattern.compile("(msisdn=|mMsisdn=|iccid=|iccid: |mImsi=)[a-zA-Z0-9]*", Pattern.CASE_INSENSITIVE);
    private static final Pattern USER_INFO_PATTERN = Pattern.compile("(UserInfo\\{\\d:)[a-zA-Z0-9\\s]*", Pattern.CASE_INSENSITIVE);
    private static final Pattern ACCOUNT_INFO_PATTERN = Pattern.compile("(Account \\{name=)[a-zA-Z0-9]*", Pattern.CASE_INSENSITIVE);

    private static final String IGNORE_DATA_RESOURCE_CACHE = "/data/resource-cache";
    private static final String IGNORE_DATA_DALVIK_CACHE = "/data/dalvik-cache";
    private static final String IGNORE_CACHE_DALVIK_CACHE = "/cache/dalvik-cache";

    public static String scrubLine(String line) {
        if (line.contains(IGNORE_DATA_RESOURCE_CACHE)
                || line.contains(IGNORE_DATA_DALVIK_CACHE)
                || line.contains(IGNORE_CACHE_DALVIK_CACHE)) {
            // ugly work around :/
            return line;
        }
        line = IP_ADDRESS_PATTERN.matcher(line).replaceAll("<IP address omitted>");
        line = EMAIL_PATTERN.matcher(line).replaceAll("<email omitted>");
        line = PHONE_NUMBER_PATTERN.matcher(line).replaceAll("<phone number omitted>");
        line = WEB_URL_PATTERN.matcher(line).replaceAll("<web url omitted>");
        line = PHONE_INFO_PATTERN.matcher(line).replaceAll("<omitted>");
        line = USER_INFO_PATTERN.matcher(line).replaceAll("<omitted>");
        line = ACCOUNT_INFO_PATTERN.matcher(line).replaceAll("<omitted>");

        return line;
    }

}