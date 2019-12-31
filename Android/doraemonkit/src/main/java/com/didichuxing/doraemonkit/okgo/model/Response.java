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
package com.didichuxing.doraemonkit.okgo.model;

import okhttp3.Call;
import okhttp3.Headers;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/9/11
 * 描    述：响应体的包装类
 * 修订历史：
 * ================================================
 */
public final class Response<T> {

    private T body;
    private Throwable throwable;
    private boolean isFromCache;
    private okhttp3.Call rawCall;
    private okhttp3.Response rawResponse;

    public static <T> Response<T> success(boolean isFromCache, T body, Call rawCall, okhttp3.Response rawResponse) {
        Response<T> response = new Response<>();
        response.setFromCache(isFromCache);
        response.setBody(body);
        response.setRawCall(rawCall);
        response.setRawResponse(rawResponse);
        return response;
    }

    public static <T> Response<T> error(boolean isFromCache, Call rawCall, okhttp3.Response rawResponse, Throwable throwable) {
        Response<T> response = new Response<>();
        response.setFromCache(isFromCache);
        response.setRawCall(rawCall);
        response.setRawResponse(rawResponse);
        response.setException(throwable);
        return response;
    }

    public Response() {
    }

    public int code() {
        if (rawResponse == null) return -1;
        return rawResponse.code();
    }

    public String message() {
        if (rawResponse == null) return null;
        return rawResponse.message();
    }

    public Headers headers() {
        if (rawResponse == null) return null;
        return rawResponse.headers();
    }

    public boolean isSuccessful() {
        return throwable == null;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public T body() {
        return body;
    }

    public Throwable getException() {
        return throwable;
    }

    public void setException(Throwable exception) {
        this.throwable = exception;
    }

    public Call getRawCall() {
        return rawCall;
    }

    public void setRawCall(Call rawCall) {
        this.rawCall = rawCall;
    }

    public okhttp3.Response getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(okhttp3.Response rawResponse) {
        this.rawResponse = rawResponse;
    }

    public boolean isFromCache() {
        return isFromCache;
    }

    public void setFromCache(boolean fromCache) {
        isFromCache = fromCache;
    }
}
