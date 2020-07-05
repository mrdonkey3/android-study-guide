package com.dk.android_art.customview.scrollview

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView

/**
 * @create on 2020/7/4 12:30
 * @description 跟手滑动的textView
 * @author mrdonkey
 */
class SildeWithHandTextView(context: Context, attributeSet: AttributeSet) :
    AppCompatTextView(context, attributeSet), GestureDetector.OnGestureListener {
    var mLastX: Float = 0f
    var mLastY: Float = 0f
    private val mGestureDetector = GestureDetector(context, this)

    init{
        mGestureDetector.setIsLongpressEnabled(false)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val rawX = event.rawX//点击屏幕的绝对坐标
        val rawY = event.rawY
        when (event.action.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_MOVE-> {
                slideWithHand(rawX, rawY)
            }
            else -> {
                //do nothing
            }
        }
        mLastX = rawX
        mLastY = rawY
        return true
//        return mGestureDetector.onTouchEvent(event)
    }

    private fun slideWithHand(rawX: Float, rawY: Float) {
        val deltaX = rawX - mLastX//位移
        val deltaY = rawY - mLastY
        println("($mLastX,$mLastY)--move-->deltaX:$deltaX deltaY:$deltaY-->($rawX,$rawY)")
        println("当前view的绝对坐标:($translationX,$translationY)")
        val translationX = translationX + deltaX//相对坐标 相对原来的view平移多少
        val translationY = translationY + deltaX
        ObjectAnimator.ofFloat(this, "translationX", translationX).setDuration(1).start()
        ObjectAnimator.ofFloat(this, "translationY", translationY).setDuration(1).start()
    }

    override fun onShowPress(e: MotionEvent?) {
        println("--->onShowPress")
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        println("--->onSingleTapUp")
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        println("--->onDown")
        return false
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        println("--->onFling")
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        println("--->onScroll")
        val action1 = e1?.action?.and(MotionEvent.ACTION_MASK)
        val action2 = e2?.action?.and(MotionEvent.ACTION_MASK)
        println("--->($action1,$action2) --->($distanceX,$distanceY)")
//
//        ObjectAnimator.ofFloat(this, "translationX", translationX).setDuration(1).start()
//        ObjectAnimator.ofFloat(this, "translationY", translationY).setDuration(1).start()
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        println("--->onLongPress")

    }

}