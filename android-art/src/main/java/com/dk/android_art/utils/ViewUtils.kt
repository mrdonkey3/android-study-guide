package com.dk.android_art.utils

import android.view.View

/**
 * @create on 2020/7/6 22:11
 * @description view相关的工具类
 * @author mrdonkey
 */
class ViewUtils {
    companion object {
        /**
         * [x]与[y]都必须是绝对左边 event.getRawX / event.getRowY
         */
        @JvmStatic
        fun isTouchPointInView(view: View,x: Float, y: Float): Boolean {
            val location = IntArray(2)
            view.getLocationOnScreen(location)//计算view的左上角坐标，并返回给location
            val left = location[0]
            val top = location[1]
            val right = left.plus(view.measuredWidth)
            val bottom = right.plus(view.measuredHeight)
            val clickable = view.isClickable
            //指定左上角和右下角的坐标确定view的矩形区域
            //判断（x,y）是否在view的矩形区域内
            return  y >= top && y <= bottom && x >= left && x <= right
        }
    }
}