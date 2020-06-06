package com.dk.android_art.customview.builderpattern

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View


/**
 * @create on 2020/5/27 22:41
 * @description 构建小人
 * @author mrdonkey
 */
class PersonThinView(context: Context, attr: AttributeSet) : View(context, attr) {

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //新建画笔
        val paint = Paint().apply {
            isAntiAlias = true//设置抗锯齿
            style = Paint.Style.STROKE//空心
            strokeWidth = 2.0f//线条粗细
        }


        //直接
//        canvas.drawCircle(500f, 200f, 100f, paint)//头部
//        canvas.drawRect(RectF(450f,300f,550f,600f),paint)//瘦身体
//        canvas.drawLine(450f,300f,350f,600f,paint)//左手
//        canvas.drawLine(550f,300f,650f,600f,paint)//右手
//        canvas.drawLine(450f,600f,350f,900f,paint)//左脚
//        canvas.drawLine(550f,600f,650f,900f,paint)//右脚

//        //直接构建
        canvas.drawCircle(500f, 200f, 100f, paint)//头部
        canvas.drawOval(RectF(420f, 300f, 570f, 600f), paint)//胖身体
        canvas.drawLine(450f, 300f, 350f, 600f, paint)//左手
        canvas.drawLine(550f, 300f, 650f, 600f, paint)//右手
        canvas.drawLine(450f, 600f, 350f, 900f, paint)//左脚
//        canvas.drawLine(550f, 600f, 650f, 900f, paint)//右脚
//    }
//}
        //构建瘦人
        val personThinBuilder =
            PersonThinBuilder1(
                paint,
                canvas
            )
        personThinBuilder.build()
        //构建胖人
        val personFatBuilder =
            PersonFatBuilder1(
                paint,
                canvas
            )
        personFatBuilder.build()
//
//        //构造者模式建造瘦人
//        val personThinBuilder = PersonThinBuilder(paint, canvas)
//        val personDirector = PersonDirector(personThinBuilder)
//        personDirector.createPerson()
//        //构造者模式建造胖人
//        val personFatBuilder = PersonFatBuilder(paint, canvas)
//        val director = PersonDirector(personThinBuilder)
//        director.createPerson()

    }
}