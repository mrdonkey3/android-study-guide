package com.dk.android_art.customview.builderpattern

import android.graphics.Canvas
import android.graphics.Paint

/**
 * @create on 2020/6/1 23:08
 * @description 抽象建造人的类 [paint]画笔 [canvas]画布
 * @author mrdonkey
 */
abstract class PersonBuilder(private val paint: Paint, private val canvas: Canvas) {

    abstract fun buildHead()//画头

    abstract fun buildBody()//画身体

    abstract fun buildArmLeft()//画左手

    abstract fun buildArmRight()//画右手

    abstract fun buildLegLeft()//画左脚

    abstract fun buildLegRight()//画右脚

}