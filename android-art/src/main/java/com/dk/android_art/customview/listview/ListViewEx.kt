package com.dk.android_art.customview.listview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import android.widget.ListView
import com.dk.android_art.customview.scrollview.HorizontalScrollViewEx2
import kotlin.math.absoluteValue

/**
 * @create on 2020/7/5 00:00
 * @description 内部拦截法：解决滑动冲突
 * @author mrdonkey
 */
class ListViewEx(context: Context, attrs: AttributeSet) : ListView(context, attrs) {
    private val TAG = "ListViewEx"
    private var mHorizontalScrollViewEx2: HorizontalScrollViewEx2? = null

    //分别记录上次的滑动坐标
    private var mLastX = 0f
    private var mLastY = 0f

    fun setHorizontalScrollViewEx2(m: HorizontalScrollViewEx2) {
        this.mHorizontalScrollViewEx2 = m
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.x//相对于view的x坐标
        val y = ev.y
        when (ev.action.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                mHorizontalScrollViewEx2?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = x - mLastX
                val deltaY = y - mLastY
                if (deltaX.absoluteValue > deltaY.absoluteValue)//如果是水平滑动则恢复父类的拦截事件
                    mHorizontalScrollViewEx2?.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        mLastX = x
        mLastY = y
        return super.dispatchTouchEvent(ev)
    }
}