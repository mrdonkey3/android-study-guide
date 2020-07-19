package com.dk.android_art.appwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.RemoteViews
import com.dk.android_art.R
import com.dk.android_art.utils.SystemUtils

/**
 * @create on 2020/7/19 09:51
 * @description 定义小部件的实现类,本质也是一个broadcastReceiver，通过广播去修改ui
 * @author mrdonkey
 */
class MyAppWidgetProvider : AppWidgetProvider() {
    companion object {
        const val CLICK_ACTION = "com.dk.android_art.action_CLICK"
        const val TAG = "MyAppWidgetProvider"

    }


    /**
     *
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        Log.i(TAG, "onReceive: action = ${intent.action}")
        //判断是否是自己的action
        when (intent.action) {
            CLICK_ACTION -> {
                //让图标旋转
                Thread {
                    val bitmap =
                        BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher)
                    val awManager = AppWidgetManager.getInstance(context)
                    for (i in 0 until 37) {
                        val degree = i.times(10) % 360.0f//角度
                        val remoteViews = RemoteViews(context.packageName, R.layout.widget)
                        remoteViews.setImageViewBitmap(R.id.iv_widget, rotateBitmap(bitmap, degree))
                        //给小部件设置单击效果
                        val intentClick = Intent()
                        intentClick.setClass(context,MyAppWidgetProvider::class.java)//要指定谁使用这个intent
                        intentClick.action = CLICK_ACTION
                        val pendingIntent = PendingIntent.getBroadcast(context, 0, intentClick, 0)
                        remoteViews.setOnClickPendingIntent(R.id.iv_widget, pendingIntent)
                        //更新小部件
                        awManager.updateAppWidget(
                            ComponentName(
                                context,
                                MyAppWidgetProvider::class.java
                            ), remoteViews
                        )
                        SystemClock.sleep(30)
                    }

                }.start()
                //打开支付宝
                SystemUtils.startAliPay(context)
            }
            else -> {
            }
        }
    }

    /**
     * 每次小部件更新时的回调
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val size = appWidgetIds.size
        Log.i(TAG, "--->onUpdate   size:$size")
        for (i in 0 until size) {
            val appWidgetId = appWidgetIds[i]
            onWidgetUpdate(context, appWidgetManager, appWidgetId)
        }
    }
    /**
     * 桌面小部件更新
     */

    private fun onWidgetUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        Log.i(TAG, "--->onWidgetUpdate appWidgetId：$appWidgetId")
        val remoteViews = RemoteViews(context.packageName, R.layout.widget)
        //给小部件设置单击事件发送intent广播
        val intentClick = Intent()
        intentClick.setClass(context,MyAppWidgetProvider::class.java)
        intentClick.action = CLICK_ACTION
        //效果相当于  context.sendBroadcast(intent)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intentClick, 0)
        remoteViews.setOnClickPendingIntent(R.id.iv_widget, pendingIntent)
        //更新小部件
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        Log.i(TAG, "--->onEnabled")
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        Log.i(TAG, "--->onDisabled")
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        super.onDeleted(context, appWidgetIds)
        Log.i(TAG, "--->onDeleted")
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
        Log.i(TAG, "--->onRestored")
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        Log.i(TAG, "--->onAppWidgetOptionsChanged")
    }



    /**
     * 旋转bitmap
     */
    private fun rotateBitmap(bitmap: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.reset()
        matrix.setRotate(degree)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

}