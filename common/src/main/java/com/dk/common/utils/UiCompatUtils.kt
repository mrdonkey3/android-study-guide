package com.dk.common.utils

import android.content.Context

/**
 * @create on 2020/6/6 12:05
 * @description ui 相关的兼容工具类
 * @author mrdonkey
 */
class UiCompatUtils {
    companion object {
        @JvmStatic
        fun dp2px(context: Context, dipValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return dipValue.times(scale).plus(0.5f).toInt()
        }
    }
}