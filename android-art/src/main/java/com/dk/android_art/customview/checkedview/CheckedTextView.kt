package com.dk.android_art.customview.checkedview

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.dk.android_art.R
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        println("CheckedTextView-----onTouchEvent-${event?.action}--id:${this.id}")
        return super.onTouchEvent(event)
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
        checkedViewState = checkedViewState.switch()//切换状态
    }

    override fun setDefaultText(text: String) {
        setText(text)
    }

}