package com.teligen.litedevice.ui.widget

import com.dk.android_art.customview.checkedview.CheckedTextView
import com.dk.android_art.customview.checkedview.MultiCheckedLayout

/**
 *@ClassName CheckedViewFactory
 *@Description 生成CheckedView的抽象工厂
 *@Date 2020/6/2 16:25
 *@Create by linhong
 */
class CheckedViewFactory {
    companion object {
        fun create(type: Int): Class<out CheckedView> {
            return when (MultiCheckedLayout.CheckedViewType.values()[type]) {
                MultiCheckedLayout.CheckedViewType.TEXT -> {
                    CheckedTextView::class.java
                }
                MultiCheckedLayout.CheckedViewType.EDIT -> {
                    CheckedEditTextView::class.java
                }
            }
        }
    }
}