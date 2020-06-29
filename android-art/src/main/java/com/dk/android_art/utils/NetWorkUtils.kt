package com.dk.android_art.utils

import android.content.Context
import android.net.wifi.WifiManager
import java.io.BufferedReader
import java.io.FileReader
import java.net.InetAddress
import java.util.concurrent.ThreadPoolExecutor
import java.util.regex.Pattern

/**
 * @create on 2020/6/16 10:07
 * @description 网络相关工具类
 * @author mrdonkey
 */
class NetWorkUtils private constructor() {
    companion object {

        @JvmStatic
        fun getConnectionDeviceIp(context: Context) {
            kotlin.runCatching {
                val ip = arrayListOf<String>()
//                val bufferedReader = BufferedReader(FileReader("/proc/net/arp"))//todo  android10异常 无访问权限
//                while (bufferedReader.readLine()?.apply { line = this } != null) {
                val pattern = Regex("([0-9]{1,3}\\.){3}[0-9]{1,3}")
                val matcher = pattern.toPattern().matcher("192.168.43.203 ")
                println("--->connection ip size ：${matcher.groupCount()}")
                while (matcher.find()) {
                    println("--->connection ip ：${matcher.group()}")
                }

//            }
            }.onFailure {
                println("-error--->${it.printStackTrace()}")
            }
        }

        @JvmStatic
        fun ping(ipAddress: String): Boolean {
            var address: InetAddress
            kotlin.runCatching {
                address = InetAddress.getByName(ipAddress)
                return address.isReachable(3000)//返回true代表ping通，反之false
            }
            return false
        }
    }
}