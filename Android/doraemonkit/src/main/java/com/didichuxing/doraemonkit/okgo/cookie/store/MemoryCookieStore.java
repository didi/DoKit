/*
 * Copyright 2016 jeasonlzy(廖子尧)
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
package com.didichuxing.doraemonkit.okgo.cookie.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：Cookie 的内存管理
 * 修订历史：
 * ================================================
 */
public class MemoryCookieStore implements CookieStore {

    private final Map<String, List<Cookie>> memoryCookies = new HashMap<>();

    @Override
    public synchronized void saveCookie(HttpUrl url, List<Cookie> cookies) {
        List<Cookie> oldCookies = memoryCookies.get(url.host());
        List<Cookie> needRemove = new ArrayList<>();
        for (Cookie newCookie : cookies) {
            for (Cookie oldCookie : oldCookies) {
                if (newCookie.name().equals(oldCookie.name())) {
                    needRemove.add(oldCookie);
                }
            }
        }
        oldCookies.removeAll(needRemove);
        oldCookies.addAll(cookies);
    }

    @Override
    public synchronized void saveCookie(HttpUrl url, Cookie cookie) {
        List<Cookie> cookies = memoryCookies.get(url.host());
        List<Cookie> needRemove = new ArrayList<>();
        for (Cookie item : cookies) {
            if (cookie.name().equals(item.name())) {
                needRemove.add(item);
            }
        }
        cookies.removeAll(needRemove);
        cookies.add(cookie);
    }

    @Override
    public synchronized List<Cookie> loadCookie(HttpUrl url) {
        List<Cookie> cookies = memoryCookies.get(url.host());
        if (cookies == null) {
            cookies = new ArrayList<>();
            memoryCookies.put(url.host(), cookies);
        }
        return cookies;
    }

    @Override
    public synchronized List<Cookie> getAllCookie() {
        List<Cookie> cookies = new ArrayList<>();
        Set<String> httpUrls = memoryCookies.keySet();
        for (String url : httpUrls) {
            cookies.addAll(memoryCookies.get(url));
        }
        return cookies;
    }

    @Override
    public List<Cookie> getCookie(HttpUrl url) {
        List<Cookie> cookies = new ArrayList<>();
        List<Cookie> urlCookies = memoryCookies.get(url.host());
        if (urlCookies != null) cookies.addAll(urlCookies);
        return cookies;
    }

    @Override
    public synchronized boolean removeCookie(HttpUrl url, Cookie cookie) {
        List<Cookie> cookies = memoryCookies.get(url.host());
        return (cookie != null) && cookies.remove(cookie);
    }

    @Override
    public synchronized boolean removeCookie(HttpUrl url) {
        return memoryCookies.remove(url.host()) != null;
    }

    @Override
    public synchronized boolean removeAllCookie() {
        memoryCookies.clear();
        return true;
    }
}
