package com.dk.android_art.customview

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.dk.android_art.R

/**
 * @create on 2020/5/28 20:19
 * @description 可选择的textview
 * @author mrdonkey
 */
class CheckedTextView(ctx: Context, attrs: AttributeSet) : AppCompatTextView(ctx, attrs) {

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

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        println("CheckedTextView-----dispatchTouchEvent-${event?.action}--id:${this.id}")
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        println("CheckedTextView-----onTouchEvent-${event?.action}--id:${this.id}")
        return super.onTouchEvent(event)
    }

    /**
     * 切换背景
     */
    fun switchBackground(){
        val color = (background as ColorDrawable).color
        println("---switchBackground:$id---${color==normal}-")
        setBackgroundColor(if (color == normal) checked else normal)
    }

}