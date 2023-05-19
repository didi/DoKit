package com.didichuxing.doraemonkit.kit.network;


import android.os.Handler;
import android.os.Looper;

import com.didichuxing.doraemonkit.BuildConfig;
import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @desc: 提供网络抓包功能开启、关闭、数据统计功能的manager
 */
public class NetworkManager {
    /**
     * ########以下为数据mock 的相关网络接口#########
     */

    public static final String MOCK_SCHEME_HTTP = "http://";
    public static final String MOCK_SCHEME_HTTPS = "https://";
    //    private static final String MOCK_HOST_DEBUG = "xyrd.intra.xiaojukeji.com";
    public static final String DOKIT_HOST = "www.dokit.cn";
    public static final String MOCK_HOST_DEBUG = "mock.dokit.cn";
    //    private static final String MOCK_HOST_DEBUG = "pre.dokit.cn";
    public static final String MOCK_HOST_RELEASE = "mock.dokit.cn";
    private static final String MOCK_DEBUG_DOMAIN = MOCK_SCHEME_HTTPS + MOCK_HOST_DEBUG;
    private static final String MOCK_RELEASE_DOMAIN = MOCK_SCHEME_HTTPS + MOCK_HOST_RELEASE;
    public static final String MOCK_DOMAIN = BuildConfig.DEBUG ? MOCK_DEBUG_DOMAIN : MOCK_RELEASE_DOMAIN;
    public static final String MOCK_HOST = BuildConfig.DEBUG ? MOCK_HOST_DEBUG : MOCK_HOST_RELEASE;
    /**
     * ########以上为数据mock 的相关网络接口#########
     */


    /**
     * ########app健康体检相关接口 的相关网络接口#########
     * 线上地址：https://www.dokit.cn/healthCheck/addCheckData
     * 测试环境地址:http://dokit-test.intra.xiaojukeji.com/healthCheck/addCheckData
     */
    public static final String APP_HEALTH_URL = "https://www.dokit.cn/healthCheck/addCheckData";
    /**
     * ########业务埋点的网络接口#########
     */
    public static final String APP_DATA_PICK_URL = "https://www.dokit.cn/pointData/addPointData";
    //public static final String APP_DATA_PICK_URL = "http://dokit-test.intra.xiaojukeji.com/pointData/addPointData";
    /**
     * 慢函数操作文档
     */

    public static final String APP_DOCUMENT_URL = "https://xingyun.xiaojukeji.com/docs/dokit/#/TimeProfiler";

    /**
     * 文件同步助手操作文档
     */

    public static final String FILE_MANAGER_DOCUMENT_URL = "https://xingyun.xiaojukeji.com/docs/dokit/#/FileList";
    /**
     * 线上环境
     * app 启动数据埋点
     */
    public static final String APP_START_DATA_PICK_URL = "https://doraemon.xiaojukeji.com/uploadAppData";

    /**
     * 测试环境
     */
    //public static final String APP_START_DATA_PICK_URL = "http://dokit-test.intra.xiaojukeji.com/uploadAppData";

    /**
     * dokit 更多页面接口请求
     */
    public static final String DOKIT_MORE_PAGE_URL = "https://star.xiaojukeji.com/config/get.node?city=-1&areaid=&name=group";
    /**
     * 预发环境
     */
    //public static final String APP_START_DATA_PICK_URL = "http://pre.dokit.cn/uploadAppData";


    private static final int MAX_SIZE = 100;

    private long mStartTime;
    private OnNetworkInfoUpdateListener mOnNetworkInfoUpdateListener;

    private int mPostCount;
    private int mGetCount;
    private int mTotalCount;

    public NetworkManager() {
    }

    public NetworkRecord getRecord(int requestId) {
        for (NetworkRecord record :
                mRecords) {
            if (record.mRequestId == requestId) {
                return record;
            }
        }
        return null;
    }


    private static class Holder {
        private static NetworkManager INSTANCE = new NetworkManager();
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private AtomicBoolean mIsActive = new AtomicBoolean(false);

    /**
     * 这个数据结构要求有序（方便移除最旧的数据），线程安全（网络请求是在子线程内执行，会在子线程内对数据进行查询插入删除操作），方便查找
     * （需要根据requestId，找到对应的record），目前没找到同时满足三个条件的数据结构，暂时先保证前两者，因为限制了大小为MAX_SIZE，查找
     * 的数据量不会很大，直接foreach
     */
    private List<NetworkRecord> mRecords = Collections.synchronizedList(new ArrayList<NetworkRecord>());

    public static NetworkManager get() {
        return NetworkManager.Holder.INSTANCE;
    }

    public void addRecord(int requestId, NetworkRecord record) {
        if (mRecords.size() > MAX_SIZE) {
            mRecords.remove(0);
        }
        if (record.isPostRecord()) {
            mPostCount++;
        } else if (record.isGetRecord()) {
            mGetCount++;
        }
        mTotalCount++;
        mRecords.add(record);

        updateRecord(record, true);
    }

    public void updateRecord(final NetworkRecord record, final boolean add) {
        /**
         * post to main thread
         */
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (DoKitManager.INSTANCE.getCALLBACK() != null && add) {
                    DoKitManager.INSTANCE.getCALLBACK().onNetworkCallBack(record);
                }
                if (mOnNetworkInfoUpdateListener != null) {
                    mOnNetworkInfoUpdateListener.onNetworkInfoUpdate(record, add);
                }
            }
        });
    }

    public void startMonitor() {
        if (mIsActive.get()) {
            return;
        }
        mIsActive.set(true);
        mStartTime = System.currentTimeMillis();
    }

    public void stopMonitor() {
        if (!mIsActive.get()) {
            return;
        }
        mIsActive.set(false);
        mStartTime = 0;
    }

    public static boolean isActive() {
        return get().mIsActive.get();
    }

    public List<NetworkRecord> getRecords() {
        return mRecords;
    }

    public void setOnNetworkInfoUpdateListener(OnNetworkInfoUpdateListener onNetworkInfoUpdateListener) {
        mOnNetworkInfoUpdateListener = onNetworkInfoUpdateListener;
    }

    public long getRunningTime() {
        if (mStartTime == 0) {
            return mStartTime;
        }
        long time = System.currentTimeMillis() - mStartTime;
        return time;
    }

    public long getTotalRequestSize() {
        long totalSize = 0;
        for (NetworkRecord record : mRecords) {
            totalSize += record.requestLength;
        }
        return totalSize;
    }


    public long getTotalSize() {
        long totalSize = 0;
        for (NetworkRecord record : mRecords) {
            totalSize += record.requestLength;
            totalSize += record.responseLength;
        }
        return totalSize;
    }

    public long getTotalResponseSize() {
        long totalSize = 0;
        for (NetworkRecord record : mRecords) {
            totalSize += record.responseLength;
        }
        return totalSize;
    }

    public int getPostCount() {
        return mPostCount;
    }

    public int getGetCount() {
        return mGetCount;
    }

    public int getTotalCount() {
        return mTotalCount;
    }
}
