package com.dk.android_art.customview.checkedview

import android.text.InputFilter
import android.text.Spanned

/**
 * @create on 2020/6/4 12:47
 * @description 最大最小值限制
 * @author mrdonkey
 */
class InputFilterMinMax(var min: Int, var max: Int) : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        try {
            //[source] 现在输入的文字 [dest] 原先输入的
            val input = (dest.toString() + source.toString()).toInt()
            if (isInRange(min, max, input)) {
                return null//如果在范围内，不过滤它
            }
        } catch (e: Exception) {
            //ingnore
        }
        return ""//输入的都返回空
    }


    private fun isInRange(min: Int, max: Int, input: Int): Boolean {
        return if (max > min) {
            input in min..max
        } else {
            input in max..min
        }
    }
}