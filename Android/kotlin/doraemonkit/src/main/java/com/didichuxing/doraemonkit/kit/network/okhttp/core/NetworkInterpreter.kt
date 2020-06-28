package com.didichuxing.doraemonkit.kit.network.okhttp.core

import android.text.TextUtils
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.NetworkRecord
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.Request
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.Response
import com.didichuxing.doraemonkit.kit.network.okhttp.stream.InputStreamProxy
import com.didichuxing.doraemonkit.kit.network.okhttp.utils.Utf8Charset
import com.didichuxing.doraemonkit.util.LogHelper.i
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @desc: 网络请求解析类
 */
class NetworkInterpreter {
    private val mNextRequestId = AtomicInteger(0)
    private var mResourceTypeHelper: ResourceTypeHelper? = null
    fun httpExchangeFailed(requestId: Int, s: String?) {
        //LogHelper.i(TAG, "[httpExchangeFailed] requestId: " + requestId + " error: " + s);
    }

    fun responseReadFinished(requestId: Int, record: NetworkRecord, outputStream: ByteArrayOutputStream?) {
        outputStream?.let {
            record.responseLength = it.size().toLong()
            record.mResponseBody = it.toString()
            //LogHelper.i(TAG, "[responseReadFinished] body: " + record.mResponseBody.toString().length());
        }
        //else {
        //LogHelper.i(TAG, "[responseReadFinished] outputStream is null request id: " + requestId);
        //}
    }

    fun responseReadFailed(requestId: Int, s: String) {
        i(TAG, "[responseReadFailed] requestId: $requestId error: $s")
    }

    private object Holder {
        val INSTANCE = NetworkInterpreter()
    }

    fun nextRequestId(): Int {
        return mNextRequestId.getAndIncrement()
    }

    fun interpretResponseStream(
            contentType: String?,
            availableInputStream: InputStream?,
            responseHandler: ResponseHandler): InputStream? {
        if (availableInputStream == null) {
            responseHandler.onEOF(null)
            return null
        }
        val resourceType = if (contentType != null) resourceTypeHelper.determineResourceType(contentType) else null
        if (resourceType != ResourceType.DOCUMENT && resourceType != ResourceType.XHR) {
            responseHandler.onEOF(null)
            return availableInputStream
        }
        return InputStreamProxy(
                availableInputStream,
                responseHandler)
    }

    fun createRecord(requestId: Int, request: InspectorRequest): NetworkRecord {
        val record = NetworkRecord()
        record.mRequestId = requestId
        fetchRequestInfo(record, request)
        NetworkManager.get().addRecord(requestId, record)
        return record
    }

    private fun fetchRequestInfo(record: NetworkRecord, request: InspectorRequest) {
        val requestJSON = Request()
        requestJSON.apply {
            url = request.url()
            method = request.method()
            headers = formatHeadersAsString(request)
            encode = request.firstHeaderValue("Content-Encoding")
            postData = readBodyAsString(request)
        }

        record.apply {
            mRequest = requestJSON
            startTime = System.currentTimeMillis()
            requestLength = readBodyLength(request)
        }

        //Log.e(TAG, requestJSON.toString());
    }

    fun fetRequestBody(record: NetworkRecord, request: ByteArray?) {
        if (record.mRequest != null) {
            record.mRequest!!.postData = readBodyAsString(request)
            record.requestLength = readBodyLength(request)
            NetworkManager.get().updateRecord(record, false)
            //Log.e(TAG, record.mRequest.postData);
        }
    }

    fun fetchResponseBody(record: NetworkRecord, body: String) {
        if (TextUtils.isEmpty(body)) {
            record.responseLength = 0
            record.mResponseBody = null
        } else {
            record.responseLength = body.toByteArray().size.toLong()
            record.mResponseBody = body
        }
    }

    fun fetchResponseInfo(record: NetworkRecord, response: InspectorResponse) {
        val responseJSON = Response()
        responseJSON.apply {
            url = response.url()
            status = response.statusCode()
            headers = formatHeadersAsString(response)
            val contentType = getContentType(response)
            mimeType = if (contentType != null) resourceTypeHelper.stripContentExtras(contentType) else "application/octet-stream"
        }
        record.mResponse = responseJSON
        record.endTime = System.currentTimeMillis()
        NetworkManager.get().updateRecord(record, false)
        //Log.e(TAG, responseJSON.toString());
    }

    private  val resourceTypeHelper: ResourceTypeHelper
        private get() {
            if (mResourceTypeHelper == null) {
                mResourceTypeHelper = ResourceTypeHelper()
            }
            return mResourceTypeHelper!!
        }

    private fun formatHeadersAsString(headers: InspectorHeaders): String {
        val builder = StringBuilder()
        for (i in 0 until headers.headerCount()) {
            val name = headers.headerName(i)
            val value = headers.headerValue(i)
            builder.append("$name:$value")
            if (i != headers.headerCount() - 1) {
                builder.append("\n")
            }
        }
        return builder.toString()
    }

    private fun readBodyAsString(
            request: InspectorRequest): String? {
        try {
            val body = request.body()
            if (body != null) {
                return String(body, Utf8Charset.INSTANCE)
            }
        } catch (e: IOException) {
        } catch (e: OutOfMemoryError) {
        }
        return null
    }

    private fun readBodyAsString(
            body: ByteArray?): String? {
        try {
            if (body != null) {
                return String(body, Utf8Charset.INSTANCE)
            }
        } catch (e: OutOfMemoryError) {
        }
        return null
    }

    private fun readBodyLength(
            request: InspectorRequest): Long {
        try {
            val body = request.body()
            if (body != null) {
                return body.size.toLong()
            }
        } catch (e: IOException) {
        } catch (e: OutOfMemoryError) {
        }
        return 0
    }

    private fun readBodyLength(
            body: ByteArray?): Long {
        try {
            if (body != null) {
                return body.size.toLong()
            }
        } catch (e: OutOfMemoryError) {
        }
        return 0
    }

    private fun getContentType(headers: InspectorHeaders): String? {
        // This may need to change in the future depending on how cumbersome header simulation
        // is for the various hooks we expose.
        return headers.firstHeaderValue("Content-Type")
    }

    /**
     * Represents the request that will be sent over HTTP.  Note that for many implementations
     * of HTTP the request constructed may differ from the request actually sent over the wire.
     * For instance, additional headers like `Host`, `User-Agent`, `Content-Type`,
     * etc may not be part of this request but should be injected if necessary.  Some stacks offer
     * inspection of the raw request about to be sent to the server which is preferable.
     */
    interface InspectorRequest : InspectorRequestCommon {
        fun url(): String?

        /**
         * HTTP method ("GET", "POST", "DELETE", etc).
         */
        fun method(): String?

        /**
         * Provide the body if part of an entity-enclosing request (like "POST" or "PUT").  May
         * return null otherwise.
         */
        @Throws(IOException::class)
        fun body(): ByteArray?
    }

    interface InspectorResponse : InspectorResponseCommon {
        fun url(): String?
    }

    interface InspectorRequestCommon : InspectorHeaders {
        /**
         * Unique identifier for this request.  This identifier must be used in all other network
         * events corresponding to this request.  Identifiers may be re-used for HTTP requests  or
         * WebSockets that have exhuasted the state machine to its final closed/finished state.
         */
        fun id(): Int
    }

    interface InspectorResponseCommon : InspectorHeaders {
        /**
         * @see InspectorRequest.id
         */
        fun requestId(): Int
        fun statusCode(): Int
    }

    interface InspectorHeaders {
        fun headerCount(): Int
        fun headerName(index: Int): String
        fun headerValue(index: Int): String
        fun firstHeaderValue(name: String): String?
    }

    companion object {
        const val TAG = "NetworkInterpreter"
        fun get(): NetworkInterpreter {
            return Holder.INSTANCE
        }
    }
}