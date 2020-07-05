package com.dk.android_art.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager


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
            val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val dm = DisplayMetrics()
            wm.defaultDisplay.getMetrics(dm)
            return dm
        }
    }

}