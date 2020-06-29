package com.dk.android_art

import android.app.Application
import android.util.Log
import com.dk.android_art.utils.SystemUtils

/**
 * @create on 2020/6/20 15:51
 * @description application
 * @author mrdonkey
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("MyApplication", "---onCreate--->processName:${SystemUtils.getProcessName(this)}")
    }
}