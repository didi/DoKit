package com.didichuxing.doraemonkit.kit.blockmonitor.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Debug
import android.os.Looper
import android.text.TextUtils
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.DokitConstant.APP_HEALTH_RUNNING
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.blockmonitor.BlockMonitorFragment
import com.didichuxing.doraemonkit.kit.blockmonitor.bean.BlockInfo
import com.didichuxing.doraemonkit.kit.blockmonitor.core.BlockCanaryUtils.concernStackString
import com.didichuxing.doraemonkit.kit.core.UniversalActivity
import com.didichuxing.doraemonkit.util.LogHelper.e
import com.didichuxing.doraemonkit.util.LogHelper.i
import com.didichuxing.doraemonkit.util.NotificationUtils
import com.didichuxing.doraemonkit.util.NotificationUtils.cancelNotification
import com.didichuxing.doraemonkit.util.NotificationUtils.setInfoNotification
import java.util.*

/**
 * @desc: 卡顿检测管理类
 */
class BlockMonitorManager private constructor() {

    var isRunning = false
        private set
    private var mMonitorCore: MonitorCore? = null
    private var mContext: Context? = null
    private val mBlockInfoList = Collections.synchronizedList(ArrayList<BlockInfo>())
    private var mOnBlockInfoUpdateListener: OnBlockInfoUpdateListener? = null
    fun start() {
        if (isRunning) {
            i(TAG, "start when manager is running")
            return
        }
        if (DoraemonKit.APPLICATION == null) {
            e(TAG, "start fail, context is null")
            return
        }
        // 卡顿检测和跳转耗时统计都使用了Printer的方式，无法同时工作
        //todo stop TimeCounterManager
//        TimeCounterManager.get().stop();
        mContext = DoraemonKit.APPLICATION!!.applicationContext
        if (mMonitorCore == null) {
            mMonitorCore = MonitorCore()
        }
        isRunning = true
        Looper.getMainLooper().setMessageLogging(mMonitorCore)
    }

    fun stop() {
        if (!isRunning) {
            i(TAG, "stop when manager is not running")
            return
        }
        Looper.getMainLooper().setMessageLogging(null)
        if (mMonitorCore != null) {
            mMonitorCore!!.shutDown()
            mMonitorCore = null
        }
        cancelNotification(mContext!!, NotificationUtils.ID_SHOW_BLOCK_NOTIFICATION)
        isRunning = false
        mContext = null
    }

    fun setOnBlockInfoUpdateListener(onBlockInfoUpdateListener: OnBlockInfoUpdateListener?) {
        mOnBlockInfoUpdateListener = onBlockInfoUpdateListener
    }

    /**
     * 动态添加卡顿信息到appHealth
     *
     * @param blockInfo
     */
    private fun addBlockInfoInAppHealth(blockInfo: BlockInfo) {
        //todo addBlockInfoInAppHealth
    }

    /**
     * 通知卡顿
     *
     * @param blockInfo
     */
    fun notifyBlockEvent(blockInfo: BlockInfo) {
        blockInfo.concernStackString = concernStackString(mContext!!, blockInfo)
        blockInfo.time = System.currentTimeMillis()
        if (!TextUtils.isEmpty(blockInfo.concernStackString)) {
            //卡顿 debug模式下会造成卡顿
            if (APP_HEALTH_RUNNING && !Debug.isDebuggerConnected()) {
                addBlockInfoInAppHealth(blockInfo)
            }
            showNotification(blockInfo)
            if (mBlockInfoList.size > MAX_SIZE) {
                mBlockInfoList.removeAt(0)
            }
            mBlockInfoList.add(blockInfo)
            mOnBlockInfoUpdateListener?.onBlockInfoUpdate(blockInfo)
        }
    }

    private fun showNotification(info: BlockInfo) {
        val contentTitle = mContext!!.getString(R.string.dk_block_class_has_blocked, info.timeStart)
        val contentText = mContext?.getString(R.string.dk_block_notification_message)
        val intent = Intent(mContext, UniversalActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_BLOCK_MONITOR)
        intent.putExtra(BlockMonitorFragment.KEY_JUMP_TO_LIST, true)
        val pendingIntent = PendingIntent.getActivity(mContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mContext?.let {
            setInfoNotification(it, NotificationUtils.ID_SHOW_BLOCK_NOTIFICATION,
                    contentTitle, contentText, contentText, pendingIntent)
        }

    }

    val blockInfoList: List<BlockInfo>
        get() = mBlockInfoList

    companion object {
        private const val TAG = "BlockMonitorManager"
        private const val MAX_SIZE = 50
        @JvmStatic
        val instance: BlockMonitorManager = BlockMonitorManager()
    }
}