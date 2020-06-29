package com.didichuxing.doraemonkit.kit.network

import android.os.Handler
import android.os.Looper
import com.didichuxing.doraemonkit.kit.network.okhttp.bean.NetworkRecord
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class NetworkManager {
    private var mOnNetworkInfoUpdateListener: OnNetworkInfoUpdateListener? = null
    private val mIsActive = AtomicBoolean(false)
    private var mStartTime: Long = 0
    var postCount = 0
    var getCount = 0
    var totalCount = 0
    private val MAX_SIZE = 100

    private object Holder {
        val INSTANCE = NetworkManager()
    }


    private val mHandler = Handler(Looper.getMainLooper())
    /**
     * 这个数据结构要求有序（方便移除最旧的数据），线程安全（网络请求是在子线程内执行，会在子线程内对数据进行查询插入删除操作），方便查找
     * （需要根据requestId，找到对应的record），目前没找到同时满足三个条件的数据结构，暂时先保证前两者，因为限制了大小为MAX_SIZE，查找
     * 的数据量不会很大，直接foreach
     */
    val records = Collections.synchronizedList(ArrayList<NetworkRecord>())
    val totalSize: Long
        get() {
            var totalSize: Long = 0
            for (record in records) {
                totalSize += record.requestLength
                totalSize += record.responseLength
            }
            return totalSize
        }

    fun startMonitor() {
        if (mIsActive.get()) {
            return
        }
        mIsActive.set(true)
        mStartTime = System.currentTimeMillis()
    }

    fun stopMonitor() {
        if (!mIsActive.get()) {
            return
        }
        mIsActive.set(false)
        mStartTime = 0
    }

    fun setOnNetworkInfoUpdateListener(onNetworkInfoUpdateListener: OnNetworkInfoUpdateListener?) {
        mOnNetworkInfoUpdateListener = onNetworkInfoUpdateListener
    }

    val runningTime: Long
        get() = if (mStartTime == 0L) {
            mStartTime
        } else System.currentTimeMillis() - mStartTime

    val totalRequestSize: Long
        get() {
            var totalSize: Long = 0
            for (record in records) {
                totalSize += record.requestLength
            }
            return totalSize
        }

    val totalResponseSize: Long
        get() {
            var totalSize: Long = 0
            for (record in records) {
                totalSize += record.responseLength
            }
            return totalSize
        }

    fun addRecord(requestId: Int, record: NetworkRecord) {
        if (records.size >MAX_SIZE) {
            records.removeAt(0)
        }
        if (record.isPostRecord) {
            postCount++
        } else if (record.isGetRecord) {
            getCount++
        }
        totalCount++
        records.add(record)
        updateRecord(record, true)
    }

    fun updateRecord(record: NetworkRecord?, add: Boolean) {
        if (mOnNetworkInfoUpdateListener != null) {
            /**
             * post to main thread
             */
            mHandler.post(Runnable {
                if (mOnNetworkInfoUpdateListener != null) {
                    mOnNetworkInfoUpdateListener!!.onNetworkInfoUpdate(record, add)
                }
            })
        }
    }
    companion object {
        val instance = Holder.INSTANCE

        val isActive: Boolean
            get() = instance.mIsActive.get()
    }
}