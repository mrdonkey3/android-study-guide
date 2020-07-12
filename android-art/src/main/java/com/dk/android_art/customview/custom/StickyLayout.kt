package com.dk.android_art.customview.custom

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout

/**
 * @create on 2020/7/11 18:09
 * @description 类似可以上下滑动的竖直linearLayout，内部分别放一个header和一个ListView(PinnedHeaderExpandableListView)，内外两层都可以滑动
 * 滑动规则：
 *        1. 当header显示时或者listView滑动到顶部吗，由StickyLayout拦截
 *        2. 当header隐藏时
 *           2-1 listView已经滑动的顶部，并且当前手势是向下滑动的话，由StickyLayout拦截
 *           2-2 其他情况由listView拦截
 * header：可以来回收展开、收缩：通过动态设置其layoutParams的height即可 展开/收缩时机：在onTouchEvent的Up的时候判断
 * @author mrdonkey
 */
class StickyLayout constructor(ctz: Context, attrs: AttributeSet?) : LinearLayout(ctz, attrs) {

    private var mHeader: View? = null//头部
    private var mContent: View? = null//内容

    private var mOriginalHeaderHeight: Int = 0;//原始header高度
    private var mHeaderHeight: Int = 0//当前header的高度

    //onTouchEvent
    private var mLastX = 0//上次滑动的坐标
    private var mLastY = 0//上次滑动的Y坐标

    //onInterceptorTouchEvent
    private var mLastXInterrupt = 0//上次滑动的坐标
    private var mLastYInterrupt = 0//上次滑动的Y坐标

    private var mTouchSlop: Int = 0//最小滑动距离

    private var mHeaderStatus = HeaderStatus.EXPANDED//header默认展开状态

    private var mChildGiveUpTouchEventListener: OnChildGiveUpTouchEventListener? =
        null//子元素不处理事件的逻辑由外层实现

    /**
     * 在这个方法里获取目标view的测量宽高
     * 同时进行初始化操作
     * 方法会多次进入
     */
    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        //保证只进入一次
        if (hasWindowFocus && mHeader == null && mContent == null) {
            initData()
        }
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        //获取子View的资源id
        val headerId = resources.getIdentifier("sticky_header", "id", context.packageName)
        val contentId = resources.getIdentifier("sticky_content", "id", context.packageName)
        //已经被加载进来
        if (headerId != 0 && contentId != 0) {
            mHeader = findViewById(headerId)
            mContent = findViewById(contentId)
            mOriginalHeaderHeight = mHeader?.measuredHeight ?: 0//记录原始高度
            mHeaderHeight = mOriginalHeaderHeight//记录header高度
            mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop//最小滑动距离
            println("initData--->mOriginalHeaderHeight:$mOriginalHeaderHeight")
        } else {
            throw NoSuchElementException("did you view with \"header\" or \"content\" exist? ")
        }
    }


    /**
     * 拦截
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercept = 0
        val x = ev.x.toInt()//相对坐标
        val y = ev.y.toInt()
        when (ev.action.and(MotionEvent.ACTION_MASK)) {
            //必须不拦截down事件
            //如果down拦截了，那么只后的事件都将交由此View处理，不再走onInterceptTouchEvent方法
            MotionEvent.ACTION_DOWN -> {
                mLastX = x
                mLastY = y
                mLastXInterrupt = x
                mLastYInterrupt = y
                intercept = 0
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = x - mLastXInterrupt//位移x
                val deltaY = y - mLastYInterrupt//位移y
                //如果header是展开状态，并且是向上滑动时，进行拦截
                //为什么向下滑动时不直接拦截？因为还有考虑子元素也是可以向下滑动的
                if (mHeaderStatus == HeaderStatus.EXPANDED && deltaY <= -mTouchSlop) {
                    intercept = 1
                } else if (mChildGiveUpTouchEventListener != null) {
                    //所以只有在content达到顶部时，向下滑动时进行拦截，让header显示出来
                    if (mChildGiveUpTouchEventListener?.giveUpTouchEvent(ev) == true && deltaY >= mTouchSlop) {
                        intercept = 1
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                //为什么mLastX与mLastY不被置零，因为在Move拦截后，在OnTouchEvent事件中需要对依据
                // 上次的坐标进行设置header的高
                mLastXInterrupt = 0
                mLastYInterrupt = 0
                intercept = 0
            }
            else -> {
                intercept = 0
            }
        }
        println("sticky:onInterceptTouchEvent--->action:${ev.action} intercept=${intercept != 0}")
        return intercept != 0
    }

    /**
     * 事件消费
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.x.toInt()//相对坐标
        val y = ev.y.toInt()
        println("sticky:onTouchEvent---> action:${ev.action} x:$x y:$y mLastY:$mLastY")
        when (ev.action.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                //nothing
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = x - mLastX
                val deltaY = y - mLastY
                println("sticky:onTouchEvent--->mHeaderHeight:$mHeaderHeight deltaY:$deltaY")
                mHeaderHeight += deltaY//header的高度加上位移为它新的高度（位移y：向上滑是负的，向下滑动时正的）
                // （正最大不得大于原始高度，不得小于0）
                setHeaderHeight(mHeaderHeight)
            }
            MotionEvent.ACTION_UP -> {
                //当手指松开，判断header是要自动展开还是收缩
                //dstHeight :header的最终高度
                val dstHeight =
                    if (mHeaderHeight <= mOriginalHeaderHeight.times(0.5)) {//当前header高度小于元素的一半
                        mHeaderStatus = HeaderStatus.COLLAPSED//收缩
                        0
                    } else {
                        mHeaderStatus = HeaderStatus.EXPANDED//展开
                        mOriginalHeaderHeight
                    }
                //自动慢慢地滑向终点高度
                smoothSetHeaderHeight(mHeaderHeight, dstHeight, 500L)
            }
        }
        mLastX = x
        mLastY = y
        return true
    }

    /**
     * 让header慢慢滑动到指定的高度
     * 将一次滑动分成几部分进行
     */
    private fun smoothSetHeaderHeight(from: Int, to: Int, duration: Long) {
        val frameCount = (duration / 1000 * 30).plus(1).toInt()//帧数 1s 30帧
        val partition = (to - from) / (frameCount).toFloat()//每帧滑动的距离
        object : Thread("Thread#smoothSetHeaderHeight") {
            override fun run() {
                repeat(frameCount) { i ->
                    val height: Int = if (i == frameCount - 1) {//最后一帧
                        to
                    } else {//慢慢增加高度
                        from.plus(partition.times(i)).toInt()
                    }
                    post { setHeaderHeight(height) }
                    kotlin.runCatching {
                        sleep(10)
                    }
                }
            }
        }.start()
    }

    /**
     * 设置header的高度
     */
    private fun setHeaderHeight(height: Int) {
        println("setHeaderHeight--->height:$height")
        val dstHeight = when {//最终高度
            height <= 0 -> 0
            height > mOriginalHeaderHeight -> mOriginalHeaderHeight
            else -> height
        }
        mHeaderStatus = if (dstHeight == 0) HeaderStatus.COLLAPSED else HeaderStatus.EXPANDED
        println("setHeaderHeight--->dstHeight:$dstHeight mHeaderHeight:$mHeaderHeight")
        if (mHeader != null && mHeader?.layoutParams != null) {
            mHeaderHeight = dstHeight
            mHeader?.layoutParams?.height = dstHeight
            mHeader?.requestLayout()//请求绘制
        }
    }


    fun getHeaderHeight(): Int = mHeaderHeight


    fun setOnChildGiveUpTouchEventListener(l: OnChildGiveUpTouchEventListener) {
        mChildGiveUpTouchEventListener = l
    }

    /**
     * 子元素不处理事件，交由外层来实现
     */
    interface OnChildGiveUpTouchEventListener {
        fun giveUpTouchEvent(event: MotionEvent): Boolean
    }

    /**
     * 头部的状态：展开、收缩
     */
    enum class HeaderStatus {
        EXPANDED,
        COLLAPSED;
    }

}