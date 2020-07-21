package com.dk.android_art.drawable

import android.annotation.ColorInt
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import kotlin.math.max

/**
 * @create on 2020/7/20 21:56
 * @description 自定义Drawable 无法在XML中使用
 * @author mrdonkey
 */
class CustomDrawable(@ColorInt val color: Int) : Drawable() {

    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        mPaint.color = color
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds//获取drawable的实际区域，一般来说和它的View的尺寸相同
        val centerX = bounds.exactCenterX()
        val centerY = bounds.exactCenterX()
        canvas.drawCircle(centerX, centerY, max(centerX, centerY), mPaint)
    }

    /**
     * 设置透明度
     */
    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
        invalidateSelf()
    }


    /**
     * 不透明
     */
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }
}