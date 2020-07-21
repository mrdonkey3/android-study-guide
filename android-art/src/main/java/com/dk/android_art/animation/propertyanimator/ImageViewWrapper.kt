package com.dk.android_art.animation.propertyanimator

import android.view.View

/**
 * @create on 2020/7/21 23:49
 * @description ImageView包装类，为了实现属性动画当不支持某个set、get方法时的一个解决方式
 * @author mrdonkey
 */
class ImageViewWrapper(val mTarget: View) {

    fun setWidth(width: Int) {
        mTarget.layoutParams.width = width
        mTarget.requestLayout()
    }

    fun getWidth(): Int {
        return mTarget.layoutParams.width
    }
}