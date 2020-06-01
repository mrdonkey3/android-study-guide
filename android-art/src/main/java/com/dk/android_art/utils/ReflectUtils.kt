package com.dk.android_art.utils

import android.graphics.PointF
import android.view.View
import java.lang.reflect.Method

/**
 * @create on 2020/5/29 23:42
 * @description 反射工具类
 * @author mrdonkey
 */
class ReflectUtils {
    companion object {
        /**
         * 获取指定Class的方法
         * [targetClz] 目标类型
         * [methodName] 目标方法
         * [params] 方法参数Class类型 可传空
         */
        @JvmStatic
        fun getTargetMethod(
            targetClz: Class<*>,
            methodName: String,
            vararg paramsClz: Class<out Any>?
        ): Method? {
            try {
                return targetClz.getDeclaredMethod(methodName, *paramsClz)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        /**
         * 调用指定的方法
         * [invoker] 方法调用者，是具体的实例。必须和拥有method 方法的 类 有关联（invoker是该类的派生类等）
         * [method] 调用的方法
         * [params] 方法入参
         * [T] 返回值类型
         */
        @Suppress("UNCHECKED_CAST")
        fun <T> invokeTargetMethod(invoker: Any, method: Method?, vararg params: Any?): T? {
            try {
                method?.isAccessible=true
                method?.invoke(invoker, *params)?.apply {
                    return this as T
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}