package com.dk.android_art.customview.builderpattern

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF

/**
 * @create on 2020/6/1 23:12
 * @description 胖人建造者
 * @author mrdonkey
 */
class PersonFatBuilder(private val paint: Paint, private val canvas: Canvas) :
    PersonBuilder(paint, canvas) {

    override fun buildHead() {
        canvas.drawCircle(500f, 200f, 100f, paint)//头部
    }

    override fun buildBody() {
        canvas.drawOval(RectF(400f,300f,600f,600f),paint)//胖身体
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