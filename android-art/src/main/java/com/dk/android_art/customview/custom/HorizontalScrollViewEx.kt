package com.dk.android_art.customview.custom

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

/**
 * @create on 2020/7/4 22:42
 * @description 类似viewPager的控件，支持水平滑动；内部的子元素竖直滑动时，就容易有滑动冲突；
 * 该控件解决水平滑动与垂直方向滑动冲突问题（外部拦截法）
 * @author mrdonkey
 */
class HorizontalScrollViewEx(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {
    private val TAG = "HorizontalScrollViewEx"

    private var mChildrenSize: Int = 0
    private var mChildWidth: Int = 0
    private var mChildIndex: Int = 0

    //分别记录上次的滑动坐标
    private var mLastX = 0f
    private var mLastY = 0f

    //分别记录上次的滑动左边(onInterceptTouchEvent)
    private var mLastXIntercept = 0f
    private var mLastYIntercept = 0f

    private val mScroller: Scroller = Scroller(context)//弹性滑动
    private val mVelocityTracker = VelocityTracker.obtain()//速度追踪器


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercepted = false
        val x = ev.x//相对于view的x坐标
        val y = ev.y
        when (ev.action.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                intercepted = false
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                    intercepted = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = x - mLastXIntercept
                val deltaY = y - mLastYIntercept
                intercepted = deltaX.absoluteValue > deltaY.absoluteValue
            }
            MotionEvent.ACTION_UP -> {
                intercepted = false
            }
            else -> {
            }
        }
        Log.e(TAG, "intercept-->$intercepted")
        mLastX = x
        mLastY = y
        mLastXIntercept = x
        mLastYIntercept = y
        return intercepted
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        mVelocityTracker.addMovement(ev)
        val x = ev.x//相对于view的x坐标
        val y = ev.y
        when (ev.action.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                Log.e(TAG,"ACTION_DOWN--->")
                if (!mScroller.isFinished) {
                    mScroller.abortAnimation()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                Log.e(TAG,"ACTION_MOVE--->")
                val deltaX = x - mLastX
                val deltaY = y - mLastY
                //相对坐标滑动 屏幕上点击的点产生的位移是正的，意味着要是view内容从左往右滑动（负值）（与scroll接受的值刚好符合相反）
                scrollBy(-deltaX.toInt(), 0)
            }
            MotionEvent.ACTION_UP -> {
                Log.e(TAG,"ACTION_UP--->")
                val scrollX = scrollX
                val scrollToChildIndex = scrollX / mChildWidth
                mVelocityTracker.computeCurrentVelocity(1000)
                val xVelocity = mVelocityTracker.xVelocity//从左往右是正值
                Log.e(TAG,"xVelocity--->$xVelocity")
                if (xVelocity.absoluteValue >= 50) //像素
                {
                    mChildIndex = if (xVelocity > 0) mChildIndex - 1 else mChildIndex + 1
                } else {
                    mChildIndex = scrollX.plus(mChildWidth.div(2)).div(mChildWidth)
                }
                mChildIndex = max(0, min(mChildIndex, mChildrenSize - 1))
                val dx = mChildIndex.times(mChildWidth) - scrollX
                smoothScrollBy(dx, 0)
                mVelocityTracker.clear()
            }
            else -> {
            }
        }
        mLastX = x
        mLastY = y
        return true
    }

    private fun smoothScrollBy(dx: Int, dy: Int) {
        mScroller.startScroll(scrollX, 0, dx, 0, 500)//绝对滑动
        invalidate()
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)//绝对滑动
            postInvalidate()
        }
    }


    /**
     * viewgroup没有定义其测量的具体方法，因为viewgroup是一个抽象类
     * 其测量过程的onMeasure方法需要各个子类去具体实现；比如linearLayout等。
     * 当viewGroup被绘制时，就调用子类实现的onMeasure方法
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var measuredWidth = 0
        var measuredHeight = 0
        val childCount = childCount
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val widthSpaceSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpaceSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        if (childCount == 0) {
            setMeasuredDimension(0, 0)
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            val childView = getChildAt(0)
            measuredHeight = childView.measuredHeight
            setMeasuredDimension(widthSpaceSize, childView.measuredHeight)//设置view的测量值宽高
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            val childView = getChildAt(0)
            measuredWidth = childView.measuredWidth * childCount
            setMeasuredDimension(measuredWidth, heightSpaceSize)
        } else {
            val childView = getChildAt(0)
            measuredWidth = childView.measuredWidth * childCount
            measuredHeight = childView.measuredHeight
            setMeasuredDimension(measuredWidth, measuredHeight)
        }

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var childLeft = 0
        val childCount = childCount
        mChildrenSize = childCount

        for (i in 0 until childCount) {
            val childView: View = getChildAt(i)
            if (childView.visibility != View.GONE) {
                val childWidth: Int = childView.measuredWidth
                mChildWidth = childWidth
                childView.layout(
                    childLeft, 0, childLeft + childWidth,
                    childView.measuredHeight
                )
                childLeft += childWidth
            }
        }
    }

    override fun onDetachedFromWindow() {
        mVelocityTracker.recycle()
        super.onDetachedFromWindow()
    }
}