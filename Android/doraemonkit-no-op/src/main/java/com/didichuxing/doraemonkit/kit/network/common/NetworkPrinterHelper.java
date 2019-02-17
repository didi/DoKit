package com.didichuxing.doraemonkit.kit.network.common;

/**
 * @desc: 目前DoraemonKit只支持okhttp3和HttpUrlConnection的自动抓包，其他网络库如果想把网络请求打印出来，需要手动调用该类的方法将请求内容写入
 */
public class NetworkPrinterHelper {
    private static final String TAG = "NetworkLogHelper";

    private NetworkPrinterHelper() {

    }


    private static class Holder {
        private static NetworkPrinterHelper INSTANCE = new NetworkPrinterHelper();
    }

    private static NetworkPrinterHelper get() {
        return Holder.INSTANCE;
    }

    /**
     * @return 返回一个请求id，后续所有的更新操作都需要传入这个请求id，用以定位对应的bean
     */
    public static int obtainRequestId() {
        return 0;
    }

    public static void updateRequest( CommonInspectorRequest request) {
    }

    public static void updateResponse( CommonInspectorResponse response) {
    }

    public static void updateResponseBody(int requestId, String body) {
    }

}
