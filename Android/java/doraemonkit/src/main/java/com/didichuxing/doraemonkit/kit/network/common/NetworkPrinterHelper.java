package com.didichuxing.doraemonkit.kit.network.common;


import androidx.annotation.NonNull;

import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;
import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;
import com.didichuxing.doraemonkit.util.LogHelper;

/**
 * @desc: 目前DoraemonKit只支持okhttp3和HttpUrlConnection的自动抓包，其他网络库如果想把网络请求打印出来，需要手动调用该类的方法将请求内容写入
 */
public class NetworkPrinterHelper {
    private static final String TAG = "NetworkLogHelper";
    private final NetworkInterpreter mInterpreter = NetworkInterpreter.get();

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
        return get().mInterpreter.nextRequestId();
    }

    public static void updateRequest(@NonNull CommonInspectorRequest request) {
        get().mInterpreter.createRecord(request.id(), "native", request);
    }

    public static void updateResponse(@NonNull CommonInspectorResponse response) {
        NetworkRecord networkRecord = NetworkManager.get().getRecord(response.requestId());
        if (networkRecord != null) {
            get().mInterpreter.fetchResponseInfo(networkRecord, response);
        } else {
            LogHelper.e(TAG, "updateResponse fail ,record is null for requestId: " + response.requestId());
        }
    }

    public static void updateResponseBody(int requestId, String body) {
        NetworkRecord networkRecord = NetworkManager.get().getRecord(requestId);
        if (networkRecord != null) {
            get().mInterpreter.fetchResponseBody(networkRecord, body);
        } else {
            LogHelper.e(TAG, "updateResponseBody fail ,record is null for requestId: " + requestId);
        }
    }

}
