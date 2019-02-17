package com.didichuxing.doraemonkit.kit.network.core;

import java.io.IOException;

/**
 * @desc: 网络请求解析类
 */
public class NetworkInterpreter {

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

        String firstHeaderValue(String name);
    }
}
