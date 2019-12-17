package com.didichuxing.doraemonkit.kit.network.rpc;


import android.support.annotation.NonNull;

import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.common.CommonHeaders;
import com.didichuxing.doraemonkit.kit.network.common.CommonInspectorRequest;
import com.didichuxing.doraemonkit.kit.network.common.CommonInspectorResponse;
import com.didichuxing.doraemonkit.kit.network.common.NetworkPrinterHelper;
import com.didichuxing.doraemonkit.kit.network.core.DefaultResponseHandler;
import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;
import com.didichuxing.foundation.net.MimeType;
import com.didichuxing.foundation.net.http.HttpEntity;
import com.didichuxing.foundation.net.http.HttpHeader;
import com.didichuxing.foundation.net.rpc.http.HttpRpcRequest;
import com.didichuxing.foundation.net.rpc.http.HttpRpcResponse;
import com.didichuxing.foundation.rpc.RpcInterceptor;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;


/**
 * @author: linjizong
 * @date: 2019/1/3
 * @desc: DoraemonKit的拦截器，用以添加抓包逻辑
 */
public class RpcMonitorInterceptor implements RpcInterceptor<HttpRpcRequest, HttpRpcResponse> {
    @Override
    public HttpRpcResponse intercept(RpcChain<HttpRpcRequest, HttpRpcResponse> chain) throws IOException {
        //抓包开关是否打开
        if (!NetworkManager.isActive()) {
            HttpRpcRequest request = chain.getRequest();
            return chain.proceed(request);
        }

        final int id = NetworkPrinterHelper.obtainRequestId();
        HttpRpcRequest request = chain.getRequest();
        HttpRpcResponse response = chain.proceed(request);
        List<HttpHeader> headers = request.getHeaders();
        CommonHeaders.Builder builder = new CommonHeaders.Builder();
        for (int i = 0; i < headers.size(); i++) {
            builder.add(headers.get(i).getName(), headers.get(i).getValue());
        }
        String body = null;
        if (request.getEntity() != null) {
            final byte[] b = retrieveRequest(request);
            body = new String(b);
        }
        // create request bean and update
        CommonInspectorRequest rq = new CommonInspectorRequest(id, request.getUrl(), request.getMethod().toString(), body, builder.build());
        NetworkPrinterHelper.updateRequest(rq);
        headers = response.getHeaders();
        builder = new CommonHeaders.Builder();
        for (int i = 0; i < headers.size(); i++) {
            builder.add(headers.get(i).getName(), headers.get(i).getValue());
        }
        // create response bean and update
        NetworkInterpreter mNetworkInterpreter = NetworkInterpreter.get();
        final HttpEntity entity = response.getEntity();
        MimeType contentType = entity.getContentType();
        InputStream responseStream = response.getEntity().getContent();
        responseStream = mNetworkInterpreter.interpretResponseStream(
                contentType != null ? contentType.toString() : null,
                responseStream,
                new DefaultResponseHandler(mNetworkInterpreter, id, NetworkManager.get().getRecord(id)));
        if (responseStream != null) {
            final InputStream finalResponseStream = responseStream;
            response = response.newBuilder()
                    .setEntity(new HttpEntity() {
                        @Override
                        public MimeType getContentType() {
                            return entity.getContentType();
                        }

                        @Override
                        public String getTransferEncoding() {
                            return entity.getTransferEncoding();
                        }

                        @Override
                        public Charset getCharset() {
                            return entity.getCharset();
                        }

                        @Override
                        public InputStream getContent() throws IOException {
                            return finalResponseStream;
                        }

                        @Override
                        public long getContentLength() throws IOException {
                            return entity.getContentLength();
                        }

                        @Override
                        public void writeTo(OutputStream out) throws IOException {
                            entity.writeTo(out);
                        }

                        @Override
                        public void close() throws IOException {
                            entity.close();
                        }
                    })
                    .build();
        }
        CommonInspectorResponse rp = new CommonInspectorResponse(id, rq.url(), response.getStatus(), builder.build());
        NetworkPrinterHelper.updateResponse(rp);
        return response;
    }

    private static byte[] retrieveRequest(HttpRpcRequest r) {
        try {
            return readFromStream(r.getEntity().getContent(), new byte[512]);
        } catch (IOException e) {
        } catch (Exception e) {
        }
        return null;
    }

    public static byte[] readFromStream(InputStream is, @NonNull byte[] buffer) throws IOException {
        if (is == null) {
            return null;
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int count;
            while ((count = is.read(buffer)) != -1) {
                bos.write(buffer, 0, count);
            }

            closeSilently(is);
            return bos.toByteArray();
        }
    }

    private static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
