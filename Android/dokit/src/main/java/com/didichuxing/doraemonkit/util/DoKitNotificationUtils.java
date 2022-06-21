package com.didichuxing.doraemonkit.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;

import com.didichuxing.doraemonkit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理notification
 *
 * @author vinda
 * @since 15/5/22
 */
public class DoKitNotificationUtils {
    private static final String ID_HIGH_CHANNEL = "channel_1_oncar";
    private static final String NAME_HIGH_CHANNEL = "channel_1_name_oncar";

    private static final String ID_LOW_CHANNEL = "channel_low_onecar";
    private static final String NAME_LOW_CHANNEL = "channel_name_low_onecar";

    private static NotificationManager sNotificationManager;
    public static final int ID_SHOW_BLOCK_NOTIFICATION = 1001;


    /**
     * 文本消息
     *
     * @param notifyId    消息ID
     * @param smallIconId 小图标
     * @param title       标题
     * @param summary     内容
     */
    public static void setMessageNotification(Context context, int notifyId, int smallIconId, CharSequence title, CharSequence summary) {
        setMessageNotification(context, notifyId, smallIconId, title, summary, null);
    }

    /**
     * 文本消息
     *
     * @param notifyId 消息ID
     * @param title    标题
     * @param summary  内容
     * @param ticker   出现消息时状态栏的提示文字
     */
    public static void setMessageNotification(Context context, int notifyId, int smallIconId, CharSequence title, CharSequence summary, CharSequence ticker) {
        setMessageNotification(context, notifyId, smallIconId, title, summary, ticker, null);
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
    public static void setMessageNotification(Context context, int notifyId, int smallIconId, CharSequence title, CharSequence summary, CharSequence ticker, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, ID_HIGH_CHANNEL);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        builder.setSmallIcon(smallIconId)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.dk_doraemon))
                .setContentTitle(title)
                .setContentText(summary)
                .setAutoCancel(true)
                .setProgress(0, 0, false);// Removes the progress bar
        if (!TextUtils.isEmpty(ticker)) {
            builder.setTicker(ticker);
        }
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        } else {
            builder.setContentIntent(createPendingIntent(context));
        }
        NotificationManager manager = createNotificationManager(context);
        manager.notify(notifyId, builder.build());
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
    public static void setInfoNotification(Context context, int notifyId, CharSequence title, CharSequence summary, CharSequence ticker, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, ID_HIGH_CHANNEL);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        builder.setSmallIcon(R.mipmap.dk_doraemon)
                .setContentTitle(title)
                .setContentText(summary)
                .setAutoCancel(true)
                .setProgress(0, 0, false);// Removes the progress bar
        if (!TextUtils.isEmpty(ticker)) {
            builder.setTicker(ticker);
        }
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        } else {
            builder.setContentIntent(createPendingIntent(context));
        }
        NotificationManager manager = createNotificationManager(context);
        manager.notify(notifyId, builder.build());
    }

    /**
     * 设置进度通知
     *
     * @param notifyId 消息ID
     * @param title    标题
     * @param progress 进度（0-100）
     */
    public static void setProgressNotification(Context context, int notifyId, CharSequence title, int progress) {
        setProgressNotification(context, notifyId, title, null, progress);
    }

    /**
     * 设置下载进度通知
     *
     * @param notifyId 消息ID
     * @param title    标题
     * @param ticker   出现消息时状态栏的提示文字
     * @param progress 进度（0-100）
     */
    public static void setProgressNotification(Context context, int notifyId, CharSequence title, CharSequence ticker, int progress) {
        setProgressNotification(context, notifyId, title, ticker, progress, null);
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
    public static void setProgressNotification(Context context, int notifyId, CharSequence title, CharSequence ticker, int progress, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(context, ID_HIGH_CHANNEL);
        } else {
            builder = new NotificationCompat.Builder(context);
        }
        builder.setSmallIcon(android.R.drawable.stat_sys_download)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.dk_doraemon))
                .setContentTitle(title)
                .setProgress(100, progress, progress == 0)
                .setOngoing(progress < 100)
                .setAutoCancel(progress == 100);
        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        } else {
            builder.setContentIntent(createPendingIntent(context));
        }
        if (!TextUtils.isEmpty(ticker)) {
            builder.setTicker(ticker);
        }
        NotificationManager manager = createNotificationManager(context);
        manager.notify(notifyId, builder.build());
    }

    /**
     * 取消通知
     *
     * @param notifyId 通知ID
     */
    public static void cancelNotification(Context context, int notifyId) {
        NotificationManager manager = createNotificationManager(context);
        manager.cancel(notifyId);
    }


    /**
     * 取消所有通知
     */
    public static void cancelNotification(Context context) {
        NotificationManager manager = createNotificationManager(context);
        manager.cancelAll();
    }

    private static NotificationManager createNotificationManager(Context context) {
        if (sNotificationManager != null) {
            return sNotificationManager;
        }
        sNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 适配>=7.0手机通知栏显示问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationHightChannel = new NotificationChannel(ID_HIGH_CHANNEL, NAME_HIGH_CHANNEL, NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel notificationLowChannel = new NotificationChannel(ID_LOW_CHANNEL, NAME_LOW_CHANNEL, NotificationManager.IMPORTANCE_LOW);
            List<NotificationChannel> channelList = new ArrayList<>();
            channelList.add(notificationLowChannel);
            channelList.add(notificationHightChannel);
            sNotificationManager.createNotificationChannels(channelList);
        }
        return sNotificationManager;
    }

    private static PendingIntent createPendingIntent(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return PendingIntent.getBroadcast(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            return PendingIntent.getBroadcast(context, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }
}
