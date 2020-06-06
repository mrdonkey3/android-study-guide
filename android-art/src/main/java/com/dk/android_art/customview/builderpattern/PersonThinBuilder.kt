package com.dk.android_art.customview.builderpattern

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

/**
 * @create on 2020/6/1 23:12
 * @description 瘦人建造者，需要构建一个小人，必须要继承[PersonBuilder],必须重写这些抽象方法，否则编译不通过
 * @author mrdonkey
 */
class PersonThinBuilder(private val paint: Paint, private val canvas: Canvas) :
    PersonBuilder(paint, canvas) {

    override fun buildHead() {
        canvas.drawCircle(500f, 200f, 100f, paint)//头部
    }

    override fun buildBody() {
        canvas.drawRect(RectF(450f, 300f, 550f, 600f), paint)//瘦身体
    }

    override fun buildArmLeft() {
        canvas.drawLine(450f, 300f, 350f, 600f, paint)//左手
    }

    override fun buildArmRight() {
        canvas.drawLine(550f, 300f, 650f, 600f, paint)//右手
    }

    override fun buildLegLeft() {
        canvas.drawLine(450f, 600f, 350f, 900f, paint)//左脚
    }

    override fun buildLegRight() {
        canvas.drawLine(550f, 600f, 650f, 900f, paint)//右脚
    }

}