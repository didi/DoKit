package com.didichuxing.doraemonkit.kit.colorpick

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.UniversalActivity

/**
 * 适配Android o。
 * 如果targetSdkVersion >=29 并且当前系统版本>= 26的时候必须设置前台service截屏。
 * @author Donald Yan
 * @date 2020/6/16
 */
class ScreenRecorderService : Service(){

    override fun onCreate() {
        super.onCreate()
        createNotification()
    }

    private fun createNotification() {
        val builder = Notification.Builder(applicationContext)
                .setContentIntent(PendingIntent.getActivity(this, 0, Intent(this, UniversalActivity::class.java), 0))
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.dk_doraemon))
                .setSmallIcon(R.mipmap.dk_doraemon)
                .setContentText("Dokit屏幕取色器前台服务")
                .setWhen(System.currentTimeMillis())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id")
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notification.defaults = Notification.DEFAULT_SOUND
        startForeground(110, notification)

    }

    companion object {

        fun bindService(context: Context, connection: ServiceConnection) {
            context.bindService(Intent(context, ScreenRecorderService::class.java), connection, BIND_AUTO_CREATE)
        }

        fun unbindService(context: Context, connection: ServiceConnection) {
            context.unbindService(connection)
        }
    }


    override fun onBind(intent: Intent?): IBinder? {
        return Binder()
    }
}