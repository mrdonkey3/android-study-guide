package com.dk.android_art.customview.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ExpandableListView

/**
 * @create on 2020/7/12 09:11
 * @description 首先继承自ExpandableListView，然后再它滚动的时候我们要监听顶部的item是属于哪个group的，
 * 当知道是哪个group以后，我们就在view的顶部绘制这个group，这样就完成了头部固定这个效果
 * @author mrdonkey
 */
class PinnedHeaderExpandableListView constructor(ctz: Context, attrs: AttributeSet?) :
    ExpandableListView(ctz, attrs), AbsListView.OnScrollListener {

    private var mGroupHeader: View? = null
    private var mGroupHeaderWidth = 0
    private var mGroupHeaderHeight = 0
    private var mScrollListener: OnScrollListener? = null
    private var mGroupHeaderUpdateListener: OnGroupHeaderUpdateListener? = null
    private var mActionDownHappened = false//记录是否发生的down事件
    private var mTouchTarget: View? = null
    protected var mIsHeaderGroupClickable = true

    init {
        initView()
    }

    private fun initView() {
        //fadingEdge属性用来设置拉滚动条时 ，边框渐变的放向。none（边框颜色不变），horizontal（水平方向颜色变淡），vertical（垂直方向颜色变淡）。
        //fadingEdgeLength用来设置边框渐变的长度
        fadingEdgeLength = 0//边缘褪色
        setOnScrollListener(this)
    }

    override fun setOnScrollListener(l: OnScrollListener?) {
        println("setOnScrollListener--->$l")
        mScrollListener = if (l != this) l else null
        println("setOnScrollListener--->current:$mScrollListener")
        super.setOnScrollListener(l)
    }

    /**
     * 设置header监听
     */
    fun setOnGroupHeaderUpdateListener(listener: OnGroupHeaderUpdateListener?) {
        mGroupHeaderUpdateListener = listener
        if (listener == null) return
        mGroupHeader = listener.getPinnedHeader()
        val firstVisiblePosition = firstVisiblePosition
        val firstVisibleGroupPosition =
            getPackedPositionGroup(getExpandableListPosition(firstVisiblePosition))
        listener.updatePinnedHeader(mGroupHeader, firstVisibleGroupPosition)
        requestLayout()
        postInvalidate()
    }

    fun setOnGroupClickListener(
        onGroupClickListener: OnGroupClickListener,
        isHeaderGroupClickable: Boolean
    ) {
        mIsHeaderGroupClickable = isHeaderGroupClickable
        super.setOnGroupClickListener(onGroupClickListener)
    }

    /**
     * 测量子元素
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mGroupHeader == null) return
        measureChild(mGroupHeader, widthMeasureSpec, heightMeasureSpec)
        //获取上述更新的测量宽高
        mGroupHeaderWidth = mGroupHeader?.measuredWidth ?: 0
        mGroupHeaderHeight = mGroupHeader?.measuredHeight ?: 0
    }


    /**
     * onLayout确认所有子元素的位置；得到最终的宽/高
     * layout方法时确定view本身的位置
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if (mGroupHeader == null) return
        //确定header的位置
        mGroupHeader?.layout(0, 0, mGroupHeaderWidth, mGroupHeaderHeight)
    }

    /**
     * 绘制子元素：header
     */
    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        if (mGroupHeader == null) return
        drawChild(canvas, mGroupHeader, drawingTime)
    }

    /**
     * 点击事件分发
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val x = ev.x.toInt()
        val y = ev.y.toInt()
        println("listView:dispatchTouchEvent--->${ev.action}")
        val pos = pointToPosition(x, y)//判断点击的事件在那个pos上，不是则返回-1
        mGroupHeader?.also { header ->
            //如果点击的y在header上
            if (y > header.top && y <= header.bottom) {
                when (ev.action.and(MotionEvent.ACTION_MASK)) {
                    MotionEvent.ACTION_DOWN -> {
                        mTouchTarget = getTouchTarget(header, x, y)
                        mActionDownHappened = true
                    }
                    MotionEvent.ACTION_UP -> {
                        val touchTarget = getTouchTarget(header, x, y)
                        if (touchTarget == mTouchTarget && touchTarget.isClickable) {
                            touchTarget.performClick()
                            invalidate(Rect(0, 0, mGroupHeaderWidth, mGroupHeaderHeight))
                        } else if (mIsHeaderGroupClickable) {
                            //拿到group 的位置
                            val groupPos = getPackedPositionGroup(getExpandableListPosition(pos))
                            //点击group判断是收缩还是展开
                            if (groupPos != AdapterView.INVALID_POSITION && mActionDownHappened) {
                                if (isGroupExpanded(groupPos)) {
                                    collapseGroup(groupPos)
                                } else {
                                    expandGroup(groupPos)
                                }
                                mActionDownHappened = false
                            }
                        }
                    }
                    else -> {
                    }

                }
                //return false //若是down返回false，其他action就不会收到
                return true//返回true代表被消费了，事件不会再进行传递
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        println("listView:onTouchEvent--->${ev?.action}")
        return super.onTouchEvent(ev)
    }

    private fun getTouchTarget(view: View, x: Int, y: Int): View {
        if (view !is ViewGroup) {
            return view
        }
        val parent = view//group.xml里的relativelayout
        val childrenCount = parent.childCount
        val customOrder = isChildrenDrawingOrderEnabled
        var target: View? = null
        for (i in childrenCount - 1 downTo 0) {
            val childIndex = if (customOrder) getChildDrawingOrder(childrenCount, i) else i
            val child = parent.getChildAt(childIndex)
            if (isTouchPointInView(child, x, y)) {
                target = child
                break
            }
        }
        if (target == null) {
            target = parent
        }
        return target
    }

    private fun isTouchPointInView(view: View, x: Int, y: Int): Boolean {
        return view.isClickable && y >= view.top && y <= view.bottom && x >= view.left && x <= view.right
    }

    fun requestRefreshHeader() {
        refreshGroupHeader()
        invalidate(Rect(0, 0, mGroupHeaderWidth, mGroupHeaderHeight))
    }

    /**
     * 刷新头部
     */
    protected fun refreshGroupHeader() {
        if (mGroupHeader == null) return
        val firstPos = firstVisiblePosition
        val nextPos = firstVisiblePosition.plus(1)
        val firstVisibleGroupPos = getPackedPositionGroup(getExpandableListPosition(firstPos))
        val nextVisibleGroupPos = getPackedPositionGroup(getExpandableListPosition(nextPos))
        //第一个可见的item所属的group不等于下一个item所属的group，意味着下一个group已经滑动到first的底部了
        if (nextVisibleGroupPos == firstVisibleGroupPos + 1) {
            val view = getChildAt(1)//expandableListView
            //当listView的顶部进入到mGroupHeader的范围时，进行绘制
            if (view.top <= mGroupHeaderHeight) {
                //向上滑动时，viewtop趋近于0
                val deltaY = mGroupHeaderHeight - view.top
                //让mGroupHeader位置往上移
                mGroupHeader?.layout(0, -deltaY, mGroupHeaderWidth, mGroupHeaderHeight)
            }
        } else {
            //重新确定view的位置
            mGroupHeader?.layout(0, 0, mGroupHeaderWidth, mGroupHeaderHeight)
        }

        mGroupHeaderUpdateListener?.updatePinnedHeader(mGroupHeader, firstVisibleGroupPos)
    }

    /**
     * SCROLL_STATE_TOUCH_SCROLL：开始滚动的时候调用，调用一次
     * SCROLL_STATE_IDLE：滚动事件结束的时候调用，调用一次
     * SCROLL_STATE_FLING：当手指离开屏幕，并且产生惯性滑动的时候调用，可能会调用<=1次
     */
    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
        //滚动事件停止时 此滑动是listView的
        if (mGroupHeader != null && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            val firstVisiblePosition = firstVisiblePosition
            if (firstVisiblePosition == 0) {
                mGroupHeader?.layout(0, 0, mGroupHeaderWidth, mGroupHeaderHeight)
            }
        }
        mScrollListener?.onScrollStateChanged(view, scrollState)
    }


    /**
     * 在滑动屏幕的过程中，onScroll方法会一直调用：
     * firstVisibleItem： 当前屏幕显示的第一个item的位置（下标从0开始）
     * visibleItemCount：当前屏幕可以见到的item总数，包括没有完整显示的item
     * totalItemCount：Item的总数，** 包括通过addFooterView添加的那个item
     **/
    override fun onScroll(
        view: AbsListView?,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {
        if (totalItemCount > 0) {
            refreshGroupHeader()
        }
        mScrollListener?.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount)

    }

    interface OnGroupHeaderUpdateListener {
        /**
         * 单例，获取header(实际是覆盖在上层的view)
         * 注意：view必须有layoutparams
         */
        fun getPinnedHeader(): View

        /**
         * 更新固定的header
         */
        fun updatePinnedHeader(mHeader: View?, firstVisibleGroupPos: Int)
    }
}