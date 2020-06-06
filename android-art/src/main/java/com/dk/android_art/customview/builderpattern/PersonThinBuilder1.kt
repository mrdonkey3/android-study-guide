package com.dk.android_art.customview.builderpattern

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

/**
 * @create on 2020/6/1 22:58
 * @description 构建小人二
 * @author mrdonkey
 */
class PersonThinBuilder1 constructor(private val paint: Paint, private val canvas: Canvas) {
    /**
     * 建造方法
     */
    fun build(){
        canvas.drawCircle(500f, 200f, 100f, paint)//头部
        canvas.drawRect(RectF(450f,300f,550f,600f),paint)//瘦身体
        canvas.drawLine(450f,300f,350f,600f,paint)//左手
        canvas.drawLine(550f,300f,650f,600f,paint)//右手
        canvas.drawLine(450f,600f,350f,900f,paint)//左脚
        canvas.drawLine(550f,600f,650f,900f,paint)//右脚
    }
}