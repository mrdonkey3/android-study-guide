package com.dk.android_art.utils

import android.content.Context
import android.view.View
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
    }
}