package com.dk.android_art.customview


import android.content.Context
import android.graphics.PointF
import android.os.Parcelable
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.dk.android_art.R
import com.dk.android_art.customview.MultiCheckedLayout.ViewState.CHECKED
import com.dk.android_art.customview.MultiCheckedLayout.ViewState.NORMAL
import com.dk.android_art.utils.ReflectUtils
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * @create on 2020/5/28 20:15
 * @description 多选布局
 * @author mrdonkey
 */
class MultiCheckedLayout(ctx: Context, attrs: AttributeSet) : ConstraintLayout(ctx, attrs) {
    private val childType: Class<out AppCompatTextView> = CheckedTextView::class.java//填充的子view类型
    private val childViewCount = 14//填充数量
    private val checkedCount = 2//最多选中数
    private val rows = 3//几行
    private val columns = 5//几列
    private val rootId: Int by lazy { View.generateViewId() }//rootId
    private val childContainer: LinkedHashMap<Int, AppCompatTextView> by lazy {//维持当前容器中的view
        val container = LinkedHashMap<Int, AppCompatTextView>(childViewCount)
        repeat(childViewCount) { i ->
            val child = childType.getConstructor(Context::class.java, AttributeSet::class.java)
                .newInstance(ctx, attrs)
            val viewId = View.generateViewId()
            child.id = viewId
            container[viewId] = child
        }
        container
    }
    private val viewStates by lazy { //当前view的状态 [viewId,状态]
        childContainer.mapValues { NORMAL }.toMutableMap()
    }
    private val checkedLinkedList = LinkedList<CheckedView>()//维持选中view的队列


    init {
        setBackgroundColor(
            ContextCompat.getColor(context, R.color.colorAccent)
        )
        initRoot()
        initChildView()
    }

    /**
     * 初始化root
     */
    private fun initRoot() {
        this.id = rootId
    }

    /**
     * 动态添加child  view
     */
    private fun initChildView() {
        val constraintSet = ConstraintSet()
        val indicesArray =
            IntArray(rows.times(columns)) { 1 }.indices//根据行列创建一个 index 数组[0,1,2,3,4,5,6,7,8,9,10,11]
        val maxCountIndex =
            childViewCount.minus(1)//max count => index  最大填充数对应的index 是 count-1 假如count为8 那么对应的index就是 7
        val headAndTailPair =
            indicesArray//算出头尾的index，分别存在两个list中--([0, 5, 10], [4, 9, 11])---//--0-----1-----2-----3-----4---//--5-----6-----7-----8-----9---
                .filter { it.rem(columns) == 0 || it.plus(1).rem(columns) == 0 }//过滤
                .let {
                    if (it.size.rem(2) != 0)//如果数量不是成对的，添加maxCountIndex
                        it.plus(maxCountIndex)
                    else
                        it
                }
                .let { it ->
                    if (it.last() > maxCountIndex)//如果最后的值大于 maxCountIndex ，则 去掉最后的值，把maxCountIndex添加进来
                        it.dropLastWhile { it > maxCountIndex }.plus(maxCountIndex)
                    else
                        it
                }
                .partition { it.rem(columns) == 0 }//将满足取余数为0的放在first，其他不满足的放在second

        val headIndexList = headAndTailPair.first//[0, 5, 10]
        val tailIndexList = headAndTailPair.second//[4, 9, 11]

        repeat(headIndexList.size) { index ->//根据行数进行遍历
            for (i in headIndexList[index]..tailIndexList[index]) {
                val view = childContainer.values.elementAtOrNull(i)
                view?.apply {
                    val id = view.id
                    constraintSet.constrainWidth(id, LayoutParams.WRAP_CONTENT)
                    constraintSet.constrainHeight(id, LayoutParams.WRAP_CONTENT)
                    if (index > 0) {
                        constraintSet.setMargin(id, ConstraintSet.TOP, 8)
                        constraintSet.connect(
                            id,
                            ConstraintSet.TOP,
                            childContainer.keys.elementAt(i - columns),
                            ConstraintSet.BOTTOM
                        )
                        constraintSet.connect(
                            id,
                            ConstraintSet.LEFT,
                            childContainer.keys.elementAt(i - columns),
                            ConstraintSet.LEFT
                        )
                    } else {
                        constraintSet.connect(id, ConstraintSet.TOP, rootId, ConstraintSet.TOP)
                        constraintSet.createHorizontalChain(//只要第一行创建水平链 第二行对齐第一行的左边，第三行对齐第二行的左边 同理
                            rootId,
                            ConstraintSet.LEFT,
                            rootId,
                            ConstraintSet.RIGHT,
                            childContainer.keys.toIntArray()
                                .sliceArray(headIndexList[index]..tailIndexList[index]),
                            FloatArray(
                                tailIndexList[index].minus(headIndexList[index]).plus(1)
                            ) { 1.0f },
                            LayoutParams.CHAIN_SPREAD_INSIDE
                        )
                    }
                    addView(view)
                }
            }
        }

        TransitionManager.beginDelayedTransition(this)
        constraintSet.applyTo(this)
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        println("MultiCheckedLayout-----dispatchTouchEvent----")
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN -> {
                return handle(ev)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                return handle(ev)
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        println("MultiCheckedLayout-----onTouchEvent-${event?.action}")
        return super.onTouchEvent(event)
    }

    /**
     * 是否要拦截 判断点击的坐标是否在子View上，如果在则判断是否能否继续点
     */
    private fun handle(ev: MotionEvent): Boolean {
        childContainer.values.forEach { v ->
            if (v.pointInView(ev.getX(ev.actionIndex), ev.getY(ev.actionIndex))) {//点击的是子View
                val viewState = viewStates[v.id]
                handleAction(v.id, viewState)
            }
        }
        return true//全部拦截
    }

    /**
     * 处理这次行为
     * 1.判断当前选中队列是否等于最大选中数
     * 是-> 判断当前选择的view是否已经选中
     *      是->改变选中的view的状态，remove掉
     *      否->让选中队列的队首出队，将选中的插入队尾
     * 否->将当前选中的插入队尾
     *
     */
    private fun handleAction(
        viewId: Int,
        viewState: ViewState?
    ) {
        if (viewState?.isChecked() == true) {
            checkedLinkedList.remove(checkedLinkedList.find { it.id == viewId })//移除
            viewStates[viewId] = viewState.switch()
            (childContainer[viewId] as CheckedTextView).switchBackground()
        } else {
            if (checkedLinkedList.size == checkedCount) {
                checkedLinkedList.poll()?.apply {
                    (childContainer[id] as CheckedTextView).switchBackground()
                    viewStates[id] = state?.switch() ?: NORMAL
                }
            }
            viewStates[viewId] = viewState?.switch() ?: NORMAL
            (childContainer[viewId] as CheckedTextView).switchBackground()
            checkedLinkedList.offer(CheckedView(viewId, viewStates[viewId]))
        }

        println("----checkedLinkedList:$checkedLinkedList-----")
    }


    /**
     * 获取当前选中的view的id列表
     */
    fun getCheckedViewIds(): IntArray {
        return viewStates.filterValues { it.isChecked() }.keys.toIntArray()
    }

    /**
     * 判断给定的点是否在给定的view的空间坐标中 利用反射调用 ViewGroup的方法
     */
    private fun View.pointInView(x: Float, y: Float): Boolean {
        val objClass: Class<*> = ViewGroup::class.java
        val cls: Array<out Class<out Any>?> =
            arrayOf(Float::class.java, Float::class.java, View::class.java, PointF::class.java)
        val methodName = "isTransformedTouchPointInView"
        val targetMethod = ReflectUtils.getTargetMethod(objClass, methodName, *cls)
        return ReflectUtils.invokeTargetMethod<Boolean>(
            this@MultiCheckedLayout,
            targetMethod, x, y, this, null
        ) ?: false
    }


    /**
     * 子View的状态 包括[CHECKED] [NORMAL] 两种
     */
    enum class ViewState {
        CHECKED, NORMAL;

        /**
         * 切换方法 normal<=>checked
         */
        fun switch() = if (this == NORMAL) CHECKED else NORMAL

        /**
         * 是否被选中
         */
        fun isChecked() = this == CHECKED
    }

    /**
     * CheckedView的结构体，包括[id]与[state],用来维持当前选中的view队列
     */
    inner class CheckedView(var id: Int, var state: ViewState?) {
        override fun toString(): String {
            return "CheckedView(id=$id, state=$state)"
        }
    }

}