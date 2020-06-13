package com.didichuxing.doraemonkit.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import com.didichuxing.doraemonkit.R
import java.util.*

/**
 * 管理notification
 *
 * @author vinda
 * @since 15/5/22
 */
object NotificationUtils {
    private const val ID_HIGH_CHANNEL = "channel_1_oncar"
    private const val NAME_HIGH_CHANNEL = "channel_1_name_oncar"
    private const val ID_LOW_CHANNEL = "channel_low_onecar"
    private const val NAME_LOW_CHANNEL = "channel_name_low_onecar"
    private var sNotificationManager: NotificationManager? = null
    const val ID_SHOW_BLOCK_NOTIFICATION = 1001

    /**
     * 文本消息
     *
     * @param notifyId    消息ID
     * @param smallIconId 小图标
     * @param title       标题
     * @param summary     内容
     */
    fun setMessageNotification(context: Context, notifyId: Int, smallIconId: Int, title: CharSequence?, summary: CharSequence?) {
        setMessageNotification(context, notifyId, smallIconId, title, summary, null)
    }

    /**
     * 文本消息
     *
     * @param notifyId 消息ID
     * @param title    标题
     * @param summary  内容
     * @param ticker   出现消息时状态栏的提示文字
     */
    fun setMessageNotification(context: Context, notifyId: Int, smallIconId: Int, title: CharSequence?, summary: CharSequence?, ticker: CharSequence?) {
        setMessageNotification(context, notifyId, smallIconId, title, summary, ticker, null)
    }

    /**
     * 文本消息
     *
     * @param notifyId      消息ID
     * @param title         标题
     * @param summary       内容
     * @param ticker        出现消息时状态栏的提示文字
     * @param pendingIntent 点击后的intent
     */
    fun setMessageNotification(context: Context, notifyId: Int, smallIconId: Int, title: CharSequence?, summary: CharSequence?, ticker: CharSequence?, pendingIntent: PendingIntent?) {
        val builder: NotificationCompat.Builder
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, ID_HIGH_CHANNEL)
        } else {
            NotificationCompat.Builder(context)
        }
        builder.setSmallIcon(smallIconId)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.dk_doraemon))
                .setContentTitle(title)
                .setContentText(summary)
                .setAutoCancel(true)
                .setProgress(0, 0, false) // Removes the progress bar
        if (!TextUtils.isEmpty(ticker)) {
            builder.setTicker(ticker)
        }
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        } else {
            builder.setContentIntent(PendingIntent.getBroadcast(context, 0, Intent(), PendingIntent.FLAG_UPDATE_CURRENT))
        }
        val manager = createNotificationManager(context)
        manager?.notify(notifyId, builder.build())
    }

    /**
     * 显示消息中心的消息
     *
     * @param notifyId      消息ID
     * @param title         标题
     * @param summary       内容
     * @param ticker        出现消息时状态栏的提示文字
     * @param pendingIntent 点击后的intent
     */
    @JvmStatic
    fun setInfoNotification(context: Context, notifyId: Int, title: CharSequence?, summary: CharSequence?, ticker: CharSequence?, pendingIntent: PendingIntent?) {
        val builder: NotificationCompat.Builder
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, ID_HIGH_CHANNEL)
        } else {
            NotificationCompat.Builder(context)
        }
        builder.setSmallIcon(R.mipmap.dk_doraemon)
                .setContentTitle(title)
                .setContentText(summary)
                .setAutoCancel(true)
                .setProgress(0, 0, false) // Removes the progress bar
        if (!TextUtils.isEmpty(ticker)) {
            builder.setTicker(ticker)
        }
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        } else {
            builder.setContentIntent(PendingIntent.getBroadcast(context, 0, Intent(), PendingIntent.FLAG_UPDATE_CURRENT))
        }
        val manager = createNotificationManager(context)
        manager?.notify(notifyId, builder.build())
    }

    /**
     * 设置进度通知
     *
     * @param notifyId 消息ID
     * @param title    标题
     * @param progress 进度（0-100）
     */
    fun setProgressNotification(context: Context, notifyId: Int, title: CharSequence?, progress: Int) {
        setProgressNotification(context, notifyId, title, null, progress)
    }

    /**
     * 设置下载进度通知
     *
     * @param notifyId 消息ID
     * @param title    标题
     * @param ticker   出现消息时状态栏的提示文字
     * @param progress 进度（0-100）
     */
    fun setProgressNotification(context: Context, notifyId: Int, title: CharSequence?, ticker: CharSequence?, progress: Int) {
        setProgressNotification(context, notifyId, title, ticker, progress, null)
    }

    /**
     * 设置下载进度通知
     *
     * @param notifyId      消息ID
     * @param title         标题
     * @param ticker        出现消息时状态栏的提示文字
     * @param progress      进度（0-100）
     * @param pendingIntent 点击后的intent
     */
    fun setProgressNotification(context: Context, notifyId: Int, title: CharSequence?, ticker: CharSequence?, progress: Int, pendingIntent: PendingIntent?) {
        val builder: NotificationCompat.Builder
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, ID_HIGH_CHANNEL)
        } else {
            NotificationCompat.Builder(context)
        }
        builder.setSmallIcon(android.R.drawable.stat_sys_download)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.dk_doraemon))
                .setContentTitle(title)
                .setProgress(100, progress, progress == 0)
                .setOngoing(progress < 100)
                .setAutoCancel(progress == 100)
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        } else {
            builder.setContentIntent(PendingIntent.getBroadcast(context, 0, Intent(), PendingIntent.FLAG_UPDATE_CURRENT))
        }
        if (!TextUtils.isEmpty(ticker)) {
            builder.setTicker(ticker)
        }
        val manager = createNotificationManager(context)
        manager?.notify(notifyId, builder.build())
    }

    /**
     * 取消通知
     *
     * @param notifyId 通知ID
     */
    fun cancelNotification(context: Context, notifyId: Int) {
        val manager = createNotificationManager(context)
        manager?.cancel(notifyId)
    }

    /**
     * 取消所有通知
     */
    fun cancelNotification(context: Context) {
        val manager = createNotificationManager(context)
        manager?.cancelAll()
    }

    private fun createNotificationManager(context: Context): NotificationManager? {
        if (sNotificationManager != null) {
            return sNotificationManager
        }
        sNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 适配>=7.0手机通知栏显示问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationHightChannel = NotificationChannel(ID_HIGH_CHANNEL, NAME_HIGH_CHANNEL, NotificationManager.IMPORTANCE_HIGH)
            val notificationLowChannel = NotificationChannel(ID_LOW_CHANNEL, NAME_LOW_CHANNEL, NotificationManager.IMPORTANCE_LOW)
            val channelList: MutableList<NotificationChannel> = ArrayList()
            channelList.add(notificationLowChannel)
            channelList.add(notificationHightChannel)
            sNotificationManager?.createNotificationChannels(channelList)
        }
        return sNotificationManager
    }
}