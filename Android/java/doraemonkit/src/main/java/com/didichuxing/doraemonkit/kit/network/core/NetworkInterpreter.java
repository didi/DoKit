package com.didichuxing.doraemonkit.kit.network.core;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;
import com.didichuxing.doraemonkit.kit.network.bean.Request;
import com.didichuxing.doraemonkit.kit.network.bean.Response;
import com.didichuxing.doraemonkit.kit.network.stream.InputStreamProxy;
import com.didichuxing.doraemonkit.kit.network.utils.Utf8Charset;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @desc: 网络请求解析类
 */
public class NetworkInterpreter {

    private static class Holder {
        private static NetworkInterpreter INSTANCE = new NetworkInterpreter();
    }

    public static final String TAG = "NetworkInterpreter";

    private final AtomicInteger mNextRequestId = new AtomicInteger(0);
    private ResourceTypeHelper mResourceTypeHelper;

    public static NetworkInterpreter get() {
        return NetworkInterpreter.Holder.INSTANCE;
    }

    public void httpExchangeFailed(int requestId, String s) {
        //LogHelper.i(TAG, "[httpExchangeFailed] requestId: " + requestId + " error: " + s);
    }

    public void responseReadFinished(int requestId, NetworkRecord record, ByteArrayOutputStream outputStream) {
        if (outputStream != null) {
            record.responseLength = outputStream.size();
            record.mResponseBody = outputStream.toString();
            //LogHelper.i(TAG, "[responseReadFinished] body: " + record.mResponseBody.toString().length());
        } else {
            //LogHelper.i(TAG, "[responseReadFinished] outputStream is null request id: " + requestId);
        }
    }

    public void responseReadFailed(int requestId, String s) {
        LogHelper.i(TAG, "[responseReadFailed] requestId: " + requestId + " error: " + s);
    }

    public int nextRequestId() {
        return mNextRequestId.getAndIncrement();
    }

    public InputStream interpretResponseStream(
            String contentType,
            @Nullable InputStream availableInputStream,
            ResponseHandler responseHandler
    ) {
        if (availableInputStream == null) {
            responseHandler.onEOF(null);
            return null;
        }
        ResourceType resourceType = contentType != null
                ? getResourceTypeHelper().determineResourceType(contentType)
                : null;
        if (resourceType != ResourceType.DOCUMENT && resourceType != ResourceType.XHR) {
            responseHandler.onEOF(null);
            return availableInputStream;
        }
        return new InputStreamProxy(availableInputStream, responseHandler);
    }

    public NetworkRecord createRecord(int requestId, String platform, NetworkInterpreter.InspectorRequest request) {
        NetworkRecord record = new NetworkRecord();
        record.mRequestId = requestId;
        record.mPlatform = platform;
        fetchRequestInfo(record, request);
        NetworkManager.get().addRecord(requestId, record);
        return record;
    }

    private void fetchRequestInfo(NetworkRecord record, NetworkInterpreter.InspectorRequest request) {
        Request requestJSON = new Request();
        requestJSON.url = request.url();
        requestJSON.method = request.method();
        requestJSON.headers = formatHeadersAsString(request);
        requestJSON.encode = request.firstHeaderValue("Content-Encoding");
        requestJSON.postData = readBodyAsString(request);
        record.mRequest = requestJSON;
        record.startTime = System.currentTimeMillis();
        record.requestLength = readBodyLength(request);
        //Log.e(TAG, requestJSON.toString());
    }

    public void fetRequestBody(NetworkRecord record, byte[] request) {
        if (record.mRequest != null) {
            record.mRequest.postData = readBodyAsString(request);
            record.requestLength = readBodyLength(request);
            NetworkManager.get().updateRecord(record, false);
            //Log.e(TAG, record.mRequest.postData);
        }
    }

    public void fetchResponseBody(NetworkRecord record, String body) {
        if (TextUtils.isEmpty(body)) {
            record.responseLength = 0;
            record.mResponseBody = null;
        } else {
            record.responseLength = body.getBytes().length;
            record.mResponseBody = body;
        }
    }

    public void fetchResponseInfo(NetworkRecord record, NetworkInterpreter.InspectorResponse response) {
        Response responseJSON = new Response();
        responseJSON.url = response.url();
        responseJSON.status = response.statusCode();
        responseJSON.headers = formatHeadersAsString(response);
        String contentType = getContentType(response);
        responseJSON.mimeType = contentType != null ?
                getResourceTypeHelper().stripContentExtras(contentType) :
                "application/octet-stream";
        record.mResponse = responseJSON;
        record.endTime = System.currentTimeMillis();
        NetworkManager.get().updateRecord(record, false);
        //Log.e(TAG, responseJSON.toString());
    }

    private ResourceTypeHelper getResourceTypeHelper() {
        if (mResourceTypeHelper == null) {
            mResourceTypeHelper = new ResourceTypeHelper();
        }
        return mResourceTypeHelper;
    }

    private String formatHeadersAsString(NetworkInterpreter.InspectorHeaders headers) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < headers.headerCount(); i++) {
            String name = headers.headerName(i);
            String value = headers.headerValue(i);
            builder.append(name + ":" + value);
            if (i != headers.headerCount() - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    private String readBodyAsString(NetworkInterpreter.InspectorRequest request) {
        try {
            byte[] body = request.body();
            if (body != null) {
                return new String(body, Utf8Charset.INSTANCE);
            }
        } catch (IOException | OutOfMemoryError e) {
        }
        return null;
    }

    private String readBodyAsString(byte[] body) {
        try {
            if (body != null) {
                return new String(body, Utf8Charset.INSTANCE);
            }
        } catch (OutOfMemoryError e) {
        }
        return null;
    }

    private long readBodyLength(NetworkInterpreter.InspectorRequest request) {
        try {
            byte[] body = request.body();
            if (body != null) {
                return body.length;
            }
        } catch (IOException | OutOfMemoryError e) {
        }
        return 0;
    }

    private long readBodyLength(byte[] body) {
        try {
            if (body != null) {
                return body.length;
            }
        } catch (OutOfMemoryError e) {
        }
        return 0;
    }

    private String getContentType(NetworkInterpreter.InspectorHeaders headers) {
        // This may need to change in the future depending on how cumbersome header simulation
        // is for the various hooks we expose.
        return headers.firstHeaderValue("Content-Type");
    }


    /**
     * Represents the request that will be sent over HTTP.  Note that for many implementations
     * of HTTP the request constructed may differ from the request actually sent over the wire.
     * For instance, additional headers like {@code Host}, {@code User-Agent}, {@code Content-Type},
     * etc may not be part of this request but should be injected if necessary.  Some stacks offer
     * inspection of the raw request about to be sent to the server which is preferable.
     */
    public interface InspectorRequest extends InspectorRequestCommon {

        String url();

        /**
         * HTTP method ("GET", "POST", "DELETE", etc).
         */
        String method();

        /**
         * Provide the body if part of an entity-enclosing request (like "POST" or "PUT").  May
         * return null otherwise.
         */
        @Nullable
        byte[] body() throws IOException;
    }

    public interface InspectorResponse extends InspectorResponseCommon {
        String url();
    }

    public interface InspectorRequestCommon extends InspectorHeaders {
        /**
         * Unique identifier for this request.  This identifier must be used in all other network
         * events corresponding to this request.  Identifiers may be re-used for HTTP requests  or
         * WebSockets that have exhuasted the state machine to its final closed/finished state.
         */
        int id();
    }

    public interface InspectorResponseCommon extends InspectorHeaders {
        /**
         * @see InspectorRequest#id()
         */
        int requestId();

        int statusCode();
    }

    public interface InspectorHeaders {
        int headerCount();

        String headerName(int index);

        String headerValue(int index);

        @Nullable
        String firstHeaderValue(String name);
    }
}
