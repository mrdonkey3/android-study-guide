package com.dk.android_art.customview.checkedview

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.dk.android_art.R
import com.dk.android_art.utils.ViewUtils
import com.teligen.litedevice.ui.widget.CheckedView

/**
 * @create on 2020/5/28 20:19
 * @description 可选择的textview
 * @author mrdonkey
 */
class CheckedTextView(ctx: Context, attrs: AttributeSet) : AppCompatTextView(ctx, attrs),
    CheckedView {

    override var checkedViewState: MultiCheckedLayout.CheckedViewState =
        MultiCheckedLayout.CheckedViewState.NORMAL

    override var predicate: (arg: String) -> Boolean = { false }
    override var prompt: String = ""

    private val normal = ContextCompat.getColor(context, R.color.colorPrimary)
    private val checked = ContextCompat.getColor(context, R.color.colorAccent)

    private var consumeDownEvent = false//是否消费down事件
    private var moveOutOfViewBounds = false//是move事件在view的边界内
    private var locOnScreen = FloatArray(4)

    init {
        text = "View"
        setBackgroundColor(normal)
        setPadding(50, 50, 50, 50)
//        setOnClickListener {
//            println("---click:${it.id}----")
//            val color = (it.background as ColorDrawable).color
//            setBackgroundColor(if (color == normal) checked else normal)
//        }
    }


    override fun setViewId(viewId: Int) {
        id = viewId
    }

    override fun getViewId() = id

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        println("CheckedTextView-----dispatchTouchEvent-${event?.action}--id:${this.id}")
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        println("CheckedTextView-----onTouchEvent-${event.action}--action:(${event.action})id:${this.id}---index:(${event.actionIndex})----count:(${event.pointerCount}--${event}")
        when (event.action.and(MotionEvent.ACTION_MASK)) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                println("ACTION_DOWN---->${event.rawX},${event.rawY}")
                return handle(event.rawX, event.rawY)
            }
            MotionEvent.ACTION_MOVE -> {
                //判断是否移出view的边界
                if (!moveOutOfViewBounds) {
                    val touchPointInView = isTouchPointInView(event.rawX, event.rawY)
                    if (!touchPointInView) {
                        moveOutOfViewBounds = true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                println("ACTION_UP---->${event.x},${event.y}")
                println("ACTION_UP---->${event.rawX},${event.rawY}")
                val touchPointInView = isTouchPointInView(event.rawX, event.rawY)
                if (consumeDownEvent && !moveOutOfViewBounds && touchPointInView) {
                    toggle()
                }
                consumeDownEvent = false
                moveOutOfViewBounds = false
            }
            else -> return false
        }
        return false
    }

    private fun handle(x: Float, y: Float): Boolean {
        val touchPointInView = ViewUtils.isTouchPointInView(this, x, y)
        if (touchPointInView) {
            consumeDownEvent = true
            moveOutOfViewBounds = false
            return true
        }
        return false
    }


    /**
     * 使normal的配置无效
     */
    private fun invalidateNormal() {
        setBackgroundColor(checked)
    }


    /**
     * 使选中状态的配置无效
     */
    private fun invalidateChecked() {
        setBackgroundColor(normal)
    }

    /**
     * 是否选中，暴露给外面获取状态
     */
    fun isChecked() = (background as ColorDrawable).color != normal

    /**
     * 切换背景
     */
    override fun toggle() {
        if (isChecked()) {
            invalidateChecked()
        } else {
            invalidateNormal()
        }
        println("--->$parent  ${parent is MultiCheckedLayout}")
        checkedViewState = checkedViewState.switch()//切换状态
    }

    override fun setDefaultText(text: String) {
        setText(text)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val location = locationOnScreen
        val l = location[0].toFloat()
        val t = location[1].toFloat()
        val r = l.plus(measuredWidth).toFloat()
        val b = t.plus(measuredHeight).toFloat()
        //指定左上角和右下角的坐标确定view的矩形区域
        //判断（x,y）是否在view的矩形区域内
        println("--->id:$id hw:${measuredWidth} $measuredHeight $height ($l,$t,$r,$b) ")
        locOnScreen[0] = l
        locOnScreen[1] = t
        locOnScreen[2] = r
        locOnScreen[3] = b

    }

    fun isTouchPointInView(x: Float, y: Float): Boolean {
        println("--->$left,$top,$right,$bottom ")
        return y >= locOnScreen[1] && y <= locOnScreen[3] && x >= locOnScreen[0] && x <= locOnScreen[2]
    }


}