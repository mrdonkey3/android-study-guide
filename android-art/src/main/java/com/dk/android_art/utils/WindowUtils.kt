package com.dk.android_art.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.notification.StatusBarNotification
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.dk.android_art.R


/**
 * @create on 2020/7/3 21:56
 * @description window工具类
 * @author mrdonkey
 */
class WindowUtils {
    companion object {
        @JvmStatic
        fun showSoftInputFromWindow(view: View) {
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.requestFocus()
            val im =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.showSoftInput(view, 0)
        }

        fun getScreenMetrics(context: Context): DisplayMetrics? {
            val wm: WindowManager =
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            wm.defaultDisplay.getMetrics(dm)
            return dm
        }

        fun createSystemNotification(context: Context, targetClz: Class<*>) {
            val channelId = "chanelId"
            val intent = Intent(context, targetClz)
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //相当于  context.startActivity(xx)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(channelId, "1", NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
            }
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("hello world")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("systemNotification")
                .setContentText("这是系统的通知栏！")
                .setContentIntent(pendingIntent)
                .build()
            notification.flags = Notification.FLAG_AUTO_CANCEL
            notificationManager.notify(4, notification)
        }


        fun createCustomNotification(context: Context, targetClz: Class<*>) {
            val channelId = "chanelId"
            val intent = Intent(context, targetClz)
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //相当于  context.startActivity(xx)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//适配android8.0
                val channel =
                    NotificationChannel(channelId, "1", NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
            }
            val remoteViews = RemoteViews(context.packageName, R.layout.widget)
            remoteViews.setImageViewResource(R.id.iv_widget, R.drawable.ic_launcher)
            remoteViews.setOnClickPendingIntent(R.id.iv_widget, pendingIntent)
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("hello world")
                .setWhen(System.currentTimeMillis())
//                .setFlag(Notification.FLAG_AUTO_CANCEL, true)
                .setContent(remoteViews)
                .setContentIntent(pendingIntent)
                .build()
            notification.flags = Notification.FLAG_AUTO_CANCEL//点击时候消失
            notificationManager.notify(5, notification)
        }

    }

}