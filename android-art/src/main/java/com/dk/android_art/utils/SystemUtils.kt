package com.dk.android_art.utils

import android.app.ActivityManager
import android.content.Context

/**
 * @create on 2020/6/20 15:57
 * @description 系统工具类
 * @author mrdonkey
 */
class SystemUtils private constructor() {
    companion object {
        @JvmStatic
        fun getProcessName(context: Context): String? {
            val myPid = android.os.Process.myPid()
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningAppProcesses = activityManager.runningAppProcesses
            return runningAppProcesses?.find { it.pid == myPid }?.processName
        }
    }
}