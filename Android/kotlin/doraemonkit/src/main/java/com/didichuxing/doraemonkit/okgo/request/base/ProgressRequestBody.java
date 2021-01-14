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
package com.didichuxing.doraemonkit.okgo.request.base;

import com.didichuxing.doraemonkit.okgo.callback.Callback;
import com.didichuxing.doraemonkit.okgo.model.Progress;
import com.didichuxing.doraemonkit.okgo.utils.HttpUtils;
import com.didichuxing.doraemonkit.okgo.utils.OkLogger;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：包装的请求体，处理进度，可以处理任何的 RequestBody，
 * 修订历史：
 * ================================================
 */
public class ProgressRequestBody<T> extends RequestBody {

    private RequestBody requestBody;         //实际的待包装请求体
    private Callback<T> callback;
    private UploadInterceptor interceptor;

    ProgressRequestBody(RequestBody requestBody, Callback<T> callback) {
        this.requestBody = requestBody;
        this.callback = callback;
    }

    /** 重写调用实际的响应体的contentType */
    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    /** 重写调用实际的响应体的contentLength */
    @Override
    public long contentLength() {
        try {
            return requestBody.contentLength();
        } catch (IOException e) {
            OkLogger.printStackTrace(e);
            return -1;
        }
    }

    /** 重写进行写入 */
    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        CountingSink countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        requestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    /** 包装 */
    private final class CountingSink extends ForwardingSink {

        private Progress progress;

        CountingSink(Sink delegate) {
            super(delegate);
            progress = new Progress();
            progress.totalSize = contentLength();
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);

            Progress.changeProgress(progress, byteCount, new Progress.Action() {
                @Override
                public void call(Progress progress) {
                    if (interceptor != null) {
                        interceptor.uploadProgress(progress);
                    } else {
                        onProgress(progress);
                    }
                }
            });
        }
    }

    private void onProgress(final Progress progress) {
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.uploadProgress(progress);
                }
            }
        });
    }

    public void setInterceptor(UploadInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public interface UploadInterceptor {
        void uploadProgress(Progress progress);
    }
}
