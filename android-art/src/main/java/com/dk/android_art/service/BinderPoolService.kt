package com.dk.android_art.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.dk.android_art.aidl.binderpool.BinderPool

/**
 * @create on 2020/6/25 13:34
 * @description binder连接池，将每一个业务模块的binder请求统一转发到远程的service去执行，从而避免重复创建service的过程。
 * 简单的来说就是实现，一个service管理多个aidl
 * @author mrdonkey
 */
class BinderPoolService : Service() {
    private val TAG = "BinderPoolService"
    private val mBinderPool = BinderPool.BinderPoolImpl()

    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "--->onCreate")
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.e(TAG, "--->onBind")
        return mBinderPool
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "--->onDestroy")
    }
}