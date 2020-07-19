package com.dk.android_art.customview.checkedview


import android.content.Context
import android.graphics.PointF
import android.transition.TransitionManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.dk.android_art.R
import com.dk.android_art.utils.ViewUtils
import com.dk.android_art.utils.ViewUtils.Companion.isTouchPointInView
import com.dk.common.utils.ReflectUtils
import com.teligen.litedevice.ui.widget.CheckedView
import com.teligen.litedevice.ui.widget.CheckedViewFactory
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * @create on 2020/5/28 20:15
 * @description 多选布局
 * @author mrdonkey
 */
class MultiCheckedLayout(ctx: Context, attrs: AttributeSet) : ConstraintLayout(ctx, attrs) {
    private var childType: Class<out CheckedView> = CheckedTextView::class.java //子view类型
    private var childMatchConstraint = false//子view是否需要占满剩余空间,默认wrap_content
    private val childViewCount = 3//填充数量
    private val checkedCount = 1//最多选中数
    private val rows = 1//几行
    private val columns = 3//几列
    private val rootId: Int by lazy { id }//rootId ，这个id是通过布局设置的
    private val childContainer: LinkedHashMap<Int, CheckedView> by lazy {//维持当前容器中的view
        val container = LinkedHashMap<Int, CheckedView>(childViewCount)
        repeat(childViewCount) { i ->
            val child = childType.getConstructor(Context::class.java, AttributeSet::class.java)
                .newInstance(ctx, attrs)
            val viewId = View.generateViewId()
            child.setViewId(viewId)
            container[viewId] = child
        }
        container
    }
    private val checkedLinkedList = LinkedList<CheckedView>()//维持选中view的队列


    init {
        ctx.obtainStyledAttributes(attrs, R.styleable.MultiCheckedLayout).apply {
            childType =
                CheckedViewFactory.create(getInteger(R.styleable.MultiCheckedLayout_childType, 0))
            childMatchConstraint =
                getBoolean(R.styleable.MultiCheckedLayout_child_match_constraint, false)
            recycle()
        }
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
            indicesArray//算出头尾的index，分别存在两个list中--([0, 5, 10], [4, 9, 11])---//--0-----1-----2-----3-----4---//--5-----6-----7-----8-----9---//--10----11
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

        repeat(headIndexList.size) { row ->//根据行数进行遍历
            for (i in headIndexList[row]..tailIndexList[row]) {
                val view = childContainer.values.elementAtOrNull(i)
                view?.apply {
                    val id = view.getViewId()
                    constraintSet.constrainWidth(id,if (childMatchConstraint) LayoutParams.MATCH_CONSTRAINT else LayoutParams.WRAP_CONTENT)
                    constraintSet.constrainHeight(id, LayoutParams.WRAP_CONTENT)
                    constraintSet.setMargin(id, ConstraintSet.BOTTOM, 8)//marginBottom
                    constraintSet.setMargin(id, ConstraintSet.END, 8)//marginTop
                    constraintSet.setMargin(id, ConstraintSet.TOP, 8)//marginTop
                    if (row > 0) {//如果行数大于1[0,1]
                        constraintSet.connect(//topToBottom
                            id,
                            ConstraintSet.TOP,
                            childContainer.keys.elementAt(i - columns),
                            ConstraintSet.BOTTOM
                        )
                        constraintSet.connect(//LeftToLeft
                            id,
                            ConstraintSet.LEFT,
                            childContainer.keys.elementAt(i - columns),
                            ConstraintSet.LEFT
                        )
                        if (rows.minus(1) == row)
                            constraintSet.connect(
                                id,
                                ConstraintSet.BOTTOM,
                                rootId,
                                ConstraintSet.BOTTOM
                            )//BottomToBottom

                    } else {//第0行时，做的事情
                        constraintSet.setMargin(id, ConstraintSet.START, 8)//marginTop
                        val ids = childContainer.keys.toIntArray()
                            .sliceArray(headIndexList[row]..tailIndexList[row])//截取指定下标区间id集合
                        val weights =
                            FloatArray(
                                tailIndexList[row].minus(headIndexList[row]).plus(1)
                            ) { 1.0f }//对应ids的weight数组
                        constraintSet.connect(
                            id,
                            ConstraintSet.TOP,
                            rootId,
                            ConstraintSet.TOP
                        )//TopToTop
                        if (rows == 1)
                            constraintSet.connect(
                                id,
                                ConstraintSet.BOTTOM,
                                rootId,
                                ConstraintSet.BOTTOM
                            )//BottomToBottom

                        constraintSet.createHorizontalChain(//只要第一行创建水平链 第二行对齐第一行的左边，第三行对齐第二行的左边 同理
                            rootId,
                            ConstraintSet.LEFT,
                            rootId,
                            ConstraintSet.RIGHT,
                            ids,//截取指定下标区间的 ids元素集合
                            weights,//每个id对应的weight集合 与上面要一致
                            LayoutParams.CHAIN_SPREAD_INSIDE
                        )
                    }
                    if (view is View) {
                        addView(view)
                    }
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
//        when (ev.action.and(MotionEvent.ACTION_MASK)) {
//            MotionEvent.ACTION_DOWN -> {
//                return handle(ev)
//            }
//            MotionEvent.ACTION_POINTER_DOWN -> {
//                return handle(ev)
//            }
//        }
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
            if (isTouchPointInView(v as CheckedTextView,ev.rawX, ev.rawY)) {//点击的是子View
//            if (v.pointInView(ev.getX(ev.actionIndex), ev.getY(ev.actionIndex))) {//点击的是子View
                val viewState = v.checkedViewState
                handleAction(v.getViewId(), viewState)
            }
        }
        return false//全部不拦截
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
        viewState: CheckedViewState?
    ) {
        if (viewState?.isChecked() == true) {
            checkedLinkedList.remove(checkedLinkedList.find { it.getViewId() == viewId })//移除
            childContainer[viewId]?.toggle()
        } else {
            if (checkedLinkedList.size == checkedCount) {
                checkedLinkedList.poll()?.apply {
                    childContainer[getViewId()]?.toggle()
                }
            }
            childContainer[viewId]?.apply {
                toggle()
                checkedLinkedList.offer(this)
            }
        }
        println("----checkedLinkedList:$checkedLinkedList-----")
    }


    /**
     * 设置预测方法
     * 可以将具体使用者需要预测的方法传入到每个子view，子view可以通过传递相应参数，调用使用者的方法
     * 例如：如果使用者关心子view的监听事件，需要关心子view的监听获取到的参数，那么在子view触发监听时，传入参数到这这个预测方法中，
     * 实际就是调用了使用者提供的预测方法，使用者拿到参数具体怎么操作子view不关心，只关心预测的结果，子view根据结果做出相应的操作。
     */
    fun setPredicate(predicate: (String) -> Boolean): MultiCheckedLayout {
        childContainer.values.forEach {
            it.predicate = predicate
        }
        return this
    }

    /**
     * 设置提示内容
     */
    fun setPrompt(prompt: String): MultiCheckedLayout {
        childContainer.values.forEach {
            it.prompt = prompt
        }
        return this
    }

    /**
     * 获取选中状态的View的ids
     */
    fun getCheckedViewIds(): IntArray {
        return childContainer.values.filter { it.checkedViewState == CheckedViewState.CHECKED }
            .map { it.getViewId() }
            .toIntArray()
    }


    /**
     * 获取子view的data
     */
    fun <T : CheckedView.Entity> getChildViewData(): List<T?> {
        return childContainer.values.map { it.viewData<T>() }
    }

    /**
     * 判断给定的点是否在给定的view的空间坐标中 利用反射调用 ViewGroup的方法
     */
    private fun CheckedView.pointInView(x: Float, y: Float): Boolean {
        val objClass: Class<*> = ViewGroup::class.java
        val cls: Array<out Class<out Any>?> =
            arrayOf(Float::class.java, Float::class.java, View::class.java, PointF::class.java)
        val methodName = "isTransformedTouchPointInView"
        val targetMethod = ReflectUtils.getTargetMethod(objClass, methodName, *cls)
        return ReflectUtils.invokeTargetMethod<Boolean>(
            this@MultiCheckedLayout,
            targetMethod, x, y, this as View, null
        ) ?: false
    }

    /**
     * 子view状态的枚举类
     */
    enum class CheckedViewState {
        CHECKED, NORMAL;

        //checked<=>normal
        fun switch(): CheckedViewState {
            return if (this == NORMAL) CHECKED else NORMAL
        }

        fun isChecked() = this == CHECKED
    }

    enum class CheckedViewType {
        TEXT, EDIT
    }


//    /**
//     * 重写此方法， 给添加进来的子 view 添加默认的 布局参数 在 addView时调用
//     *  LayoutParams 的作用是：子控件告诉父控件，自己要如何布局。
//     *  不重写，w & h 默认生成 wrap content的
//     */
//    override fun generateDefaultLayoutParams(): LayoutParams {
//        return LayoutParams(
//            LayoutParams.MATCH_PARENT,
//            LayoutParams.WRAP_CONTENT
//        )
//    }


}